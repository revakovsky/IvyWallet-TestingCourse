package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import java.time.Instant

internal class RawStatsTest {

    @Test
    fun `Test creating raw stats from transactions`() {
        // Set transactions time
        val tenSecondAgo = Instant.now().minusSeconds(10L)
        val thirtySecondAgo = Instant.now().minusSeconds(30L)
        val fiveSecondAgo = Instant.now().minusSeconds(5L)
        val minuteAgo = Instant.now().minusSeconds(60L)

        // Create transactions
        val trns: List<CalcTrn> = listOf(
            CalcTrn(
                amount = 1.0,
                currency = "USD",
                type = TransactionType.Expense,
                time = fiveSecondAgo,
            ),
            CalcTrn(
                amount = 10.0,
                currency = "USD",
                type = TransactionType.Income,
                time = thirtySecondAgo,
            ),
            CalcTrn(
                amount = 5.0,
                currency = "EUR",
                type = TransactionType.Expense,
                time = tenSecondAgo,
            ),
            CalcTrn(
                amount = 10.0,
                currency = "EUR",
                type = TransactionType.Income,
                time = minuteAgo,
            ),
            CalcTrn(
                amount = 5.0,
                currency = "EUR",
                type = TransactionType.Income,
                time = minuteAgo,
            ),
        )

        // Getting result of the testing function
        val rawStats = rawStats(trns)

        val expected = RawStats(
            incomes = mapOf("USD" to 10.0, "EUR" to 15.0),
            expenses = mapOf("USD" to 1.0, "EUR" to 5.0),
            incomesCount = 3,
            expensesCount = 2,
            newestTrnTime = fiveSecondAgo
        )

        assertThat(rawStats).isEqualTo(expected)

        assertThat(rawStats.incomesCount).isEqualTo(3)
        assertThat(rawStats.expensesCount).isEqualTo(2)

        assertThat(rawStats.expenses.keys.first()).isEqualTo("USD")
        assertThat(rawStats.incomes.keys.first()).isEqualTo("USD")

        assertThat(rawStats.newestTrnTime).isEqualTo(fiveSecondAgo)

        assertThat(rawStats.incomes).isEqualTo(mapOf("USD" to 10.0, "EUR" to 15.0))
        assertThat(rawStats.expenses).isEqualTo(mapOf("USD" to 1.0, "EUR" to 5.0))
    }

}