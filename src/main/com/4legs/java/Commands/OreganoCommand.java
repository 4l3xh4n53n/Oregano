package Commands;

import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

/**
 * Interface for the bots commands
 */
public interface OreganoCommand {

    /**
     * Gets the type of command ( Administrative, Fun, etc... )
     * @return CommandType ( Type Of Command )
     */
    CommandType getType();

    /**
     * Gets an example of the command e.g. COMMAND (ARGS)
     * @return String ( Example Command )
     */
    String getExample();

    /**
     * Gets information of what the command does
     * @return String ( Command Information )
     */
    String getPurpose();

    /**
     * Returns Discord's built-in permission for the command
     * @return Permission
     */
    Permission getBuiltInPermission();

    /**
     * Returns the data in order to register the on Discord
     * @return SlashCommandData
     */
    SlashCommandData getSlashCommand();

    /**
     * Code to run when a slash command is used
     */
    String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options);

    /**
     * Executes the command
     */
    @Deprecated
    @ReplaceWith("onSlashCommand")
    String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args);

}
