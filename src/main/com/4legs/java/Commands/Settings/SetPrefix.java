package Commands.Settings;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPrefix extends SettingsCommand{
    @Override
    public String getExample() {
        return "setprefix {newPrefix}";
    }

    @Override
    public String getPurpose() {
        return "Allows you to change the bots prefix";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        if (args.length < 1) return "You have not included enough args for this command";
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command.";

        if (args.length > 10) return "Prefix must be under 10 characters long.";
        SettingsManager.setGuildsPrefix(guildID, args[0]);

        return "Prefix set to: " + args[0];
    }
}
