package dev.kouyang07.data.structs.scraper;

import dev.kouyang07.data.structs.Item;
import lombok.Data;

import java.util.List;
@Data
public class PriceHistory {
    private Item item;
    private List<DailyPrice> dailyPrice;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + item.generateShortName() + '\'' +
                ", priceHistory=" + dailyPrice +
                '}';
    }
}
