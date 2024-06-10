package dev.kouyang07.data.structs.scraper;

import lombok.Data;

import java.util.List;

@Data
public class PriceStatistics {
    private double currentPrice;
    private double priceChange;
    private double tradingVolume;
    private double marketCap;
    private double volumeMarketCap;
    private double high;
    private double low;
    private double allTimeHigh;
    private double allTimeLow;

    public PriceStatistics(double currentPrice, double priceChange, double tradingVolume, double marketCap, double volumeMarketCap, double high, double low, double allTimeHigh, double allTimeLow) {
        this.currentPrice = currentPrice;
        this.priceChange = priceChange;
        this.tradingVolume = tradingVolume;
        this.marketCap = marketCap;
        this.volumeMarketCap = volumeMarketCap;
        this.high = high;
        this.low = low;
        this.allTimeHigh = allTimeHigh;
        this.allTimeLow = allTimeLow;
    }

    public PriceStatistics() {
        this.currentPrice = 0;
        this.priceChange = 0;
        this.tradingVolume = 0;
        this.marketCap = 0;
        this.volumeMarketCap = 0;
        this.high = 0;
        this.low = 0;
        this.allTimeHigh = 0;
        this.allTimeLow = 0;
    }

    public PriceStatistics(List<Double> values){
        this.currentPrice = values.get(0);
        this.priceChange = values.get(1);
        this.tradingVolume = values.get(2);
        this.marketCap = values.get(3);
        this.volumeMarketCap = values.get(4);
        this.high = values.get(5);
        this.low = values.get(6);
        this.allTimeHigh = values.get(7);
        this.allTimeLow = values.get(8);
    }
}
