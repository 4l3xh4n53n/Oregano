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
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.Duration;
import java.util.List;

public class Mute extends AdministrativeCommand{

    @Override
    public String getExample() {
            return "mute {USER} {DURATION}";
        }

    @Override
    public String getPurpose() {
        return "Times out a user for a set amount of time.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.MODERATE_MEMBERS;
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("mute", "Stops a user from talking.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                .addOption(OptionType.USER, "member", "Who you want to kick", true)
                .addOption(OptionType.STRING, "reason", "Why you are kicking them", false)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "mute", args, Permission.MODERATE_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage(); // todo Permission.MODERATE_MEMBERS has changed

        int duration = stringToInt(args[1]);

        c.mentioned().timeoutFor(Duration.ofMinutes(duration)).queue();

        return "Member has been muted";

    }

}
