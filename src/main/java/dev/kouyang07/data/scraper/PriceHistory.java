package dev.kouyang07.data.scraper;

import lombok.Data;

import java.util.Date;
@Data
public class PriceHistory {
    private Date day;
    private int price;
    private int quantity;
    private int volume;

    @Override
    public String toString() {
        return "PriceHistory{" +
                "day=" + day +
                ", price=" + price +
                ", quantity=" + quantity +
                ", volume=" + volume +
                '}';
    }
}
