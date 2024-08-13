package Commands.Settings;

import Commands.Administrative.AdministrativeCommand;
import Commands.CommandHandler;
import Commands.CommandType;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private static String showRoles(String[] features, String guildID, Guild guild, boolean filtered, String[] filter) {

        StringBuilder output = new StringBuilder();

        for (String featureName : features) {

            if (filtered) {
                CommandType commandType = CommandHandler.getCommandByName(featureName).getType();

                if (Arrays.stream(filter).noneMatch(x -> x.equals(commandType.toString()))) {
                    continue;
                }
            }

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

        return output.toString();

    }

    private static String showFeatures(String[] features, String guildID, boolean filtered, String[] filter) {
        StringBuilder output = new StringBuilder();

        boolean featureEnabled;

        for (String featureName : features) {
            System.out.println("Feature name: " + featureName);

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

    private static String[][] createFilter(String[] args, int argStart) {
        String[] expectedCommandTypes = Arrays.stream(CommandType.values()).map(Enum::name).toArray(String[]::new);
        List<String> filter = new ArrayList<>(); // Filters display by command type
        List<String> unknownFilterTypes = new ArrayList<>(); // Used to display invalid input from user

        for (int i = argStart; i < args.length; i++) {

            // Checks that argument is a valid type of feature, e.g. ADMINISTRATIVE,
            String filterItem = args[i].toUpperCase();
            if (Arrays.asList(expectedCommandTypes).contains(filterItem)) {
                filter.add(filterItem);
            } else {
                unknownFilterTypes.add(filterItem);
            }

        }

        String[] arrFilter = filter.toArray(String[]::new);
        String[] arrUnknownFilterTypes = unknownFilterTypes.toArray(String[]::new);

        return new String[][]{arrFilter, arrUnknownFilterTypes};
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        String[] features = SettingsManager.getFeatures();
        StringBuilder output = new StringBuilder();
        String result;

        // TEST VALUES
        SettingsManager.setRequiredRoles(guildID, "ban", new String[]{"850115918486044753", "849020974493728848"});
        SettingsManager.setRequiredRoles(guildID, "kick", new String[]{"850115918486044753"});
        SettingsManager.setCommandUsesPermission(guildID, "purge", true);
        SettingsManager.setGuildsFeature(guildID, "purge", true);

        if (args.length >= 1){

            // Shows permissions/roles required to use a feature with/without filter
            if (args[0].toLowerCase(Locale.ROOT).equals("roles")) {

                if (args.length < 3) { // No filter
                    System.out.println("ROLES NO FILTER");

                    result = showRoles(features, guildID, guild, false, new String[]{});
                } else { // Filter
                    System.out.println("ROLES WITH FILTER");

                    String[][] createFilterResult = createFilter(args, 1);
                    String[] filter = createFilterResult[0];
                    String[] unknownFeatureTypes = createFilterResult[1];
                    if (unknownFeatureTypes.length > 0) {
                        output.append("Unknown feature types: ")
                                .append(Arrays.toString(unknownFeatureTypes))
                                .append("\n");
                    }

                    result = showRoles(features, guildID, guild, true, filter);
                }
            } else { // Shows whether a feature is on or off with filter
                System.out.println("TOGGLE WITH FILTER");

                String[][] createFilterResult = createFilter(args, 0);
                String[] filter = createFilterResult[0];
                String[] unknownFeatureTypes = createFilterResult[1];
                if (unknownFeatureTypes.length > 0) {
                    output.append("Unknown feature types: ")
                            .append(Arrays.toString(unknownFeatureTypes))
                            .append("\n");
                }

                result = showFeatures(features, guildID, true, filter);

            }
        } else { // Displays whether a feature is on or off without filter
            System.out.println("TOGGLE NO FILTER");
            result = showFeatures(features, guildID, false, new String[]{});
        }

        output.append(result);

        return output.toString();
    }

}
