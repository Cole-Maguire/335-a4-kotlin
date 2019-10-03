package com.cmag704.CompSci335A1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory


const val BASEURL = "http://redsox.uoa.auckland.ac.nz/ms/MuseumService.svc"
const val BASEURL_SECURE = "http://redsox.uoa.auckland.ac.nz/mss/Service.svc"

suspend fun getImage(image_url: String): Bitmap? {
    val url = URL(image_url)
    print(url)
    return BitmapFactory.decodeStream(url.openConnection().getInputStream())
}

fun readXml(xmlString: String): Document {
    val dbFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    val xmlInput = InputSource(StringReader(xmlString))

    return dBuilder.parse(xmlInput)
}

suspend fun getNews(news: ArrayList<News>) {
    parseNewsXmlString(getRequest("$BASEURL/news"), news)
}

suspend fun getDisplays(displays: ArrayList<Display>) {
    parseDisplaysXmlString(getRequest("$BASEURL/items"), displays)
}

suspend fun getShopItems(shopItems: ArrayList<ShopItem>) {
    parseShopXmlString(getRequest("$BASEURL/shop?term="), shopItems)
}

fun parseDisplaysXmlString(xmlString: String, displays: ArrayList<Display>) {
    val document = readXml(xmlString)

    val nodeList = document.getElementsByTagName("Item")
    for (i in 0 until nodeList.length) {
        displays.add(
            Display(
                // Semantic ordering is yucky, but whatever
                nodeList.item(i).childNodes.item(0).textContent,
                nodeList.item(i).childNodes.item(1).textContent,
                nodeList.item(i).childNodes.item(2).textContent
            )
        )
    }
}

fun parseNewsXmlString(xmlString: String, news: ArrayList<News>) {
    val document = readXml(xmlString)

    val nodeList = document.getElementsByTagName("RSSItem")
    for (i in 0 until nodeList.length) {
        val el = nodeList.item(i) as Element
        news.add(
            News(
                el.getElementsByTagName("descriptionField").item(0).textContent,
                el.getElementsByTagName("urlField").item(0).textContent,
                el.getElementsByTagName("linkField").item(0).textContent,
                el.getElementsByTagName("pubDateField").item(0).textContent,
                el.getElementsByTagName("titleField").item(0).textContent
            )
        )
    }
}

fun parseShopXmlString(xmlString: String, shopItems: ArrayList<ShopItem>) {
    val document = readXml(xmlString)

    val nodeList = document.getElementsByTagName("Item")
    for (i in 0 until nodeList.length) {
        val el = nodeList.item(i) as Element
        shopItems.add(
            ShopItem(
                el.getElementsByTagName("Description").item(0).textContent,
                el.getElementsByTagName("ItemId").item(0).textContent,
                el.getElementsByTagName("Title").item(0).textContent
            )
        )
    }
}

fun postCommentRequest(name: String, comment: String): String? {
    val safeName = URLEncoder.encode(name, "UTF-8")
    val mURL = URL("$BASEURL/comment?name=$safeName")

    with(mURL.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json; charset=utf-8")
        val commentUtf = "\"$comment\"".toByteArray(Charsets.UTF_8)
        //set the content length of the body
        setRequestProperty("Content-length", commentUtf.size.toString())

        doInput = true
        doOutput = true
        useCaches = false
        outputStream.write(commentUtf)
        outputStream.close()
        outputStream.flush()

        println("URL : $url")
        println("Response Code : $responseCode")

        if (responseCode != 200) {
            return "$responseCode : ${inputStream.bufferedReader().readText()}"
        }
    }
    return null
}

fun MD5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(input.toByteArray()).joinToString("") {
        //adapted from 'https://code.luasoftware.com/tutorials/android/android-string-to-md5/'
        //I think this is converting to ascii, but I can't be bothered delving deeper when it works well
        String.format("%02x", it)
    }
}


enum class LogFiles(val filename: String) {
    HA1("HA1"),
    USERNAME("username")
}

fun isLoggedIn(context: Context): Boolean {
    //Temporary, till I implement login
    return File(context.cacheDir, "username").isFile and File(context.cacheDir, "HA1").isFile

}

fun getFileContents(context: Context, file: LogFiles): String {
    return File(context.cacheDir, file.filename).readText()
}

fun writeFileContents(context: Context, file: LogFiles, contents: String) {
    File(context.cacheDir, file.filename).outputStream().use {
        it.write(contents.toByteArray())
    }
}

fun clearCache(context: Context) {
    val cacheDir = context.cacheDir

    val files = cacheDir.listFiles()

    if (files != null) {
        for (file in files!!)
            file.delete()
    }
}

fun secureRequest(
    endpoint: String,
    username: String,
    password: String? = null,
    ha1In: String? = null
): Triple<Int, String, String> {
// Must provide either username/password OR a md5ed equivalent
// Returns (responseCode, responseText, ha1)

    val mURL = URL("$BASEURL_SECURE/$endpoint")
    val digestURI = "/Service.svc/$endpoint"

    // Initial request to get nonce, opaque etc.
    val (realm, nonce, opaque) = with(URL("$BASEURL_SECURE/id").openConnection() as HttpURLConnection) {
        val re = Regex("Digest realm=\"([^\"]*)\", nonce=\"([^\"]*)\", opaque=\"([^\"]*)\"$")
        return@with re.matchEntire(getHeaderField("WWW-Authenticate"))!!.destructured
    }

    val ha1 = ha1In ?: MD5("$username:$realm:$password")
    val ha2 = MD5("GET:$digestURI")
    val response = MD5("$ha1:$nonce:$ha2")

    with(mURL.openConnection() as HttpURLConnection) {
        setRequestProperty(
            "Authorization",
            "Digest username=\"$username\", realm=\"$realm\", nonce=\"$nonce\", uri=\"$digestURI\", response=\"$response\", opaque=\"$opaque\""
        )
        val data = inputStream.bufferedReader().readText()

        return Triple(responseCode, data, ha1)
    }
}

fun checkAndLog(context: Context, username: String, password: String, button: Button) {
    // A whole heap of nested scopes and callbacks is not my ideal architecture
    // But I don't want to spend even more time on this, and it works

    GlobalScope.launch {
        val (code, message, ha1) = secureRequest(
            "id",
            username = username,
            password = password
        )
        val toast = if (code == 200) {
            writeFileContents(context, LogFiles.USERNAME, username)
            writeFileContents(context, LogFiles.HA1, ha1)
            withContext(Dispatchers.Main) {
                button.text = "Logout"
            }

            "Succesfully logged in!"

        } else {
            message
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                toast,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

fun getRequest(url: String): String {
    val mURL = URL(url)

    with(mURL.openConnection() as HttpURLConnection) {
        return inputStream.bufferedReader().readText()
    }

}

