package com.example.moneymanagement.data

data class ListItem(
    var uid: String = "",
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var date: String = "",
    var category: String = "",
    var periodYear: String = "",
    var periodMonth: String = "",
)

data class ListWithId(
    val itemId: String,
    val item: ListItem
)