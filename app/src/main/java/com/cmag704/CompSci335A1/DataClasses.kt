package com.cmag704.CompSci335A1

import java.util.*

//This is the order provided in the xml
data class Display(val description: String, val itemId: String, val title: String)

data class News(val description: String, val imageUrl: String, val storyUrl: String, var date: String, var title: String)

data class ShopItem(val description: String, val itemId: String, var title: String)