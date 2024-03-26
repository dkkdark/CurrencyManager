package com.kseniabl.currencymanager.domain.model

enum class ResponseError : Error {
    NotFound,
    Unauthorized,
    NetworkError,
    Other
}