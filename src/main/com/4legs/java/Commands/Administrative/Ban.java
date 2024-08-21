package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ban extends AdministrativeCommand {

    @Override
    public String getExample() {
        return "ban {ID/@USERNAME/TAG} <REASON>";
    }

    @Override
    public String getPurpose() {
        return "Bans a user which removes them from the guild and stops them from joining back.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.BAN_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "ban", args, Permission.BAN_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        String reason = getReason(args, 1);

        c.mentioned().ban(0, reason).queue();
        return "Member has been banned";

    }

}
