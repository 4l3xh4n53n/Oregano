package Commands.Settings;

import Configurations.SettingsManager;
import Util.Utilities;
import Util.Tuple;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
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
     * @param message   The message sent from the member
     * @param guild     The guild the message was sent in
     * @param args      Message arguments
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
     * @param e     MessageReceivedEvent
     * @param args  String[] message arguments
     * @return null if successful, a String containing a response if unsuccessful
     */
    @Deprecated
    protected String checks(MessageReceivedEvent e, String[] args){
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return "You do not have permission to run this command.";
        if (args.length < 2) return "You have not included enough args for this command.";
        return null; // Success
    }

    /**
     * Creates a RoleChecksAndData object that has failed a check
     * @param message (String)  The reason why the check has failed
     * @return  RoleChecksAndData
     */
    protected RoleChecksAndData checkFailed(String message) {
        return new RoleChecksAndData(
                false,
                message,
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Does certain checks and returns data
     * @param e         MessageReceivedEvent
     * @param message   (Message)   message sent by user
     * @param guild     (Guild)     guild that is being acted in
     * @param guildID   (String)    guilds ID
     * @param args      (String[])  message arguments
     * @return RoleChecksAndData checksSuccessful, checksMessage, featureName, featureIsEnabled, roles, unknownRoles, currentRoleRequirements
     */
    protected RoleChecksAndData checksWithRoleRequirements(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        RoleChecksAndData r = checks(e, message, guild, guildID, args); // todo rename r to c
        if (r.roles().isEmpty()) return checkFailed("You have not mentioned any valid roles");

        List<String> currentRoleRequirements = Arrays.stream(SettingsManager.getRequiredRoles(guildID, r.featureName())).toList();

        return new RoleChecksAndData(
                true,
                "",
                r.featureName(),
                r.featureIsEnabled(),
                r.roles(),
                r.unknownRoles(),
                currentRoleRequirements);
    }

    /**
     * Does certain checks and returns data
     * @param e         MessageReceivedEvent
     * @param message   (Message)   message sent by user
     * @param guild     (Guild)     guild that is being acted in
     * @param guildID   (String)    guild's id
     * @param args      (String[])  message arguments
     * @return RoleChecksAndData checksSuccessful, checksMessage, featureName, featureIsEnabled, roles, unknownRoles
     */
    protected RoleChecksAndData checks(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args){
        Member author = e.getMember();
        if (!author.hasPermission(Permission.ADMINISTRATOR)) return checkFailed("You do not have permission to run this command.");
        if (args.length < 2) return checkFailed("You have not included enough args for this command.");

        String featureName = args[0].toLowerCase(Locale.ROOT);
        Boolean featureIsEnabled = SettingsManager.featureIsEnabled(guildID, featureName);
        if (featureIsEnabled == null) return checkFailed("Feature does not exist");

        Tuple<List<Role>, List<String>> rolesAndUnknownRoles = getRolesAndUnknownRoles(message, guild, args);
        List<Role> roles = rolesAndUnknownRoles.x();
        List<String> unknownRoles = rolesAndUnknownRoles.y();

        return new RoleChecksAndData(true, "", featureName, featureIsEnabled, roles, unknownRoles, null);
    }

    /**
     * Formats a response for role update
     * @param featureName       String The name of the feature being configured
     * @param roleIDs           List of Strings roleIDs being set
     * @param unknownRoles      List of Strings of unknown ID's included in the command
     * @param featureIsEnabled  boolean Whether the feature is enabled or not
     * @param guild             Guild the command was sent from
     * @return String response message
     */
    protected String makeResponse(String featureName, List<String> roleIDs, List<String> unknownRoles, boolean featureIsEnabled, Guild guild){
        List<Role> roles = new ArrayList<>();
        for (String id : roleIDs) {
            roles.add(guild.getRoleById(id));
        }

        return makeResponse(featureName, roles, unknownRoles, featureIsEnabled);
    }

    /**
     * Formats a response for role update
     * @param featureName       String name of feature that is being configured
     * @param roles             List of Roles being set
     * @param unknownRoles      List of Strings of unknown ID's included in the command
     * @param featureIsEnabled  boolean whether the feature is enabled or not
     * @return String response message
     */
    protected String makeResponse(String featureName, List<Role> roles, List<String> unknownRoles, boolean featureIsEnabled) {
        StringBuilder output = new StringBuilder("Roles updated for ");
        output.append(featureName).append(": ");

        for (Role r : roles){
            output.append(r.getName()).append(" ");
        }

        if (!featureIsEnabled) {
            output.append("\nWARNING: ").append(featureName).append(" is not enabled!");
        }

        if (!unknownRoles.isEmpty()){
            output.append("\nUnknown roles: ");
            for (String r : unknownRoles) {
                output.append(r);
            }
        }

        return output.toString();
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        RoleChecksAndData r = checks(e, message, guild, guildID, args);
        if (!r.checksSuccessful()) return r.checksMessage();

        List<Role> roles = r.roles();

        String[] roleIDs = new String[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
            roleIDs[i] = roles.get(i).getId();
        }

        SettingsManager.setRequiredRoles(guildID, r.featureName(), roleIDs);
        return makeResponse(r.featureName(), roles, r.unknownRoles(), r.featureIsEnabled());
    }
}
