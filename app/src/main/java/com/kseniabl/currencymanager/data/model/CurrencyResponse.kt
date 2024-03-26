package com.kseniabl.currencymanager.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("ID")
    val id: String,
    @SerializedName("NumCode")
    val numCode: Int,
    @SerializedName("CharCode")
    val charCode: String,
    @SerializedName("Nominal")
    val nominal: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Value")
    val value: Float,
    @SerializedName("Previous")
    val previous: Float
)
