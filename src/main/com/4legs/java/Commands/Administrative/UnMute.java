package Commands.Administrative;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnMute extends AdministrativeCommand {

    @Override
    public String getExample() {
        return "unmute {USER}";
    }

    @Override
    public String getPurpose() {
        return "Removes a time out from a user.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return null; // todo change to time out
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {

        if (!SettingsManager.featureIsEnabled(guildID, "mute")) return null;
        if (args.length < 1) return "You have not included enough arguments to use this command.";
        Member mentioned = getMentioned(guild, message, args[0]);
        if (mentioned == null) return "Member cannot be found.";
        Member author = e.getMember();
        boolean hasPermission = checkPermissions(guildID, "mute", author, mentioned, Permission.MODERATE_MEMBERS);
        if (!hasPermission) return "You do not have permission to use this command.";

        mentioned.removeTimeout().queue();

        return "Member has been un-muted";

    }
}
