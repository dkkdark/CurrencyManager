package com.kseniabl.currencymanager.data.source

import com.kseniabl.currencymanager.data.model.ValuteResponse
import retrofit2.Response
import retrofit2.http.GET

interface RemoteSource {

    @GET("/daily_json.js")
    suspend fun getCurrencies(): Response<ValuteResponse>
}