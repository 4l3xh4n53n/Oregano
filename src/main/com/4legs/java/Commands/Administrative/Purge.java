package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class Purge extends AdministrativeCommand{

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
    public SlashCommandData getSlashCommand() {
        return Commands.slash("purge", "Removes a specified number of messages.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
                .addOption(OptionType.INTEGER, "count", "How many messages you want to delete (including the one you send)", true)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "purge", args, Permission.MESSAGE_MANAGE, 1, false);
        if (!c.checksSuccessful()) return c.checksMessage();

        int messageCount = stringToInt(args[0]);
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
