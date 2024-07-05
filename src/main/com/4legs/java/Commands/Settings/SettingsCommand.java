package Commands.Settings;

import Commands.CommandType;
import Commands.OreganoCommand;
import net.dv8tion.jda.api.Permission;

public abstract class SettingsCommand implements OreganoCommand {

    private final CommandType type = CommandType.SETTINGS;

    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public Permission getBuiltInPermission(){
        return Permission.ADMINISTRATOR;
    }

}
