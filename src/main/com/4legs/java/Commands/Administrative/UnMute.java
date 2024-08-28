package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnMute extends Mute {

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
        return Permission.MODERATE_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "mute", args, Permission.MODERATE_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        c.mentioned().removeTimeout().queue();

        return "Member has been un-muted";

    }
}
