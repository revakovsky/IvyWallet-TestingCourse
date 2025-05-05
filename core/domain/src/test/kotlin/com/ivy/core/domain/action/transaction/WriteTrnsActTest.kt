package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.core.domain.action.transaction.data_generators.account
import com.ivy.core.domain.action.transaction.data_generators.attachment
import com.ivy.core.domain.action.transaction.data_generators.tag
import com.ivy.core.domain.action.transaction.data_generators.transaction
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class WriteTrnsActTest {

    private lateinit var writeTrnsAct: WriteTrnsAct

    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var invalidateAccCacheAct: InvalidateAccCacheAct

    @BeforeEach
    fun setUp() {
        transactionDaoFake = TransactionDaoFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        timeProviderFake = TimeProviderFake()
        invalidateAccCacheAct = InvalidateAccCacheAct(accountCacheDaoFake, timeProviderFake)

        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            trnsSignal = TrnsSignal(),
            timeProvider = timeProviderFake,
            invalidateAccCacheAct = invalidateAccCacheAct,
            accountCacheDao = accountCacheDaoFake
        )
    }

    @Test
    fun `Create a new trns with expense`() = runBlocking<Unit> {
        val transactionId = UUID.randomUUID()
        val tag = tag()
        val attachment = attachment(transactionId.toString())
        val transaction = transaction(transactionId, account = account()).copy(
            tags = listOf(tag),
            attachments = listOf(attachment)
        )

        writeTrnsAct(WriteTrnsAct.Input.CreateNew(transaction))

        val cachedTransaction = transactionDaoFake.transactions.find { it.id == transactionId.toString() }
        val cachedTag = transactionDaoFake.tags.find { it.tagId == tag.id }
        val cachedAttachment = transactionDaoFake.attachments.find { it.id == attachment.id }

        assertThat(cachedTransaction).isNotNull()
        assertThat(cachedTransaction?.type).isEqualTo(TransactionType.Expense)

        assertThat(cachedTag).isNotNull()
        assertThat(cachedAttachment).isNotNull()
    }

}