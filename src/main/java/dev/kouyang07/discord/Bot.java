package dev.kouyang07.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    JDA jda;

    public Bot() {
        jda = JDABuilder.createDefault("MTI0NjI4NDU2MTM5NTA5MzU2NQ.GU2NPN.DAKeHg05w060wmlvY1SLyfwpnzf3MxkPWP5avI")
                .setActivity(Activity.watching("CS:GO Skins"))
                .addEventListeners(new CommandListener())
                .build();

        List<CommandData> commandData = new ArrayList<>();
        OptionData skin = new OptionData(OptionType.STRING, "skin", "The skin you want to search for", true);
        commandData.add(Commands.slash("skin","Search for data regarding the specific skin").addOptions(skin));

        jda.updateCommands().addCommands(commandData).queue();


    }

}
