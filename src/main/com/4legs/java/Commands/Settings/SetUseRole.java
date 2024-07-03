package Commands.Settings;

import Commands.CommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetUseRole extends SettingsCommand{

    public SetUseRole() {
        CommandHandler.addCommand(this);
    }
    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getPurpose() {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        return null;
    }
}
