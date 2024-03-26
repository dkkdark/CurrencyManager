package com.kseniabl.currencymanager.data.repository

import com.kseniabl.currencymanager.data.source.RemoteSource
import com.kseniabl.currencymanager.dependency.SourceModule
import com.kseniabl.currencymanager.domain.model.ResponseError
import com.kseniabl.currencymanager.domain.model.ResultModel
import com.kseniabl.currencymanager.domain.mapper.toCurrencyModelList
import com.kseniabl.currencymanager.domain.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class RepositoryImpl(
    private val source: RemoteSource = SourceModule.provideApi(),
) : Repository {

    override suspend fun getValute(isRequestingRequired: Boolean) = processData(
        data = {
            source.getCurrencies()
        },
        mapper = {
            it.toCurrencyModelList()
        },
        isRequestingRequired = isRequestingRequired,
    )

}

inline fun <R, T> processData(
    crossinline data: suspend () -> Response<R>,
    crossinline mapper: (R) -> List<T>,
    isRequestingRequired: Boolean,
) = flow<ResultModel<List<T>, ResponseError>> {
    while (isRequestingRequired) {
        try {
            emit(ResultModel.Loading)
            val response = data()

            if (response.isSuccessful) {
                val body = response.body()!!
                emit(ResultModel.Success(mapper(body)))
            } else {
                when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> emit(ResultModel.Error(ResponseError.NotFound))
                    HttpURLConnection.HTTP_UNAUTHORIZED -> emit(ResultModel.Error(ResponseError.Unauthorized))
                    else -> emit(ResultModel.Error(ResponseError.Other))
                }
            }
        } catch (e: IOException) {
            emit(ResultModel.Error(ResponseError.NetworkError))
        } catch (e: Exception) {
            emit(ResultModel.Error(ResponseError.Other))
        }

        delay(TimeUnit.SECONDS.toMillis(30))
    }
}