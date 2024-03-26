package com.kseniabl.currencymanager.data.model

import com.google.gson.annotations.SerializedName

data class ValuteResponse(
    @SerializedName("Valute")
    val valute: Map<String, CurrencyResponse>
)