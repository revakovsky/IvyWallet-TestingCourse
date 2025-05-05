package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.ivy.core.domain.action.exchange.fakes.ExchangeRateDaoFake
import com.ivy.core.domain.action.exchange.fakes.RemoteExchangeProviderFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SyncExchangeRatesActTest {

    private lateinit var remoteExchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeRateDaoFake: ExchangeRateDaoFake
    private lateinit var syncExchangeRatesAct: SyncExchangeRatesAct


    @BeforeEach
    fun setUp() {
        remoteExchangeProviderFake = RemoteExchangeProviderFake()
        exchangeRateDaoFake = ExchangeRateDaoFake()
        syncExchangeRatesAct = SyncExchangeRatesAct(remoteExchangeProviderFake, exchangeRateDaoFake)
    }

    @Test
    fun `Test sync exchange rates, negative values ignored`() = runBlocking {
        syncExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }
        val cadRate = usdRates.find { it.currency == "CAD" }

        assertThat(cadRate).isNull()
    }

    @Test
    fun `Test sync exchange rates, valid values are successfully saved`() = runBlocking<Unit> {
        syncExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }
        val eurRate = usdRates.find { it.currency == "EUR" }
        val audRate = usdRates.find { it.currency == "AUD" }

        assertThat(eurRate).isNotNull()
        assertThat(audRate).isNotNull()
    }

}