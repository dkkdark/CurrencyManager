package com.kseniabl.currencymanager.presentation.exchange_rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.currencymanager.databinding.ExchangeCurrencyFragmentBinding
import com.kseniabl.currencymanager.presentation.exchange_rates.adapter.CurrencyRecycleViewAdapter
import kotlinx.coroutines.launch

class ExchangeRateFragment : Fragment() {

    private var _binding: ExchangeCurrencyFragmentBinding? = null
    private val binding get() = _binding!!

    // both could be provided through DI
    private lateinit var adapter: CurrencyRecycleViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val viewModel: CurrencyViewModel by viewModels { CurrencyViewModel.provideFactory(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExchangeCurrencyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CurrencyRecycleViewAdapter()
        linearLayoutManager = LinearLayoutManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            currencyRecycleView.layoutManager = linearLayoutManager
            currencyRecycleView.adapter = adapter
            currencyRecycleView.setItemViewCacheSize(20)
        }

        adapter.setOnClickListener(viewModel)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { uiState ->
                        when (uiState) {
                            is CurrencyViewModel.CurrencyState.ErrorForSnackbar -> {
                                Snackbar.make(view, uiState.error, Snackbar.LENGTH_SHORT).show()
                            }

                            CurrencyViewModel.CurrencyState.UpdateTime -> {
                                hideProgressBar()
                            }

                            CurrencyViewModel.CurrencyState.Loading -> {
                                showProgressBar()
                            }
                        }
                    }
                }
                launch {
                    viewModel.currencies.collect {
                        // diffCallback do not let update the same list
                        adapter.submitList(it)
                    }
                }
                launch {
                    viewModel.time.collect {
                        if (it == "")
                            viewModel.onEventListen(CurrencyViewModel.CurrencyEvent.GetCurrencies)
                        else
                            binding.lastUpdateTime.text = viewModel.time.value
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        // I decided not to show a progress bar when the list has items
        // it is unnecessary
        if (adapter.itemCount == 0)
            binding.progressCircular.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}