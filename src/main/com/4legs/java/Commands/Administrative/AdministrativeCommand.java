package Commands.Administrative;

import Commands.CommandType;
import Commands.OreganoCommand;

public class AdministrativeCommand implements OreganoCommand {

    private String name = null;
    private Object type = CommandType.ADMINISTRATIVE;

    public AdministrativeCommand(String name, Object type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public CommandType getType() {
        return null;
    }

    @Override
    public void execute() {

    }
}
