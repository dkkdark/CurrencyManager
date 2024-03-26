package com.kseniabl.currencymanager.domain.repository

import com.kseniabl.currencymanager.domain.model.ResponseError
import com.kseniabl.currencymanager.domain.model.ResultModel
import com.kseniabl.currencymanager.domain.model.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getValute(isRequestingRequired: Boolean): Flow<ResultModel<List<CurrencyModel>, ResponseError>>
}