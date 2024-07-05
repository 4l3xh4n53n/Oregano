package Commands.Administrative;

import Commands.CommandType;
import Commands.OreganoCommand;

public abstract class AdministrativeCommand implements OreganoCommand {

    private final CommandType type = CommandType.ADMINISTRATIVE;
    @Override
    public CommandType getType() {
        return type;
    }

}
