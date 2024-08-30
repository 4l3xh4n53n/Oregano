package Commands.Settings;

import Commands.CommandType;
import Commands.OreganoCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public abstract class SettingsCommand implements OreganoCommand {

    private final CommandType type = CommandType.SETTINGS;

    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.ADMINISTRATOR;
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return null;
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

}
