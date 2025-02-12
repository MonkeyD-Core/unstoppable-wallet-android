package io.horizontalsystems.bankwallet.modules.market.filters

import io.horizontalsystems.bankwallet.core.subscribeIO
import io.horizontalsystems.bankwallet.entities.DataState
import io.horizontalsystems.bankwallet.modules.market.MarketItem
import io.horizontalsystems.bankwallet.modules.market.priceChangeValue
import io.horizontalsystems.core.entities.Currency
import io.horizontalsystems.marketkit.MarketKit
import io.horizontalsystems.marketkit.models.CoinType
import io.horizontalsystems.marketkit.models.MarketInfo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal


class MarketFiltersService(
    private val marketKit: MarketKit,
    private val baseCurrency: Currency
) : IMarketListFetcher {

    val currencyCode: String
        get() = baseCurrency.code

    private val allTimeDeltaPercent = BigDecimal.TEN

    var coinCount: Int = CoinList.Top250.itemsCount
        set(value) {
            field = value
            cache = null

            refreshCounter()
        }
    var filterMarketCap: Pair<Long?, Long?>? = null
        set(value) {
            field = value

            refreshCounter()
        }
    var filterVolume: Pair<Long?, Long?>? = null
        set(value) {
            field = value

            refreshCounter()
        }
    var filterLiquidity: Pair<Long?, Long?>? = null
        set(value) {
            field = value

            refreshCounter()
        }
    var filterPeriod: TimePeriod = TimePeriod.TimePeriod_1D
        set(value) {
            field = value

            refreshCounter()
        }
    var filterPriceChange: Pair<Long?, Long?>? = null
        set(value) {
            field = value

            refreshCounter()
        }
    var filterBlockchains: List<MarketFiltersModule.Blockchain> = listOf()
        set(value) {
            field = value

            refreshCounter()
        }
    var filterOutperformedBtcOn: Boolean = false
        set(value) {
            field = value

            refreshCounter()
        }
    var filterOutperformedEthOn: Boolean = false
        set(value) {
            field = value

            refreshCounter()
        }
    var filterOutperformedBnbOn: Boolean = false
        set(value) {
            field = value

            refreshCounter()
        }
    var filterPriceCloseToAth: Boolean = false
        set(value) {
            field = value

            refreshCounter()
        }
    var filterPriceCloseToAtl: Boolean = false
        set(value) {
            field = value

            refreshCounter()
        }

    var numberOfItemsAsync = BehaviorSubject.create<DataState<Int>>()

    private var topItemsDisposable: Disposable? = null
    private var disposables = CompositeDisposable()
    private var cache: List<MarketInfo>? = null

    fun start() {
        refreshCounter()
    }

    fun stop() {
        topItemsDisposable?.dispose()
        disposables.dispose()
    }

    override fun fetchAsync(): Single<List<MarketItem>> {
        return getTopMarketList()
            .map { coinMarkets ->
                coinMarkets.map {
                    val coinMarket = it.value

                    MarketItem.createFromCoinMarket(coinMarket, baseCurrency, filterPeriod)
                }
            }
    }

    private fun refreshCounter() {
        topItemsDisposable?.dispose()

        numberOfItemsAsync.onNext(DataState.Loading)
        topItemsDisposable = getTopMarketList()
            .map { it.size }
            .subscribeIO({
                numberOfItemsAsync.onNext(DataState.Success(it))
            }, {
                numberOfItemsAsync.onNext(DataState.Error(it))
            })

    }

    private fun getTopMarketList(): Single<Map<Int, MarketInfo>> {
        val topMarketListAsync = if (cache != null) {
            Single.just(cache)
        } else {
            marketKit.advancedMarketInfosSingle(coinCount, baseCurrency.code)
                .doOnSuccess {
                    cache = it
                }
        }

        return topMarketListAsync
            .map {
                it.mapIndexed { index, coinMarket ->
                    index to coinMarket
                }.filter {
                    filterCoinMarket(it.second)
                }.toMap()
            }
    }

    private fun filterCoinMarket(marketInfo: MarketInfo): Boolean {
        val marketCap = marketInfo.marketCap ?: return false
        val totalVolume = marketInfo.totalVolume ?: return false
        val priceChangeValue = marketInfo.priceChangeValue(filterPeriod) ?: return false

        return filterByRange(filterMarketCap, marketCap.toLong())
                && filterByRange(filterVolume, totalVolume.toLong())
                && inBlockchain(marketInfo.coinTypes)
                && filterByRange(filterPriceChange, priceChangeValue.toLong())
                && (!filterPriceCloseToAth || closeToAllTime(marketInfo.athPercentage))
                && (!filterPriceCloseToAtl || closeToAllTime(marketInfo.atlPercentage))
                && (!filterOutperformedBtcOn || outperformed(priceChangeValue, "bitcoin"))
                && (!filterOutperformedEthOn || outperformed(priceChangeValue, "ethereum"))
                && (!filterOutperformedBnbOn || outperformed(priceChangeValue, "binancecoin"))
    }

    private fun filterByRange(filter: Pair<Long?, Long?>?, value: Long?): Boolean {
        if (filter == null) return true

        filter.first?.let { min ->
            if (value == null || value < min) {
                return false
            }
        }

        filter.second?.let { max ->
            if (value == null || value > max) {
                return false
            }
        }

        return true
    }

    private fun marketInfo(coinUid: String): MarketInfo? =
        cache?.firstOrNull { it.fullCoin.coin.uid == coinUid }

    private fun outperformed(value: BigDecimal?, coinUid: String): Boolean {
        if (value == null) return false
        val coinMarket = marketInfo(coinUid) ?: return false

        return coinMarket.priceChangeValue(filterPeriod) ?: BigDecimal.ZERO < value
    }

    private fun closeToAllTime(value: BigDecimal?): Boolean {
        value ?: return false

        return value.abs() < allTimeDeltaPercent
    }

    private fun inBlockchain(coinTypes: List<CoinType>?): Boolean {
        if (filterBlockchains.isEmpty()) return true

        coinTypes?.forEach { coinType ->
            if (filterBlockchains.any { it.contains(coinType) }) {
                return true
            }
        }

        return false
    }
}
