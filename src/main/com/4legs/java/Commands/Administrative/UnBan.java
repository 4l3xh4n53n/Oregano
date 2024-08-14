package Commands.Administrative;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnBan extends AdministrativeCommand{

    @Override
    public String getExample() {
        return "unban {ID}";
    }

    @Override
    public String getPurpose() {
        return "Removes a ban from a user which allows them to join back after being banned.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.BAN_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){

        if (!SettingsManager.featureIsEnabled(guildID, "ban")) return null;
        if (args.length < 1) return "You have not included enough arguments to use this command.";
        Member mentioned = getMentioned(guild, message, args[0]);
        if (mentioned == null) return "Member cannot be found.";
        Member author = e.getMember();
        boolean hasPermission = checkPermissions(guildID, "ban", author, mentioned, Permission.BAN_MEMBERS);
        if (!hasPermission) return "You do not have permission to use this command.";
        guild.unban(mentioned).queue();
        return "Member has been un-banned";

    }

}
