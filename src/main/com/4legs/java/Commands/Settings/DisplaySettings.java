package Commands.Settings;

import Commands.CommandHandler;
import Commands.CommandType;
import Configurations.SettingsManager;
import Main.Utilities;
import Util.Tuple;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Locale;

public class DisplaySettings extends SettingsCommand {

    @Override
    public String getExample() {
        return "displaysettings Opt{ROLES} Opt{CommandType} ...";
    }

    @Override
    public String getPurpose() {
        return "Shows whether a setting is turned on or off. Optionally which permissions it requires.";
    }

    private static String showRoles(String[] features, String guildID, Guild guild, boolean filtered, String[] filter) {
        StringBuilder output = new StringBuilder();

        for (String featureName : features) {

            if (filtered) {
                CommandType commandType = CommandHandler.getCommandByName(featureName).getType();

                if (Arrays.stream(filter).noneMatch(x -> x.equals(commandType.toString()))) {
                    continue;
                }
            }

            if (SettingsManager.commandUsesPermissions(guildID, featureName)) { // Displays Discord's built-in permission

                output.append(featureName)
                        .append(" : ")
                        .append(CommandHandler.getCommandByName(featureName).getBuiltInPermission())
                        .append("\n");

            } else { // Displays all role names

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

        return output.toString();
    }

    private static String showFeatures(String[] features, String guildID, boolean filtered, String[] filter) {
        StringBuilder output = new StringBuilder();
        boolean featureEnabled;

        for (String featureName : features) {
            if (filtered) {
                CommandType commandType = CommandHandler.getCommandByName(featureName).getType();

                if (Arrays.stream(filter).noneMatch(x -> x.equals(commandType.toString()))) {
                    continue;
                }
            }

            featureEnabled = SettingsManager.featureIsEnabled(guildID, featureName);

            if (featureEnabled) output.append(":green_square:").append(featureName).append("\n");
            else output.append(":red_square:").append(featureName).append("\n");
        }

        return output.toString();
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        String[] features = SettingsManager.getFeatures();
        StringBuilder output = new StringBuilder();
        String result;

        if (args.length >= 1){

            // Shows permissions/roles required to use a feature with/without filter
            if (args[0].toLowerCase(Locale.ROOT).equals("roles")) {

                if (args.length < 3) { // No filter
                    result = showRoles(features, guildID, guild, false, new String[]{});
                } else { // Filter

                    Tuple<String[], String[]> createFilterResult = Utilities.createFilter(args, 1);
                    String[] filter = createFilterResult.x();
                    String[] unknownFeatureTypes = createFilterResult.y();
                    if (unknownFeatureTypes.length > 0) {
                        output.append("Unknown feature types: ")
                                .append(Arrays.toString(unknownFeatureTypes).replace("[", "").replace("]", ""))
                                .append("\n");
                    }

                    result = showRoles(features, guildID, guild, true, filter);

                }
            } else { // Shows whether a feature is on or off with filter

                Tuple<String[], String[]> createFilterResult = Utilities.createFilter(args, 0);
                String[] filter = createFilterResult.x();
                String[] unknownFeatureTypes = createFilterResult.y();
                if (unknownFeatureTypes.length > 0) {
                    output.append("Unknown feature types: ")
                            .append(Arrays.toString(unknownFeatureTypes))
                            .append("\n");
                }

                result = showFeatures(features, guildID, true, filter);

            }
        } else { // Displays whether a feature is on or off without filter
            result = showFeatures(features, guildID, false, new String[]{});
        }

        output.append(result);
        return output.toString();
    }

}
