package Commands.Administrative;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TempBan extends AdministrativeCommand{

    @Override
    public String getExample() {
        return "tempban {ID/@USERNAME/TAG} <TIME(days)> <REASON>";
    }

    @Override
    public String getPurpose() {
        return "Bans a user for a certain amount of time measured in days.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.BAN_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){

        if (!SettingsManager.featureIsEnabled(guildID, "ban")) return null;
        if (args.length < 2) return "You have not included enough arguments to use this command.";
        Member mentioned = getMentioned(guild, message, args[0]);
        if (mentioned == null) return "Member cannot be found.";
        Member author = e.getMember();
        boolean hasPermission = checkPermissions(guildID, "ban", author, mentioned, Permission.BAN_MEMBERS);
        if (!hasPermission) return "You do not have permission to use this command.";
        int length = stringToInt(args[1]);
        if (length < 0) return "Ban duration is invalid.";

        List<String> argsList = new LinkedList<>(Arrays.asList(args));
        argsList.remove(0);
        argsList.remove(0);
        String reason = String.join(" ", argsList);

        mentioned.ban(length, reason).queue();
        return "Member has been temporarily banned";

    }

}
