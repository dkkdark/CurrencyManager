package com.kseniabl.currencymanager.presentation.exchange_rates

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kseniabl.currencymanager.domain.model.CurrencyModel
import com.kseniabl.currencymanager.domain.use_case.GetExchangeRatesUseCase
import com.kseniabl.currencymanager.presentation.exchange_rates.adapter.CurrencyRecycleViewAdapter
import com.kseniabl.currencymanager.presentation.processResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val getExchangeRates: GetExchangeRatesUseCase
) : ViewModel(), CurrencyRecycleViewAdapter.Listener {

    private val _currencies = MutableStateFlow<List<CurrencyModel>>(emptyList())
    val currencies = _currencies.asStateFlow()

    private val _state = MutableSharedFlow<CurrencyState>()
    val state = _state.asSharedFlow()

    private val _observeCurrencyEvery30Seconds = MutableStateFlow(true)

    private fun getCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRates(_observeCurrencyEvery30Seconds.value).collect {
                it.processResult(
                    getValue = {
                        // If _currencies does not update, it means that the list remain the same
                        _currencies
                    },
                    state = _state,
                )
            }
        }
    }

    // If we need, it is easy to stop 30-seconds updating
    private fun changeObserveCurrencyEvery30Seconds() {
        _observeCurrencyEvery30Seconds.update {
            !it
        }
    }

    fun onEventListen(event: CurrencyEvent) {
        when (event) {
            is CurrencyEvent.GetCurrencies -> getCurrencies()
            is CurrencyEvent.ChangeObserveCurrencyEvery30Seconds -> changeObserveCurrencyEvery30Seconds()
        }
    }


    sealed interface CurrencyEvent {
        data object GetCurrencies : CurrencyEvent
        data object ChangeObserveCurrencyEvery30Seconds : CurrencyEvent
    }

    sealed interface CurrencyState {
        data class ErrorForSnackbar(val error: String) : CurrencyState
        data object UpdateTime : CurrencyState
        data object Loading : CurrencyState
    }

    // I prefer handle interactions with RecycleView in ViewModel
    // because they are often affect some data
    override fun onAddItemClick(item: CurrencyModel) {
        Log.i("CurrencyViewModel", "item $item was clicked")
    }

    // We do not use DI, so we have to create the factory and specify all dependencies manually
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val getExchangeRates = GetExchangeRatesUseCase()

                return CurrencyViewModel(
                    getExchangeRates
                ) as T
            }
        }
    }

}

