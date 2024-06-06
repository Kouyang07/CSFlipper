package dev.kouyang07.data.scraper;

import lombok.Data;

import java.util.List;
@Data
public class Product {
    private String name;
    private List<PriceHistory> priceHistory;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", priceHistory=" + priceHistory +
                '}';
    }
}
