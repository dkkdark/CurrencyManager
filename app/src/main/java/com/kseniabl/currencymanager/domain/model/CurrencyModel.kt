package com.kseniabl.currencymanager.domain.model

data class CurrencyModel(
    val id: String,
    val numCode: Int,
    val charCode: String,
    val nominal: Int,
    val name: String,
    val value: Float,
    val previous: Float
)