package com.kseniabl.currencymanager.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.kseniabl.currencymanager.R
import com.kseniabl.currencymanager.presentation.exchange_rates.ExchangeRateFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setExchangeFragment()
    }

    private fun setExchangeFragment() {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace<ExchangeRateFragment>(R.id.fragment_container_view)
        }
    }
}