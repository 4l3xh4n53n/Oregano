package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WarnList extends Warn {

    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getPurpose() {
        return null;
    }

    @Override
    public Permission getBuiltInPermission() {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        return null;
    }

}
