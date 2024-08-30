package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
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
    public SlashCommandData getSlashCommand() {
        return Commands.slash("ban", "Removes a user from a server, blocking rejoin. Optionally allows rejoin after a set timeframe.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                .addOption(OptionType.USER, "member", "Who you want to ban", true)
                .addOption(OptionType.NUMBER,"timeframe", "How long in specified time frame (default days)", true)
                .addOptions(
                        new OptionData(OptionType.STRING, "unit", "Unit for ban timeframe", false)
                                .addChoice("Minutes", "minutes")
                                .addChoice("Hours", "hours")
                                .addChoice("Days", "days")
                )
                .addOption(OptionType.NUMBER, "delete", "Timeframe for message deletion", false)
                .addOption(OptionType.STRING, "reason", "Why you are banning them", false)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
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