package com.kseniabl.currencymanager.presentation.exchange_rates

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.kseniabl.currencymanager.domain.model.CurrencyModel
import com.kseniabl.currencymanager.domain.use_case.GetExchangeRatesUseCase
import com.kseniabl.currencymanager.presentation.exchange_rates.adapter.CurrencyRecycleViewAdapter
import com.kseniabl.currencymanager.presentation.processResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class CurrencyViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getExchangeRates: GetExchangeRatesUseCase
) : ViewModel(), CurrencyRecycleViewAdapter.Listener {

    private val _currencies = MutableStateFlow<List<CurrencyModel>>(emptyList())
    val currencies = _currencies.asStateFlow()

    private val _state = MutableSharedFlow<CurrencyState>()
    val state = _state.asSharedFlow()

    private val _observeCurrencyEvery30Seconds = MutableStateFlow(true)

    val time: StateFlow<String> =
        savedStateHandle.getStateFlow("currentTime", "")

    private fun setCurrentTime(time: String) {
        savedStateHandle["currentTime"] = time
    }

    private fun getCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRates(_observeCurrencyEvery30Seconds.value).collect {
                it.processResult(
                    getValue = {
                        setCurrentTime(getCurrentTime())
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

    private fun getCurrentTime(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            currentDateTime.format(formatter)
        } else {
            val currentDateTime = Date()
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            formatter.format(currentDateTime)
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

    // I prefer handling interactions with RecycleView items in ViewModel
    // because they are often affect some data
    override fun onAddItemClick(item: CurrencyModel) {
        Log.i("CurrencyViewModel", "item $item was clicked")
    }

    companion object {
        fun provideFactory(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    val useCase = GetExchangeRatesUseCase()
                    return CurrencyViewModel(handle, useCase) as T
                }
            }
    }
}


