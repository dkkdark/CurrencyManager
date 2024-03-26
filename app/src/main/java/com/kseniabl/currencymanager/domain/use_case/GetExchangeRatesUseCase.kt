package com.kseniabl.currencymanager.domain.use_case

import com.kseniabl.currencymanager.dependency.SourceModule
import com.kseniabl.currencymanager.domain.model.ResponseError
import com.kseniabl.currencymanager.domain.model.ResultModel
import com.kseniabl.currencymanager.domain.model.CurrencyModel
import com.kseniabl.currencymanager.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetExchangeRatesUseCase(
    private val repository: Repository = SourceModule.provideApiCallsRepository()
) {

    suspend operator fun invoke(isRequestingRequired: Boolean): Flow<ResultModel<List<CurrencyModel>, ResponseError>> {
        return repository.getValute(isRequestingRequired)
    }

}