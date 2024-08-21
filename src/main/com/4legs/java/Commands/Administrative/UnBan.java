package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnBan extends Ban {

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
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "ban", args, Permission.BAN_MEMBERS, 1, false);
        if (!c.checksSuccessful()) return c.checksMessage();

        guild.unban(c.mentioned()).queue();
        return "Member has been un-banned.";

    }

}
