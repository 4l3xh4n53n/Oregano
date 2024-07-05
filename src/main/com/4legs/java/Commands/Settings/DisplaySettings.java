package Commands.Settings;

import Commands.Administrative.AdministrativeCommand;
import Commands.CommandHandler;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;

public class DisplaySettings extends AdministrativeCommand {

    public DisplaySettings(){
        CommandHandler.addCommand(this);
    }

    @Override
    public String getExample() {
        return "displaysettings Opt{ROLES} Opt{CommandType/S}";
    }

    @Override
    public String getPurpose() {
        return "Shows whether a setting is turned on or off. Optionally which permissions it requires.";
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.BAN_MEMBERS;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        String[] features = SettingsManager.getFeatures();
        StringBuilder output = new StringBuilder();


        // TEST VALUES
        SettingsManager.setRequiredRoles(guildID, "ban", new String[]{"850115918486044753", "849020974493728848"});
        SettingsManager.setRequiredRoles(guildID, "kick", new String[]{"850115918486044753"});
        SettingsManager.setCommandUsesPermission(guildID, "purge", true);

        if (args.length >= 1) {

            // todo add in filtering

            if (!args[0].toLowerCase(Locale.ROOT).equals("roles")) return "Unknown argument";

            for (String featureName : features) {

                if (SettingsManager.commandUsesPermissions(guildID, featureName)) {

                    // Displays Discord's built-in permission

                    output.append(featureName)
                            .append(" : ")
                            .append(CommandHandler.getCommandByName(featureName).getBuiltInPermission())
                            .append("\n");
                } else {

                    // Displays all role names

                    String[] roleIDs = SettingsManager.getRequiredRoles(guildID, featureName);
                    StringBuilder roleNames = new StringBuilder();

                    if (roleIDs.length == 0){
                        roleNames.append("No roles");
                    } else {
                        for (String roleID : roleIDs) {
                            Role role = guild.getRoleById(roleID);
                            if (role != null){
                                roleNames.append(role.getName()).append(" ");
                            }
                        }
                    }

                    output.append(featureName)
                            .append(" : ")
                            .append(roleNames)
                            .append("\n");
                }

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
