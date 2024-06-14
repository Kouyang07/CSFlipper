package dev.kouyang07.data.structs;

import dev.kouyang07.data.structs.scraper.Platform;
import dev.kouyang07.data.structs.scraper.DailyPrice;
import dev.kouyang07.data.structs.scraper.PriceHistory;
import dev.kouyang07.data.structs.scraper.PriceStatistics;
import lombok.Data;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
public class Price {
    private Platform lowestPlatform;
    private Platform highestPlatform;
    private PriceHistory priceHistory;
    private PriceStatistics priceStatistics;

    public Price(Platform lowestPlatform, Platform highestPlatform, PriceHistory priceHistory, PriceStatistics priceStatistics) {
        this.lowestPlatform = lowestPlatform;
        this.highestPlatform = highestPlatform;
        this.priceHistory = priceHistory;
        this.priceStatistics = priceStatistics;
    }

    public byte[] generateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); // The format of your date strings
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd"); // Desired format for the chart
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14); // Get date 30 days ago
        Date thirtyDaysAgo = calendar.getTime();
        for (DailyPrice entry : priceHistory.getDailyPrice()) {
            try {
                // Parse the date using the correct format
                Date date = inputDateFormat.parse(String.valueOf(entry.getDay()));
                if (date.after(thirtyDaysAgo)) { // Filter for the last 30 days
                    String formattedDate = outputDateFormat.format(date);
                    dataset.addValue(entry.getPrice(), "Price", formattedDate);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date: " + entry.getDay());
                e.printStackTrace(); // Print stack trace to identify the issue
            } catch (Exception e) {
                System.err.println("Unexpected error while adding value to dataset for date: " + entry.getDay());
                e.printStackTrace(); // Print stack trace to identify the issue
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Price History for " + priceHistory.getItem().generateShortName(),
                "Date",
                "Price",
                dataset,
                PlotOrientation.VERTICAL, // Changed to vertical for better visualization
                true,
                true,
                false
        );
        lineChart.getPlot().setBackgroundPaint(new Color(31, 41, 55));

        try {
            return ChartUtils.encodeAsPNG(lineChart.createBufferedImage(1300, 600));
        } catch (Exception e) {
            System.err.println("Error while encoding as PNG");
            e.printStackTrace(); // Print stack trace to identify the issue
        }

        return new byte[]{};
    }
}
