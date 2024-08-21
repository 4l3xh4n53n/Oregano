package Commands.Administrative;

import Commands.CommandType;
import Commands.OreganoCommand;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public abstract class AdministrativeCommand implements OreganoCommand {

    private final CommandType type = CommandType.ADMINISTRATIVE;

    @Override
    public CommandType getType() {
        return type;
    }

    /**
     * Converts a String to an int
     * @param string The String that is being converted
     * @return int, returns -1 if the string isn't numerical
     */
    protected static int stringToInt(String string) {
        int result;

        try {
            result = Integer.parseInt(string);
        } catch (Exception ignored){
            result = -1;
        }

        return result;
    }

    /**
     * Gets the member that has been mentioned in a message
     * @param guild The guild that the message was sent in
     * @param message The message that was sent
     * @param arg The position in the command where a mention, ID or tag is intended to be
     * @return The member that has been mentioned
     */
    protected static Member getMentioned(Guild guild, Message message, String arg) {
        List<Member> mentions = message.getMentions().getMembers();
        if (mentions.size() > 0){
            return mentions.get(0);
        }

        Member member = null;

        try {
            member = guild.getMemberById(arg);
            if (member == null) { // If user doesn't exist in cache
                member = guild.retrieveMemberById(arg).complete();
            }
        } catch (Exception ignored) {} // Invalid snowflake or invalid ID

        try { // Member by tag
            member = guild.getMemberByTag(arg);
        } catch (Exception ignored) {}

        return member;
    }

    /**
     * Gets every role that a member has and gets them as ID so that it can be compared with the required role ID's
     * @param roles List of the members roles
     * @return List of role ID's
     */
    protected static List<String> getIDsFromRoles(List<Role> roles){
        List<String> ids = new ArrayList<>();
        for (Role role : roles){
            ids.add(role.getId());
        }
        return ids;
    }

    /**
     * Makes sure that the command author has the correct roles and is higher in the role hierarchy than the member they are issuing the command for
     * @param guildID The guilds ID
     * @param featureName The setting that has to be checked, usually the name of the command
     * @param author The person who sent the command
     * @param mentioned The person mentioned in the command
     * @param requiredPermission The required Discord permissions
     * @return boolean value, true of false depending on whether the author has the correct permissions or not
     */
    protected static boolean checkPermissions(String guildID, String featureName, Member author, Member mentioned, Permission requiredPermission){
        boolean authorHasPermission = checkPermissions(guildID, featureName, author, requiredPermission);

        if (!authorHasPermission) return false;

        // Make sure the command author is higher than command target

        List<Role> mentionedRoles = mentioned.getRoles();
        List<Role> authorRoles = author.getRoles();

        int mentionedPosition = 0;
        int authorPosition = 0;

        if (mentionedRoles.size() > 0) mentionedPosition = mentionedRoles.get(0).getPosition();
        if (authorRoles.size() > 0) authorPosition = authorRoles.get(0).getPosition();

        return authorPosition > mentionedPosition;
    }

    /**
     * Makes sure that the command author has the correct roles
     * @param guildID The guilds ID
     * @param featureName The setting that has to be checked, usually the name of the command
     * @param author The person who sent the command
     * @param requiredPermission The required Discord permissions
     * @return boolean value, true of false depending on whether the author has the correct permissions or not
     */
    protected static boolean checkPermissions(String guildID, String featureName, Member author, Permission requiredPermission){
        boolean authorHasPermission = false;

        if (SettingsManager.commandUsesPermissions(guildID, featureName)){ // Discord permissions based authorization
            EnumSet<Permission> authorPermissions = author.getPermissions();

            if (authorPermissions.contains(requiredPermission) || authorPermissions.contains(Permission.ADMINISTRATOR)){
                authorHasPermission = true;
            }

        } else { // Discord roles based authorization
            String[] requiredRoles = SettingsManager.getRequiredRoles(guildID, featureName);

            if (CollectionUtils.containsAny(Arrays.asList(requiredRoles), getIDsFromRoles(author.getRoles()))){
                authorHasPermission = true;
            }
        }

        return authorHasPermission;
    }

    /**
     * Creates a RoleChecksAndData object that has failed a check
     * @param message (String)  The reason why the check has failed
     * @return  RoleChecksAndData
     */
    protected static AdministrativeChecksAndData checkFailed(String message) {
        return new AdministrativeChecksAndData(false, message, null);
    }

    /**
     * Does certain checks and returns data
     * @param e MessageReceivedEvent
     * @param message               (Message)       message sent by user
     * @param guild                 (Guild)         guild that is being acted in
     * @param guildID               (String)        guilds ID
     * @param featureName           (String)        name of the feature being run
     * @param args                  (String[])      message arguments
     * @param requiredPermission    (Permission)    discord permission required to run the command
     * @param requiredArgs          (int)           required amount of arguments to run the command
     * @param checkHierarchy        (boolean)       whether to check role hierarchy
     * @return RoleChecksAndData checksSuccessful, checksMessage, featureName, featureIsEnabled, roles, unknownRoles, currentRoleRequirements
     */
    protected static AdministrativeChecksAndData checks(MessageReceivedEvent e, Message message, Guild guild, String guildID, String featureName, String[] args, Permission requiredPermission, int requiredArgs, boolean checkHierarchy) {
        if (!SettingsManager.featureIsEnabled(guildID, featureName)) return checkFailed(null);
        if (args.length < requiredArgs) return checkFailed("You have not included enough arguments to use this command.");
        Member mentioned = getMentioned(guild, message, args[0]);

        if (mentioned == null) return checkFailed("Member cannot be found.");
        Member author = e.getMember();

        boolean hasPermission;
        if (checkHierarchy) hasPermission = checkPermissions(guildID, featureName, author, mentioned, requiredPermission);
        else hasPermission = checkPermissions(guildID, featureName, author, requiredPermission);
        if (!hasPermission) return checkFailed("You do not have permission to use this command.");

        return new AdministrativeChecksAndData(true, "", mentioned);
    }

    /**
     * Filters arguments and turns them into a string.
     * @param args      (String[]) arguments
     * @param startPos  (int) position in args where reason starts
     * @return (String) reason
     */
    protected String getReason(String[] args, int startPos) {
        List<String> argsList = new LinkedList<>(Arrays.asList(args));
        if (startPos > 0) {
            argsList.subList(0, startPos).clear();
        }
        return String.join(" ", argsList);
    }

}
