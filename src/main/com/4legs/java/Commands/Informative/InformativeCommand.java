package Commands.Informative;

import Commands.CommandType;
import Commands.OreganoCommand;
import net.dv8tion.jda.api.Permission;

public abstract class InformativeCommand implements OreganoCommand {

    private final CommandType type = CommandType.INFORMATIVE;

    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public Permission getBuiltInPermission() {
        return null;
    }
}
