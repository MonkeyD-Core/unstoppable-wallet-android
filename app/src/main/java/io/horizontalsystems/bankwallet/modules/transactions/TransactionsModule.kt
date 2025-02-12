package io.horizontalsystems.bankwallet.modules.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Account
import io.horizontalsystems.bankwallet.entities.CoinSettings
import io.horizontalsystems.bankwallet.entities.EvmBlockchain
import io.horizontalsystems.marketkit.models.PlatformCoin
import java.math.BigDecimal
import java.util.*

object TransactionsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return TransactionsViewModel(
                TransactionsService(
                    TransactionRecordRepository(App.transactionAdapterManager),
                    TransactionsRateRepository(App.currencyManager, App.marketKit),
                    TransactionSyncStateRepository(App.transactionAdapterManager),
                    App.transactionAdapterManager,
                    App.walletManager,
                    TransactionFilterService()
                ),
                TransactionViewItemFactory(App.evmLabelManager)
            ) as T
        }
    }
}

data class TransactionLockInfo(
    val lockedUntil: Date,
    val originalAddress: String,
    val amount: BigDecimal?
)

sealed class TransactionStatus {
    object Pending : TransactionStatus()
    class Processing(val progress: Float) : TransactionStatus() //progress in 0.0 .. 1.0
    object Completed : TransactionStatus()
    object Failed : TransactionStatus()
}

data class TransactionWallet(
    val platformCoin: PlatformCoin?,
    val source: TransactionSource,
    val badge: String?
)

data class TransactionSource(
    val blockchain: Blockchain,
    val account: Account,
    val coinSettings: CoinSettings
) {

    sealed class Blockchain {
        object Bitcoin : Blockchain()
        object Litecoin : Blockchain()
        object Tyzen : Blockchain()
        object BitcoinCash : Blockchain()
        object Dash : Blockchain()
        object Zcash : Blockchain()
        class Bep2(val symbol: String) : Blockchain(){
            override fun hashCode(): Int {
                return this.symbol.hashCode()
            }
            override fun equals(other: Any?): Boolean {
                return when(other){
                    is Bep2 -> this.symbol == other.symbol
                    else -> false
                }
            }
        }
        class Evm(val evmBlockchain: EvmBlockchain) : Blockchain() {
            override fun hashCode(): Int {
                return this.evmBlockchain.hashCode()
            }
            override fun equals(other: Any?): Boolean {
                return when(other){
                    is Evm -> this.evmBlockchain == other.evmBlockchain
                    else -> false
                }
            }
        }

        fun getTitle(): String {
            return when(this){
                Bitcoin -> "Bitcoin"
                Litecoin -> "Litecoin"
                Tyzen -> "Tyzen"
                BitcoinCash -> "BitcoinCash"
                Dash -> "Dash"
                Zcash -> "Zcash"
                is Bep2 -> "Binance Chain"
                is Evm -> this.evmBlockchain.shortName
            }
        }
    }

}
