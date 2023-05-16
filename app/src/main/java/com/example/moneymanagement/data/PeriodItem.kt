package com.example.moneymanagement.data

data class PeriodItem(
    var uid: String = "",
    var year: String = "",
    var month: String = "",
    var description: String = "",
    var date: String = "",
)

data class PeriodWithId(
    val periodId: String,
    val period: PeriodItem
)