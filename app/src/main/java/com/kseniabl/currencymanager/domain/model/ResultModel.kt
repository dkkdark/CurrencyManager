package com.kseniabl.currencymanager.domain.model

interface Error
typealias ResultError = Error

sealed interface ResultModel<out D, out E : ResultError> {
    data class Success<out D, out E : ResultError>(val data: D) : ResultModel<D, E>
    data class Error<out D, out E : ResultError>(val error: E) : ResultModel<D, E>
    data object Loading : ResultModel<Nothing, Nothing>
}