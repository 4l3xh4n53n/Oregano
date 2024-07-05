package Commands.Administrative;

import Commands.CommandHandler;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Kick extends AdministrativeCommand{

    public Kick(){
        CommandHandler.addCommand(this);
    }

    @Override
    public String getExample() {
        return "kick {ID/@USERNAME/TAG}";
    }

    @Override
    public String getPurpose() {
        return "Kicks a user, which removes them from the guild but allows them to join back.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.KICK_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){

        if (!SettingsManager.featureIsEnabled(guildID, "kick")) return null;
        if (args.length < 1) return "You have not included enough arguments to use this command.";
        Member mentioned = AdministrativeUtilities.getMentioned(guild, message, args[0]);
        if (mentioned == null) return "Member cannot be found.";
        Member author = e.getMember();
        boolean hasPermission = AdministrativeUtilities.checkPermissions(guildID, "kick", author, mentioned, Permission.KICK_MEMBERS);
        if (!hasPermission) return "You do not have permission to use this command.";

        List<String> argsList = new LinkedList<>(Arrays.asList(args));
        argsList.remove(0);
        argsList.remove(0);
        String reason = String.join(" ", argsList);

        guild.kick(mentioned, reason).queue();
        return "Member has been kicked";

    }

}
