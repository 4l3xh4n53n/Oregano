package Commands.Settings;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ToggleFeature extends SettingsCommand{

    @Override
    public String getExample() {
        return "togglefeature {FEATURE NAME}";
    }

    @Override
    public String getPurpose() {
        return "Turns on or off a feature.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) { // todo test

        if (args.length < 1) return "You have not included enough args for this command";
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command.";

        Boolean featureStatus = SettingsManager.featureIsEnabled(guildID, args[0]);
        if (featureStatus == null) return "Feature does not exist";
        SettingsManager.setGuildsFeature(guildID, args[0], !featureStatus);

        return args[0] + " set to: " + !featureStatus;

    }

}
