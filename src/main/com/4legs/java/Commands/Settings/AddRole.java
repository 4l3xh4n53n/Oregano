package Commands.Settings;

import Configurations.SettingsManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class AddRole extends SetRoles {
    @Override
    public String getExample() {
        return "addrole {FEATURE NAME} {@role or roleID} ...";
    }

    @Override
    public String getPurpose() {
        return "Adds role(s) to list of roles that can use a certain feature.";
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        RoleChecksAndData r = checksWithRoleRequirements(e, message, guild, guildID, args);
        if (!r.checksSuccessful()) return r.checksMessage();

        // Add any roles that don't already exist in settings

        List<String> newRoleRequirements = new ArrayList<>(r.currentRoleRequirements());

        String roleID;
        for (Role role : r.roles()){
            roleID = role.getId();
            if (!r.currentRoleRequirements().contains(roleID)) newRoleRequirements.add(roleID);
        }

        SettingsManager.setRequiredRoles(guildID, r.featureName(), newRoleRequirements.toArray(new String[0]));
        return makeResponse(r.featureName(), newRoleRequirements, r.unknownRoles(), r.featureIsEnabled(), guild);
    }
}
