package com.kseniabl.currencymanager.domain.mapper

import com.kseniabl.currencymanager.data.model.ValuteResponse
import com.kseniabl.currencymanager.domain.model.CurrencyModel

fun ValuteResponse.toCurrencyModelList(): List<CurrencyModel> =
    this.valute.values.map {
        CurrencyModel(
            it.id,
            it.numCode,
            it.charCode,
            it.nominal,
            it.name,
            it.value,
            it.previous
        )
    }
