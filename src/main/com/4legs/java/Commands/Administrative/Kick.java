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

import java.util.List;

public class Kick extends AdministrativeCommand {

    @Override
    public String getExample() {
        return "kick {ID/@USERNAME/TAG}";
    }

    @Override
    public String getPurpose() {
        return "Kicks a user, which removes them from the guild but allows them to join back.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.KICK_MEMBERS;
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("kick", "Removes a user from a server but allows them to join back.")
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
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "kick", args, Permission.KICK_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        String reason = getReason(args, 1);

        guild.kick(c.mentioned()).reason(reason).queue();
        return "Member has been kicked";

    }

}
