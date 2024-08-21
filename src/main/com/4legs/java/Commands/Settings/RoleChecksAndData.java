package Commands.Settings;

import net.dv8tion.jda.api.entities.Role;

import java.util.List;

/**
 * Stores data that is related to the checks function that the SetRoles set of commands is going to use.
 * @param checksSuccessful          (boolean)       whether the checks were successful, if not print checksMessage
 * @param checksMessage             (String)        the reason why checks failed (e.g. not enough arguments)
 * @param featureName               (String)        the name of the feature that is being configured
 * @param featureIsEnabled          (Boolean)       whether the target feature is enabled
 * @param roles                     (List<Role>)    a list of roles that are being set
 * @param unknownRoles              (List<String>)  a list of unidentifiable roles
 * @param currentRoleRequirements   (List<String>)  the current roles required to use a command
 */
public record RoleChecksAndData
    (
            boolean checksSuccessful,
            String checksMessage,
            String featureName,
            Boolean featureIsEnabled,
            List<Role> roles,
            List<String> unknownRoles,
            List<String> currentRoleRequirements

    ) {

}
