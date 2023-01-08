package Commands.Administrative;

import Commands.CommandHandler;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class Mute extends AdministrativeCommand{

    public Mute(){
            CommandHandler.addCommand(this);
        }

    @Override
    public String getExample() {
            return "mute {USER} {DURATION}";
        }

    @Override
    public String getPurpose() {
        return "Times out a user for a set amount of time.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){

        if (!SettingsManager.featureIsEnabled(guildID, "mute")) return null;
        if (args.length < 2) return "You have not included enough arguments to use this command.";
        Member mentioned = AdministrativeUtilities.getMentioned(guild, message, args[0]);
        if (mentioned == null) return "Member cannot be found.";
        Member author = e.getMember();
        boolean hasPermission = AdministrativeUtilities.checkPermissions(guildID, "mute", author, mentioned, Permission.MODERATE_MEMBERS);
        if (!hasPermission) return "You do not have permission to use this command.";
        int duration = AdministrativeUtilities.stringToInt(args[1]);

        mentioned.timeoutFor(Duration.ofMinutes(duration)).queue();

        return "Member has been muted";

    }

}
