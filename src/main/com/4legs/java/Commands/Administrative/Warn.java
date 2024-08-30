package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class Warn extends AdministrativeCommand{

    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getPurpose() {
        return null;
    }

    @Override
    public Permission getBuiltInPermission() {
        return null;
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return null;
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        return null;
    }
}
