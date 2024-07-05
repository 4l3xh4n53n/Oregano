package Commands.Administrative;

import Commands.CommandHandler;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Purge extends AdministrativeCommand{

    public Purge(){
        CommandHandler.addCommand(this);
    }

    @Override
    public String getExample() {
        return "purge {COUNT}";
    }

    @Override
    public String getPurpose() {
        return "Bulk deletes a specified amount of messages.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.MESSAGE_MANAGE;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){

        if (!SettingsManager.featureIsEnabled(guildID, "purge")) return null;
        if (args.length < 1) return "You have not included enough arguments to use this command.";
        Member author = e.getMember();
        boolean hasPermission = AdministrativeUtilities.checkPermissions(guildID, "purge", author, Permission.MESSAGE_MANAGE);
        if (!hasPermission) return "You do not have permission to use this command.";

        int messageCount = AdministrativeUtilities.stringToInt(args[0]);
        if (messageCount < 0) return "Message count must be numerical and above 0";

        List<Message> messagesToBeRemoved;

        ChannelType type = e.getChannel().getType();
        if (type == ChannelType.TEXT){
            TextChannel channel = e.getChannel().asTextChannel();
            messagesToBeRemoved = channel.getHistory().retrievePast(messageCount + 1).complete();
            channel.deleteMessages(messagesToBeRemoved).complete();
        } else {
            ThreadChannel channel = e.getChannel().asThreadChannel();
            messagesToBeRemoved = channel.getHistory().retrievePast(messageCount + 1).complete();
            channel.deleteMessages(messagesToBeRemoved).complete();
        }

        return null;

    }

}
