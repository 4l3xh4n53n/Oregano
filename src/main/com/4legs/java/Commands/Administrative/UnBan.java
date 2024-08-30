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

public class UnBan extends Ban {

    @Override
    public String getExample() {
        return "unban {ID}";
    }

    @Override
    public String getPurpose() {
        return "Removes a ban from a user which allows them to join back after being banned.";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("ban", "Removes a user from a server and stops them joining back.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                .addOption(OptionType.USER, "member", "Who you want to ban", true)
                .addOption(OptionType.STRING, "reason", "Why you are banning them", false)
                .setGuildOnly(true);
    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "ban", args, Permission.BAN_MEMBERS, 1, false);
        if (!c.checksSuccessful()) return c.checksMessage();

        guild.unban(c.mentioned()).queue();
        return "Member has been un-banned.";

    }

}
