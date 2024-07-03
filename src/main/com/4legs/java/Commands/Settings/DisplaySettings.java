package Commands.Settings;

import Commands.Administrative.AdministrativeCommand;
import Commands.CommandHandler;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;

public class DisplaySettings extends AdministrativeCommand {

    public DisplaySettings(){
        CommandHandler.addCommand(this);
    }

    @Override
    public String getExample() {
        return "displaysettings O{ROLES}";
    }

    @Override
    public String getPurpose() {
        return "Shows whether a setting is turned on or off. Optionally whether it uses roles or build in permissions.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        String[] features = SettingsManager.getFeatures();
        StringBuilder output = new StringBuilder();

        System.out.println(args);

        if (args.length >= 1) {

            if (!args[0].toLowerCase(Locale.ROOT).equals("roles")) return "Unknown argument";

            for (String featureName : features) {

                // todo finish this, going to change the roles to all be in one big list

            }

        } else {

            // Display toggle features

            boolean featureEnabled;

            for (String featureName : features) {
                featureEnabled = SettingsManager.featureIsEnabled(guildID, featureName);

                if (featureEnabled) output.append(":green_square:").append(featureName).append("\n");
                else output.append(":red_square: ").append(featureName).append("\n");
            }

        }
        return output.toString();
    }

}
