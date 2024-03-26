package com.kseniabl.currencymanager.data.model

import com.google.gson.annotations.SerializedName
import com.kseniabl.currencymanager.data.model.CurrencyResponse

data class ValuteResponse(
    @SerializedName("Valute")
    val valute: Map<String, CurrencyResponse>
)