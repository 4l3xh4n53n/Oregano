package Commands.Settings;

import Configurations.SettingsManager;
import Util.Tuple;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RemoveRole extends SetRoles {
    @Override
    public String getExample() {
        return "removerole {FEATURE NAME} {@role or roleID} ...";
    }

    @Override
    public String getPurpose() {
        return "Removes role(s) to list of roles that can use a certain feature.";
    }

    /*

     */

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        // Check member has permissions and args length

        String checksResult = checks(e, args);
        if (checksResult != null) return checksResult;

        // Check feature is valid

        String featureName = args[0].toLowerCase(Locale.ROOT);
        Boolean featureIsEnabled = SettingsManager.featureIsEnabled(guildID, featureName);
        if (featureIsEnabled == null) return "Feature does not exist";

        // Create a list of roles

        Tuple<List<Role>, List<String>> rolesAndUnknownRoles = getRolesAndUnknownRoles(message, guild, args);
        List<String> mentionedRoles = rolesAndUnknownRoles.x().stream().map(ISnowflake::getId).toList();
        List<String> unknownRoles = rolesAndUnknownRoles.y();

        if (mentionedRoles.isEmpty()) return "You have not mentioned any valid roles";

        List<String> currentRoleRequirements = Arrays.stream(SettingsManager.getRequiredRoles(guildID, featureName)).toList();
        List<String> newRoleRequirements = new ArrayList<>();

        // Remove roles that currently exist in settings

        for (String roleID : currentRoleRequirements) {
            if (!mentionedRoles.contains(roleID)) newRoleRequirements.add(roleID);
        }

        SettingsManager.setRequiredRoles(guildID, featureName, newRoleRequirements.toArray(new String[0]));

        return makeResponse(featureName, newRoleRequirements, unknownRoles, featureIsEnabled, guild);
    }
}
