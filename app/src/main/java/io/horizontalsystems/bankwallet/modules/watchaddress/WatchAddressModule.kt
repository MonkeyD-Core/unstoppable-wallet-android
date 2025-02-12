package io.horizontalsystems.bankwallet.modules.watchaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App

object WatchAddressModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = WatchAddressService(
                App.accountFactory,
                App.accountManager,
                App.walletActivator,
                App.evmBlockchainManager
            )

            return WatchAddressViewModel(service) as T
        }
    }
}
