package Commands.Administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
    public SlashCommandData getSlashCommand() {
        return Commands.slash("ban", "Removes a user from a server, blocking rejoin. Optionally allows rejoin after a set timeframe.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                .addOption(OptionType.USER, "member", "Who you want to ban", true)
                .addOption(OptionType.NUMBER, "delete", "Timeframe for message deletion", false)
                .addOptions(
                        new OptionData(OptionType.STRING, "banunit", "Unit for ban timeframe", false)
                                .addChoice("Minutes", "minutes")
                                .addChoice("Hours", "hours")
                                .addChoice("Days", "days"),
                        new OptionData(OptionType.STRING, "delunit", "Unit for deletion timeframe", false)
                                .addChoice("Minutes", "minutes")
                                .addChoice("Hours", "hours")
                                .addChoice("Days", "days")
                                )
                .addOption(OptionType.STRING, "reason", "Why you are banning them", false)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        OptionMapping optionMember = e.getOption("member");
        OptionMapping optionReason = e.getOption("reason");
        Member member;
        String reason = null;

        if (optionReason != null) reason = optionReason.getAsString();

        assert optionMember != null; // It is required option
        member = optionMember.getAsMember();

        if (member == null) return "Ge- you didn't mention a real member wallahi";

        member.ban(1, TimeUnit.DAYS).reason(reason).queue();

        if (reason == null) return member + " has been banned";
        else return member + " has been banned for " + reason;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "ban", args, Permission.BAN_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        String reason = getReason(args, 1);

        c.mentioned().ban(0, TimeUnit.DAYS).reason(reason).queue();
        return "Member has been banned";

    }

}
