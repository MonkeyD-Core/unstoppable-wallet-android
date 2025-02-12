package io.horizontalsystems.bankwallet.modules.swap.coincard

import io.horizontalsystems.bankwallet.core.IAdapterManager
import io.horizontalsystems.bankwallet.core.IWalletManager
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.entities.EvmBlockchain
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule.CoinBalanceItem
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule.Dex
import io.horizontalsystems.core.ICurrencyManager
import io.horizontalsystems.marketkit.MarketKit
import io.horizontalsystems.marketkit.models.CoinType
import io.horizontalsystems.marketkit.models.PlatformCoin
import java.math.BigDecimal

class SwapCoinProvider(
    private val dex: Dex,
    private val walletManager: IWalletManager,
    private val adapterManager: IAdapterManager,
    private val currencyManager: ICurrencyManager,
    private val marketKit: MarketKit
) {

    private fun getCoinItems(filter: String): List<CoinBalanceItem> {
        val platformCoins = marketKit.platformCoins(dex.blockchain.platformType, filter)

        return platformCoins.map { CoinBalanceItem(it, null, null) }
    }

    private fun getWalletItems(filter: String): List<CoinBalanceItem> {
        val items = walletManager.activeWallets
            .filter { filter.isEmpty() || it.coin.name.contains(filter, true) || it.coin.code.contains(filter, true) }
            .filter { dexSupportsCoin(it.platformCoin) }
            .map { wallet ->
                val balance =
                    adapterManager.getBalanceAdapterForWallet(wallet)?.balanceData?.available

                CoinBalanceItem(
                    wallet.platformCoin,
                    balance,
                    getFiatValue(wallet.platformCoin, balance)
                )
            }

        return items
    }

    private fun dexSupportsCoin(coin: PlatformCoin) = when (coin.coinType) {
        CoinType.Ethereum, is CoinType.Erc20 -> dex.blockchain == EvmBlockchain.Ethereum
        CoinType.BinanceSmartChain, is CoinType.Bep20 -> dex.blockchain == EvmBlockchain.BinanceSmartChain
        CoinType.Polygon, is CoinType.Mrc20 -> dex.blockchain == EvmBlockchain.Polygon
        CoinType.EthereumOptimism, is CoinType.OptimismErc20 -> dex.blockchain == EvmBlockchain.Optimism
        CoinType.EthereumArbitrumOne, is CoinType.ArbitrumOneErc20 -> dex.blockchain == EvmBlockchain.ArbitrumOne
        else -> false
    }

    private fun getFiatValue(coin: PlatformCoin, balance: BigDecimal?): CurrencyValue? {
        return balance?.let {
            getXRate(coin)?.multiply(it)
        }?.let { fiatBalance ->
            CurrencyValue(currencyManager.baseCurrency, fiatBalance)
        }
    }

    private fun getXRate(platformCoin: PlatformCoin): BigDecimal? {
        val currency = currencyManager.baseCurrency
        return marketKit.coinPrice(platformCoin.coin.uid, currency.code)?.let {
            if (it.expired) {
                null
            } else {
                it.value
            }
        }
    }

    fun getCoins(filter: String): List<CoinBalanceItem> {
        val walletItems = getWalletItems(filter)
        val coinItems = getCoinItems(filter).filter { coinItem ->
            walletItems.indexOfFirst { it.platformCoin == coinItem.platformCoin } == -1
        }

        val allItems = walletItems + coinItems

        return allItems.sortedWith(compareByDescending { it.balance })
    }

}
