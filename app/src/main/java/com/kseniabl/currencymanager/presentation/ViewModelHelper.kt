package com.kseniabl.currencymanager.presentation

import com.kseniabl.currencymanager.domain.model.ResponseError
import com.kseniabl.currencymanager.domain.model.ResultError
import com.kseniabl.currencymanager.domain.model.ResultModel
import com.kseniabl.currencymanager.presentation.exchange_rates.CurrencyViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

suspend inline fun <T> ResultModel<T, ResultError>.processResult(
    crossinline getValue: () -> MutableStateFlow<T>,
    state: MutableSharedFlow<CurrencyViewModel.CurrencyState>,
) {
    when (this) {
        is ResultModel.Success -> {
            getValue().value = this.data!!
            // Update time only if result is successful
            state.emit(CurrencyViewModel.CurrencyState.UpdateTime)
        }

        is ResultModel.Error -> {
            when (error) {
                ResponseError.NotFound -> {
                    state.emit(CurrencyViewModel.CurrencyState.ErrorForSnackbar("Данные не найдены"))
                }

                ResponseError.Unauthorized -> {
                    state.emit(CurrencyViewModel.CurrencyState.ErrorForSnackbar("У вас нет доступа к данным"))
                }

                ResponseError.NetworkError -> {
                    state.emit(CurrencyViewModel.CurrencyState.ErrorForSnackbar("Проблемы с сетью, не можем загрузить данные"))
                }

                ResponseError.Other -> {
                    state.emit(CurrencyViewModel.CurrencyState.ErrorForSnackbar("Ошибка при загрузке данных"))
                }
            }
        }

        is ResultModel.Loading -> {
            state.emit(CurrencyViewModel.CurrencyState.Loading)
        }
    }
}