package dev.kouyang07.discord;

import dev.kouyang07.scraper.Scraper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("skin")){
            Scraper scraper = new Scraper(Objects.requireNonNull(event.getOption("skin")).getAsString());
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(scraper.scrape()).queue();
        }
    }
}
