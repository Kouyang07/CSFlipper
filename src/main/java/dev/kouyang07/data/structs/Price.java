package dev.kouyang07.data.structs;

import dev.kouyang07.data.structs.scraper.Platforms;
import dev.kouyang07.data.structs.scraper.Product;
import dev.kouyang07.data.structs.scraper.PriceStatistics;
import lombok.Data;

import java.util.List;

@Data
public class Price {
    private List<Platforms> platforms;
    private Product priceHistory;
    private PriceStatistics priceStatistics;

    public Price(List<Platforms> platforms, Product priceHistory, PriceStatistics priceStatistics) {
        this.platforms = platforms;
        this.priceHistory = priceHistory;
        this.priceStatistics = priceStatistics;
    }
}
