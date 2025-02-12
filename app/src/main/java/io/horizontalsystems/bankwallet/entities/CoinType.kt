package io.horizontalsystems.bankwallet.entities

import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.managers.RestoreSettingType
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.core.shortenedAddress
import io.horizontalsystems.marketkit.models.CoinType
import io.horizontalsystems.marketkit.models.PlatformCoin

val CoinType.blockchainType: String?
    get() {
        return when (this) {
            is CoinType.Erc20 -> "ERC20"
            is CoinType.Bep20 -> "BEP20"
            is CoinType.Polygon, is CoinType.Mrc20 -> "POLYGON"
            is CoinType.EthereumOptimism, is CoinType.OptimismErc20 -> "OPTIMISM"
            is CoinType.EthereumArbitrumOne, is CoinType.ArbitrumOneErc20 -> "ARBITRUM"
            is CoinType.Bep2 -> "BEP2"
            else -> null
        }
    }

val CoinType.platformType: String
    get() = when (this) {
        CoinType.Ethereum -> "Ethereum"
        is CoinType.Erc20 -> "ERC20"
        CoinType.BinanceSmartChain -> "Binance Smart Chain"
        is CoinType.Bep20 -> "BEP20"
        CoinType.Polygon, is CoinType.Mrc20 -> "Polygon"
        CoinType.EthereumOptimism, is CoinType.EthereumOptimism -> "Polygon"
        CoinType.EthereumArbitrumOne, is CoinType.ArbitrumOneErc20 -> "ArbitrumOne"
        is CoinType.Bep2 -> "Binance"
        else -> ""
    }

val CoinType.platformCoinType: String
    get() = when (this) {
        CoinType.Ethereum, CoinType.BinanceSmartChain, CoinType.Polygon, CoinType.EthereumOptimism, CoinType.EthereumArbitrumOne -> Translator.getString(R.string.CoinPlatforms_Native)
        is CoinType.Erc20 -> this.address.shortenedAddress()
        is CoinType.Bep20 -> this.address.shortenedAddress()
        is CoinType.Mrc20 -> this.address.shortenedAddress()
        is CoinType.Bep2 -> this.symbol
        else -> ""
    }

val CoinType.title: String
    get() {
        return when (this) {
            is CoinType.Bitcoin -> "Bitcoin"
            is CoinType.Litecoin -> "Litecoin"
            is CoinType.Tyzen -> "Tyzen"
            is CoinType.BitcoinCash -> "Bitcoin Cash"
            is CoinType.Dash -> "Dash"
            else -> ""
        }
    }

val CoinType.label: String?
    get() = when (this) {
        is CoinType.Erc20 -> "ERC20"
        is CoinType.Bep20 -> "BEP20"
        is CoinType.Bep2 -> "BEP2"
        else -> null
    }

val CoinType.swappable: Boolean
    get() = this is CoinType.Ethereum || this is CoinType.Erc20 ||
        this is CoinType.BinanceSmartChain || this is CoinType.Bep20 ||
        this is CoinType.Polygon || this is CoinType.Mrc20 ||
        this is CoinType.EthereumOptimism || this is CoinType.OptimismErc20 ||
        this is CoinType.EthereumArbitrumOne || this is CoinType.ArbitrumOneErc20

val CoinType.coinSettingTypes: List<CoinSettingType>
    get() = when (this) {
        CoinType.Bitcoin,
        CoinType.Litecoin -> listOf(CoinSettingType.derivation)
        CoinType.Tyzen -> listOf(CoinSettingType.derivation)
        CoinType.BitcoinCash -> listOf(CoinSettingType.bitcoinCashCoinType)
        else -> listOf()
    }

val CoinType.defaultSettingsArray: List<CoinSettings>
    get() = when (this) {
        CoinType.Bitcoin,
        CoinType.Litecoin -> listOf(CoinSettings(mapOf(CoinSettingType.derivation to AccountType.Derivation.bip49.value)))
        CoinType.Tyzen -> listOf(CoinSettings(mapOf(CoinSettingType.derivation to AccountType.Derivation.bip49.value)))
        CoinType.BitcoinCash -> listOf(CoinSettings(mapOf(CoinSettingType.bitcoinCashCoinType to BitcoinCashCoinType.type145.value)))
        else -> listOf()
    }

val CoinType.restoreSettingTypes: List<RestoreSettingType>
    get() = when (this) {
        CoinType.Zcash -> listOf(RestoreSettingType.BirthdayHeight)
        else -> listOf()
    }

val CoinType.isSupported: Boolean
    get() = when (this) {
        CoinType.Bitcoin,
        CoinType.BitcoinCash,
        CoinType.Litecoin,
        CoinType.Tyzen,
        CoinType.Dash,
        CoinType.Zcash,
        CoinType.Ethereum,
        CoinType.BinanceSmartChain,
        CoinType.Polygon,
        is CoinType.Erc20,
        is CoinType.Bep20,
        is CoinType.Mrc20,
        is CoinType.Bep2 -> true
        is CoinType.Avalanche,
        is CoinType.Fantom,
        is CoinType.HarmonyShard0,
        is CoinType.HuobiToken,
        is CoinType.Iotex,
        is CoinType.Moonriver,
        is CoinType.OkexChain,
        CoinType.EthereumOptimism,
        CoinType.EthereumArbitrumOne,
        is CoinType.OptimismErc20,
        is CoinType.ArbitrumOneErc20,
        is CoinType.Solana,
        is CoinType.Sora,
        is CoinType.Tomochain,
        is CoinType.Xdai,
        is CoinType.Unsupported -> false
    }

val CoinType.order: Int
    get() = when (this) {
        is CoinType.Bitcoin -> 1
        is CoinType.BitcoinCash -> 2
        is CoinType.Litecoin -> 3
        is CoinType.Tyzen -> 4
        is CoinType.Dash -> 5
        is CoinType.Zcash -> 6
        is CoinType.Ethereum -> 7
        is CoinType.BinanceSmartChain -> 8
        is CoinType.Polygon -> 9
        CoinType.EthereumOptimism -> 10
        CoinType.EthereumArbitrumOne -> 11
        is CoinType.Erc20 -> 12
        is CoinType.Bep20 -> 13
        is CoinType.Mrc20 -> 14
        is CoinType.OptimismErc20 -> 15
        is CoinType.ArbitrumOneErc20 -> 16
        is CoinType.Bep2 -> 17
        is CoinType.Solana -> 18
        is CoinType.Avalanche -> 19
        is CoinType.Fantom -> 20
        is CoinType.HuobiToken -> 21
        is CoinType.HarmonyShard0 -> 22
        is CoinType.Xdai -> 23
        is CoinType.Moonriver -> 24
        is CoinType.OkexChain -> 25
        is CoinType.Sora -> 26
        is CoinType.Tomochain -> 27
        is CoinType.Iotex -> 28
        else -> Int.MAX_VALUE
    }

val CoinType.customCoinUid: String
    get() = "custom_${id}"

val PlatformCoin.isCustom: Boolean
    get() = coin.uid == coinType.customCoinUid
