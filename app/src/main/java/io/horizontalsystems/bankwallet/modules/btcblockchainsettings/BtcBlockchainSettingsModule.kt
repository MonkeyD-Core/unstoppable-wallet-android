package io.horizontalsystems.bankwallet.modules.btcblockchainsettings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.BtcBlockchain

object BtcBlockchainSettingsModule {

    fun args(blockchain: BtcBlockchain): Bundle {
        return bundleOf("blockchain" to blockchain,)
    }

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val blockchain = arguments.getParcelable<BtcBlockchain>("blockchain")!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val service = BtcBlockchainSettingsService(
                blockchain,
                App.btcBlockchainManager
            )

            return BtcBlockchainSettingsViewModel(service) as T
        }
    }

    data class ViewItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val selected: Boolean,
    )
}