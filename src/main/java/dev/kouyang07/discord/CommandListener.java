package dev.kouyang07.discord;

import dev.kouyang07.data.CSData;
import dev.kouyang07.data.Scraper;
import dev.kouyang07.data.structs.Item;
import dev.kouyang07.data.structs.Price;
import dev.kouyang07.data.structs.scraper.Wear;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("skin")){
            String name = Objects.requireNonNull(event.getOption("name")).getAsString();
            Wear wear = event.getOption("wear") == null ? null : Wear.valueOf(Objects.requireNonNull(event.getOption("wear")).getAsString().replace("-", "_").toUpperCase());
            boolean stattrak = event.getOption("stattrak") != null && Boolean.parseBoolean(Objects.requireNonNull(event.getOption("stattrak")).getAsString());
            Scraper scraper;
            if((wear == null) && (!stattrak)){
                scraper = new Scraper(new Item(name.split("\\|")[0], name.split("\\|")[1]));
            }else if((wear != null) && (!stattrak)){
                scraper = new Scraper(new Item(name.split("\\|")[0], name.split("\\|")[1], wear));
            }else{
                scraper = new Scraper(new Item(name.split("\\|")[0], name.split("\\|")[1], true));
            }
            if(!scraper.isValid()){
                event.getHook().sendMessage("Invalid skin at " + scraper.getUrl()).queue();
                return;
            }
            Price price = scraper.getPrice();
            Item item = scraper.getItem();
            String scraperImage = scraper.getImageURL();
            event.replyEmbeds(generateEmbed(item, price, scraperImage))
                    .addActionRow(Button.link(price.getLowestPlatform().getUrl(), "Buy"), Button.link(price.getHighestPlatform().getUrl(), "Sell")).queue();
            event.getHook().sendFiles(FileUpload.fromData(price.generateChart(), "chart.png")).queue();
        }else if(event.getName().equals("debug")){
            event.replyEmbeds(
                    new EmbedBuilder()
                            .addField("Skins parsed", CSData.itemSet.size() + " skins", false)
                            .build()
            ).queue();
        }
    }
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("skin") && event.getFocusedOption().getName().equals("name")) {
            String input = event.getFocusedOption().getValue();
            var options = CSData.itemSet.stream()
                    .filter(item -> item.generateShortName().toLowerCase().contains(input.toLowerCase()))
                    .limit(25)
                    .map(name -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(name.generateShortName(), name.generateShortName()))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }else if (event.getName().equals("skin") && event.getFocusedOption().getName().equals("wear")) {
            String input = event.getFocusedOption().getValue();
            var options = Arrays.stream(Wear.values())
                    .map(Wear::getName)
                    .filter(name -> name.toLowerCase().contains(input.toLowerCase()))
                    .map(name -> new Command.Choice(name, name))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    public MessageEmbed generateEmbed(Item item, Price price, String imageURL) {
        if (price.getLowestPlatform() == null || price.getHighestPlatform() == null || (price.getLowestPlatform() == price.getHighestPlatform())) {
            return new EmbedBuilder()
                    .setTitle(item.generateShortName() + " : __NOT RECOMMENDED__")
                    .addField("Attributes", (item.getWear() != null ? " (" + item.getWear().getName() + ")" : "") + "\n"+ (item.isStatTrak() ? " (StatTrak)" : ""), false)
                    .addField(generateHistoricalDataField(price))
                    .setColor(price.getPriceStatistics().getPriceChange() > 0 ? Color.GREEN : Color.RED)
                    .build();
        }
        return new EmbedBuilder()
                .setTitle(item.generateShortName() + " : ~ $" + (int) (price.getHighestPlatform().getPrice() - price.getLowestPlatform().getPrice()))
                .addField("Attributes", ("Wear: `" + (item.getWear() != null ? " (" + item.getWear() + ")" : "Factory New")  + "`\nStatTrak: `" + (item.isStatTrak() ? " (True)`" : "(False)`")), false)
                .setImage(imageURL)
                .addField("Recommended action",
                        "\uD83D\uDED2 Buy: `" + price.getLowestPlatform().getName() + "@" + price.getLowestPlatform().getPrice() + "`\n\uD83D\uDCB0 Sell: `" + price.getHighestPlatform().getName() + "@" + price.getHighestPlatform().getPrice() + "` (Fees included)", false)
                .setColor(price.getPriceStatistics().getPriceChange() > 0 ? Color.GREEN : Color.RED)
                .addField(generateHistoricalDataField(price))
                 .build();
    }

    private MessageEmbed.Field generateHistoricalDataField(Price price){
        return new MessageEmbed.Field("Historical Data", "Current Price: `$" + price.getPriceStatistics().getCurrentPrice() + "`"+ "\n" +
                "24h Price Change: `$" + price.getPriceStatistics().getPriceChange() + "`\n" +
                "24h Trading Volume: `$" + price.getPriceStatistics().getTradingVolume() + "`\n" +
                "Market Cap: `$" + price.getPriceStatistics().getMarketCap() + "`\n" +
                "Volume / Market Cap: `$" + price.getPriceStatistics().getVolumeMarketCap() + "`\n" +
                "30d High: `$" + price.getPriceStatistics().getHigh() + "`\n" +
                "30d Low: `$" + price.getPriceStatistics().getLow() + "`\n" +
                "30d Average `$" + (price.getPriceStatistics().getHigh() + price.getPriceStatistics().getLow()) / 2 + "`\n" +
                "All Time High: `$" + price.getPriceStatistics().getAllTimeHigh() + "`\n" +
                "All Time Low: `$" + price.getPriceStatistics().getAllTimeLow() + "`", false);
    }
}
