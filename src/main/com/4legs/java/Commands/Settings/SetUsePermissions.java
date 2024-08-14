package Commands.Settings;

import Commands.CommandHandler;
import Commands.OreganoCommand;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;

public class SetUsePermissions extends SettingsCommand {

    @Override
    public String getExample() {
        return "setusepermissions {feature name} {true/false or 1/0}";
    }

    @Override
    public String getPurpose() {
        return "Allows admins to say whether a feature requires roles or permissions to use.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        Member author = message.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command";

        if (args.length < 2) return "You have not included enough args for this command.";

        String featureName = args[0].toLowerCase(Locale.ROOT);
        String option = args[1].toLowerCase(Locale.ROOT);

        // check this because otherwise it can try to update settings that do not exist
        Boolean featureIsEnabled = SettingsManager.featureIsEnabled(guildID, featureName);
        if (featureIsEnabled == null) return "Feature does not exist";

        OreganoCommand feature = CommandHandler.getCommandByName(featureName);
        if (feature.getBuiltInPermission() == null) return "Feature does not support permissions.";

        boolean usesPermissions;
        if (option.equals("true") || option.equals("1")) usesPermissions = true;
        else if (option.equals("false") || option.equals("0")) usesPermissions = false;
        else return "Unknown option: " + option;

        SettingsManager.setCommandUsesPermission(guildID, featureName, usesPermissions);

        if (featureIsEnabled) return featureName + " uses roles set to: " + usesPermissions;
        else return featureName + " uses roles set to: " + usesPermissions + "\nWarning: " + featureName + " is not enabled!";
    }
}
