package Commands.Administrative;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AdministrativeUtilities {

    /**
     * Converts a String to an int
     * @param string The String that is being converted
     * @return int, returns -1 if the string isn't numerical
     */
    public static int stringToInt(String string) {

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
    public static Member getMentioned(Guild guild, Message message, String arg) {

        AtomicReference<Member> member = new AtomicReference<>();
        List<Member> mentions = message.getMentions().getMembers();
        if (mentions.size() > 0){
            return mentions.get(0);
        }

        AtomicBoolean finished = new AtomicBoolean(false);

        try {
            guild.retrieveMemberById(arg).queue(newValue -> {
                member.set(newValue);
                finished.set(true);
            });
        } catch (Exception ignored) {
            finished.set(true);
        }

        while (!finished.get()) ;

        if (member.get() != null) {
            return member.get();
        }

        try {
            member.set(guild.getMemberByTag(arg));
        } catch (Exception ignored) {}

        return member.get();
    }


    /**
     * Gets every role that a member has and gets them as ID so that it can be compared with the required role ID's
     * @param roles List of the members roles
     * @return List of role ID's
     */
    private static List<String> getIDsFromRoles(List<Role> roles){
        List<String> ids = new ArrayList<>();
        for (Role role : roles){
            ids.add(role.getId());
        }
        return ids;
    }

    /**
     * Makes sure that the command author has the correct roles and is higher in the role hierarchy than the member they are issuing the command for
     * @param guildID The guilds ID
     * @param setting The setting that has to be checked, usually the name of the command
     * @param author The person who sent the command
     * @param mentioned The person mentioned in the command
     * @param requiredPermission The required Discord permissions
     * @return boolean value, true of false depending on whether the author has the correct permissions or not
     */
    public static boolean checkPermissions(String guildID, String setting, Member author, Member mentioned, Permission requiredPermission){

        boolean authorHasPermission = checkPermissions(guildID, setting, author, requiredPermission);

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
     * @param setting The setting that has to be checked, usually the name of the command
     * @param author The person who sent the command
     * @param requiredPermission The required Discord permissions
     * @return boolean value, true of false depending on whether the author has the correct permissions or not
     */
    public static boolean checkPermissions(String guildID, String setting, Member author, Permission requiredPermission){

        boolean authorHasPermission = false;

        if (SettingsManager.commandUsesPermissions(guildID, setting)){

            // Discord permissions based authorization

            EnumSet<Permission> authorPermissions = author.getPermissions();

            if (authorPermissions.contains(requiredPermission) || authorPermissions.contains(Permission.ADMINISTRATOR)){
                authorHasPermission = true;
            }

        } else {

            // Discord roles based authorization

            String[] requiredRoles = SettingsManager.getRequiredRoles(guildID, setting);

            if (CollectionUtils.containsAny(Arrays.asList(requiredRoles), getIDsFromRoles(author.getRoles()))){
                authorHasPermission = true;
            }

        }

        return authorHasPermission;

    }

}
