package com.kseniabl.currencymanager.dependency

import com.kseniabl.currencymanager.data.source.RemoteSource
import com.kseniabl.currencymanager.data.repository.RepositoryImpl
import com.kseniabl.currencymanager.domain.repository.Repository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SourceModule {

    fun provideApiURL(): String = "https://www.cbr-xml-daily.ru/"

    fun provideRetrofit(
        baseUrl: String = provideApiURL()
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    fun provideApi(
        retrofit: Retrofit = provideRetrofit()
    ): RemoteSource = retrofit.create(RemoteSource::class.java)

    fun provideApiCallsRepository(
        source: RemoteSource = provideApi()
    ): Repository =
        RepositoryImpl(source)

}