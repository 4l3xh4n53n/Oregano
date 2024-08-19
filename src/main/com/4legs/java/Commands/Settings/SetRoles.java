package Commands.Settings;

import Configurations.SettingsManager;
import Main.Utilities;
import Util.Tuple;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetRoles extends SettingsCommand {

    @Override
    public String getExample() {
        return "setroles warn {@role or roleID} ...";
    }

    @Override
    public String getPurpose() {
        return "Allows admins to allow members with certain roles to use certain features.";
    }

    /**
     * Returns a list of roles and a list of unknown roles.
     * @param message The message sent from the member
     * @param guild The guild the message was sent in
     * @param args Message arguments
     * @return List of Roles and List of Strings
     */
    protected Tuple<List<Role>, List<String>> getRolesAndUnknownRoles(Message message, Guild guild, String[] args){
        List<Role> roles = new ArrayList<>(message.getMentions().getRoles());
        List<String> unknownRoles = new ArrayList<>();
        Role role;

        for (int i = 1; i < args.length; i++){
            if (Utilities.isNumeric(args[i])){
                role = guild.getRoleById(args[i]);
                if (role == null) {
                    unknownRoles.add(args[i]);
                    continue;
                }
                roles.add(role);
            }
        }

        return new Tuple<>(roles, unknownRoles);
    }

    /**
     * Checks that the user has enough permissions and command contains the correct amount of arguments.
     * @param e MessageReceivedEvent
     * @param args String[] message arguments
     * @return null if successful, a String containing a response if unsuccessful
     */
    protected String checks(MessageReceivedEvent e, String[] args){
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command.";
        if (args.length < 2) return "You have not included enough args for this command.";
        return null; // Success
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        String checksResult = checks(e, args);
        if (checksResult != null) return checksResult;

        String featureName = args[0].toLowerCase(Locale.ROOT);
        Boolean featureIsEnabled = SettingsManager.featureIsEnabled(guildID, featureName);
        if (featureIsEnabled == null) return "Feature does not exist";

        Tuple<List<Role>, List<String>> rolesAndUnknownRoles = getRolesAndUnknownRoles(message, guild, args);
        List<Role> roles = rolesAndUnknownRoles.x();
        List<String> unknownRoles = rolesAndUnknownRoles.y();

        String[] roleIDs = new String[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
            roleIDs[i] = roles.get(i).getId();
        }

        SettingsManager.setRequiredRoles(guildID, featureName, roleIDs);

        // Make response

        StringBuilder output = new StringBuilder("Roles updated for ");
        output.append(featureName).append(": ");

        for (Role r : roles){
            output.append(r.getName()).append(" ");
        }

        if (!featureIsEnabled) {
            output.append("\nWARNING: ").append(featureName).append(" is not enabled!");
        }

        if (unknownRoles.size() > 0){
            output.append("\nUnknown roles: ");
            for (String r : unknownRoles) {
                output.append(r);
            }
        }

        return output.toString();
    }
}
