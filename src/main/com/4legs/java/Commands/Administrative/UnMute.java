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
    public SlashCommandData getSlashCommand() {
        return Commands.slash("unmute", "Allows a muted user to talk again.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                .addOption(OptionType.USER, "member", "Who you want to un-mute", true)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "mute", args, Permission.MODERATE_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        c.mentioned().removeTimeout().queue();

        return "Member has been un-muted";

    }
}
