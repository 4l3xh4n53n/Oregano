package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
     * Executes the command
     */
    String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args);

}
