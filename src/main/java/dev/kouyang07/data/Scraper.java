package dev.kouyang07.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import dev.kouyang07.data.structs.Item;
import dev.kouyang07.data.structs.Price;
import dev.kouyang07.data.structs.scraper.*;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.List;
@Data
public class Scraper {
    private final Item item;
    private Document doc;
    private String url;

    public Scraper(Item item) {
        this.item = item;
        try {
            this.url = item.generateURL();
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            doc = null;
        }
    }

    private Platform[] scrapePlatforms() {
        Platform[] platforms = new Platform[25];
        try {
            Elements skinPortElement = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[2]");
            Element skinPort = skinPortElement.first();
            assert skinPort != null;
            String[] skinPortTokens = skinPort.text().substring(267).split(" ");
            String linkString;
            Element link = skinPortElement.select("div.bg-gray-800 a:contains(View Offer)").first();
            if (link != null) {
                linkString = (link.attr("href"));
            } else {
                linkString = ("Link not found");
            }
            platforms[0] = new Platform(skinPortTokens[0], Integer.parseInt(skinPortTokens[7]), Double.parseDouble(skinPortTokens[9].substring(1)), linkString);

            for (int i = 3; i <= platforms.length + 3; i++) {
                Elements platformElements = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[" + i + "]");
                Element platform = platformElements.first();
                if (platform != null) {
                    platforms[i - 2] = parsePlatform(platform);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return platforms;
    }

    private Platform parsePlatform(Element platform) {
        String[] tokens = platform.text().split(" ");
        String linkString;
        Element link = platform.select("div.bg-gray-800 a:contains(View Offer)").first();
        if (link != null) {
            linkString = (link.attr("href"));
        } else {
            linkString = ("Link not found");
        }
        if (tokens.length == 12) {
            return new Platform(tokens[0], Integer.parseInt(tokens[7].contains("k") ? String.valueOf((Integer.parseInt(tokens[7].replaceAll("k", "")) * 1000)) : tokens[7]), Double.parseDouble(tokens[9].substring(1)), linkString);
        } else {
            return new Platform(tokens[0] + " " + tokens[1], Integer.parseInt(tokens[8]), Double.parseDouble(tokens[10].substring(1)), linkString);
        }
    }

    /**
     * Filters the platforms to only include the platform with the lowest price and the platform with the highest price
     *
     * @return an array of the lowest(Indexed at 0) and highest(Indexed at 1) priced platforms
     */

    public Platform[] filterPlatforms() {
        Platform[] platforms = scrapePlatforms();
        int lIndex = 0;
        double minPrice = Double.MAX_VALUE;
        int hIndex = 0;
        double maxPrice = Double.MIN_VALUE;
        for (int i = 0; i < platforms.length; i++) {
            if(Objects.isNull(platforms[i])){
                continue;
            }
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() < minPrice) {
                minPrice = platforms[i].getPrice();
                lIndex = i;
            }
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() > maxPrice && (platforms[i].getName().equals("Skinport") || platforms[i].getName().equals("Dmarket") || platforms[i].getName().equals("GamerPay"))) {
                maxPrice = platforms[i].getPrice();
                hIndex = i;
            }
        }

        Platform[] prices = new Platform[]{platforms[lIndex], platforms[hIndex]};

        if(prices[1] != null) {

            if (prices[1].getName().equals("Skinport")) {
                prices[1].setPrice(prices[1].getPrice() - (prices[1].getPrice() * 0.12));
            } else if (prices[1].getName().equals("Dmarket")) {
                prices[1].setPrice(prices[1].getPrice() - (prices[1].getPrice() * 0.05));
            }
        }
        return prices;
    }

    public PriceStatistics getPriceStatistics() {
        List<Double> values = new ArrayList<>();

        Element element = doc.select("div[class=\"shadow-md bg-gray-800 rounded mt-4\"]").getLast();
        Elements fields = element.select("div[class=\"flex px-4 py-2\"]");
        for (Element field : fields) {
            String[] tokens = field.text().split(" ");
            if (tokens[2].contains("Change")) {
                values.add(Double.parseDouble(tokens[tokens.length - 2].replaceAll(",", "").replaceAll("\\$", "")));
            } else {
                values.add(Double.parseDouble(tokens[tokens.length - 1].substring(1).replaceAll(",", "")));
            }
        }
        return new PriceStatistics(values);
    }

    public PriceHistory getPriceHistory() {
        Elements scripts = doc.select("script");
        PriceHistory priceHistory = null;
        try {

            for (Element script : scripts) {
                // Check if the script contains the 'priceHistory' variable
                if (script.data().contains("const priceHistory =")) {
                    // Extract the JSON data
                    String scriptData = script.data();
                    String jsonData = scriptData.split("const priceHistory =")[1].split(";")[0].trim();

                    // Parse the JSON data
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(new StdDateFormat());

                    try {
                        List<DailyPrice> dailyPriceList = objectMapper.readValue(jsonData, new TypeReference<>() {
                        });

                        priceHistory = new PriceHistory();
                        priceHistory.setItem(item);
                        priceHistory.setDailyPrice(dailyPriceList);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while parsing historical Data");
        }
        return priceHistory;
    }

    public Price getPrice(){
        Platform[] results = scrapePlatforms();
        Price price = new Price(results[0], results[1], getPriceHistory(), getPriceStatistics());
        CSData.cachedPrice.putIfAbsent(item, price);
        return price;
    }

    public String getImageURL(){
        Element img = doc.select("img#main-image").first();
        if (img != null) {
            return img.attr("src");
        } else {
            return "";
        }
    }

    public boolean isValid() {
        return doc != null;
    }
}
