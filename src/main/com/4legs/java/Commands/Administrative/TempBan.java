package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class TempBan extends Ban{

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

        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "ban", args, Permission.BAN_MEMBERS, 2, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        int length = stringToInt(args[1]);
        if (length < 0) return "Ban duration is invalid.";

        String reason = getReason(args, 2);

        c.mentioned().ban(length, TimeUnit.DAYS).reason(reason).queue();
        return "Member  has been temporarily banned.";

    }

}
