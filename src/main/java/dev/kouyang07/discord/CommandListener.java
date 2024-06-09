package dev.kouyang07.discord;

import dev.kouyang07.data.items.Skin;
import dev.kouyang07.data.scraper.Platforms;
import dev.kouyang07.data.scraper.PriceStatistics;
import dev.kouyang07.data.scraper.Scraper;
import dev.kouyang07.data.scraper.Wear;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

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
                scraper = new Scraper(name);
            }else if((wear != null) && (!stattrak)){
                scraper = new Scraper(name, wear);
            }else if(wear == null){
                scraper = new Scraper(name, true);
            }
            else{
                scraper = new Scraper(name, wear, true);
            }
            if(!scraper.isValid()){
                event.getHook().sendMessage("Invalid skin").queue();
                return;
            }

            String scraperItem = scraper.getItem();
            Wear scraperWear = scraper.getWear();
            boolean scraperStatTrak = scraper.getStatTrak();
            Platforms[] scraperPlatforms = scraper.filterPlatforms();
            PriceStatistics scraperPriceStatistics = scraper.scrapePriceStatistics();

            event.replyEmbeds(generateEmbed(scraperItem, scraperWear, scraperStatTrak, scraperPlatforms, scraperPriceStatistics))
                    .addActionRow(Button.link(scraperPlatforms[0].getUrl(), "Buy"), Button.link(scraperPlatforms[1].getUrl(), "Sell")).queue();
            event.getHook().sendFiles(FileUpload.fromData(scraper.generateChart(), "chart.png")).queue();
        }else if(event.getName().equals("debug")){
            event.replyEmbeds(
                    new EmbedBuilder()
                            .addField("Skins parsed", Skin.allSkins.size() + " skins", false)
                            .build()
            ).queue();
        }
    }
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("skin") && event.getFocusedOption().getName().equals("name")) {
            String input = event.getFocusedOption().getValue();
            var options = Skin.allSkins.keySet().stream()
                    .filter(name -> name.toLowerCase().contains(input.toLowerCase()))
                    .limit(25)
                    .map(name -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(name, name))
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

    public MessageEmbed generateEmbed(String item, Wear wear, boolean statTrack, Platforms[] platforms, PriceStatistics priceStatistics) {
        if (platforms[0] == null || platforms[1] == null || (platforms[0] == platforms[1])) {
            return new EmbedBuilder()
                    .setTitle(item.replace("-", " ") + " : __NOT RECOMMENDED__")
                    .addField("Attributes", (wear != null ? " (" + wear.getName() + ")" : "") + "\n"+ (statTrack ? " (StatTrak)" : ""), false)
                    .addField("Historical Data", "Current Price: `$" + priceStatistics.getCurrentPrice() + "`\n" +
                            "24h Price Change: `$" + priceStatistics.getPriceChange() + "`\n" +
                            "24h Trading Volume: `$" + priceStatistics.getTradingVolume() + "`\n" +
                            "Market Cap: `$" + priceStatistics.getMarketCap() + "`\n" +
                            "Volume / Market Cap: `$" + priceStatistics.getVolumeMarketCap() + "`\n" +
                            "30d High: `$" + priceStatistics.getHigh() + "`\n" +
                            "30d Low: `$" + priceStatistics.getLow() + "`\n" +
                            "30d Average `$" + (priceStatistics.getHigh() + priceStatistics.getLow()) / 2 + "`\n" +
                            "All Time High: `$" + priceStatistics.getAllTimeHigh() + "`\n" +
                            "All Time Low: `$" + priceStatistics.getAllTimeLow() + "`", true)
                    .build();
        }
        return new EmbedBuilder()
                .setTitle(item.replace("-", " ") + " : ~ $" + (int) (platforms[1].getPrice() - platforms[0].getPrice()))
                .addField("Attributes", ("Wear: " + (wear != null ? " (" + wear.getName() + ")" : "Factory New")  + "\nStatTrak: " + (statTrack ? " (True)" : "(False)")), false)
                .addField("Recommended action",
                        "\uD83D\uDED2 Buy: `" + platforms[0].getName() + "@" + platforms[0].getPrice() + "`\n\uD83D\uDCB0 Sell: `" + platforms[1].getName() + "@" + platforms[1].getPrice() + " (Fees included)`", false)

                .addField("Historical Data", "Current Price: `$" + priceStatistics.getCurrentPrice() + "`"+ "\n" +
                        "24h Price Change: `$" + priceStatistics.getPriceChange() + "`\n" +
                        "24h Trading Volume: `$" + priceStatistics.getTradingVolume() + "`\n" +
                        "Market Cap: `$" + priceStatistics.getMarketCap() + "`\n" +
                        "Volume / Market Cap: `$" + priceStatistics.getVolumeMarketCap() + "`\n" +
                        "30d High: `$" + priceStatistics.getHigh() + "`\n" +
                        "30d Low: `$" + priceStatistics.getLow() + "`\n" +
                        "30d Average `$" + (priceStatistics.getHigh() + priceStatistics.getLow()) / 2 + "`\n" +
                        "All Time High: `$" + priceStatistics.getAllTimeHigh() + "`\n" +
                        "All Time Low: `$" + priceStatistics.getAllTimeLow() + "`", false)
                 .build();
    }

}
