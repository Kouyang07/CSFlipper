package dev.kouyang07.data;

import dev.kouyang07.data.structs.APIWrapper;
import dev.kouyang07.data.structs.Price;
import dev.kouyang07.data.structs.Skin;
import dev.kouyang07.data.structs.scraper.Platforms;
import dev.kouyang07.data.structs.scraper.PriceHistory;
import dev.kouyang07.data.structs.scraper.PriceStatistics;
import dev.kouyang07.data.structs.scraper.Product;
import dev.kouyang07.data.structs.skin.Wear;
import lombok.Data;

import java.util.List;

@Data
public class Item {
    private long timeStamp;
    private final Skin skin;
    private Price price;

    public Item(long timeStamp, Skin skin, Price price){
        this.timeStamp = timeStamp;
        this.skin = skin;
        this.price = price;
    }

    public void updatePrice(){
        Scraper scraper = new Scraper(skin.getName(), dev.kouyang07.data.structs.skin.Wear.toWearEnum(skin.getWears().getFirst().getName()) , skin.isStattrak());
        Platforms[] platforms = scraper.filterPlatforms();
        Product priceHistory =  scraper.scrapeHistoricalData();
        PriceStatistics priceStatistics = scraper.scrapePriceStatistics();
        this.price = new Price(List.of(platforms), priceHistory, priceStatistics);
    }
}
