package Commands.Settings;

import Commands.CommandType;
import Commands.OreganoCommand;

public abstract class SettingsCommand implements OreganoCommand {

    private final CommandType type = CommandType.SETTINGS;

    @Override
    public CommandType getType() {
        return type;
    }

}
