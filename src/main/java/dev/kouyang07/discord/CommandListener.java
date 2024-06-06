package dev.kouyang07.discord;

import dev.kouyang07.data.items.Skin;
import dev.kouyang07.data.scraper.Scraper;
import dev.kouyang07.data.scraper.Wear;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
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
            event.deferReply().queue();
            if(!scraper.isValid()){
                event.getHook().sendMessage("Invalid skin").queue();
                return;
            }
            event.getHook().sendMessageEmbeds(scraper.flipData()).queue();
            event.getHook().sendFiles(FileUpload.fromData(scraper.generateChart(), "PriceHistoryChart.jpeg")).queue();
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
}
