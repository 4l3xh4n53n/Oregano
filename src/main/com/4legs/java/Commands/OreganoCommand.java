package Commands;

/**
 * Interface for the bots commands
 */
public interface OreganoCommand {

    /**
     * Gets the name of the command
     * @return String ( Name Of Command )
     */
    String getName();

    /**
     * Gets the type of command ( Administrative, Fun, etc... )
     * @return CommandType ( Type Of Command )
     */
    CommandType getType();

    /**
     * Executes the command
     */
    void execute();

}
