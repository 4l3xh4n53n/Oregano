package Commands.Settings;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetFeature extends SettingsCommand{

    @Override
    public String getExample() {
        return "setfeature {FEATURE NAME} {on/off or 1/0}";
    }

    @Override
    public String getPurpose() {
        return "Turns on or off a feature.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {

        if (args.length < 2) return "You have not included enough args for this command";
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command.";

        Boolean featureStatus = SettingsManager.featureIsEnabled(guildID, args[0]);
        if (featureStatus == null) return "Feature does not exist";

        String option = args[1];

        if (option.equals("on") || option.equals("1")) featureStatus = true;
        else if (option.equals("off") || option.equals("0")) featureStatus = false;
        else return "Unknown option: " + option;

        SettingsManager.setGuildsFeature(guildID, args[0], featureStatus);

        return args[0] + " set to: " + (featureStatus ? "on" : "off");

    }

}
