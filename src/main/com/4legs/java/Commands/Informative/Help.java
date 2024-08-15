package Commands.Informative;

import Commands.CommandHandler;
import Commands.CommandType;
import Commands.OreganoCommand;
import Main.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class Help extends InformativeCommand{

    @Override
    public String getExample() {
        return "help Opt{CommandType} ...";
    }

    @Override
    public String getPurpose() {
        return "Shows which commands and features the bot has.";
    }

    /**
     * Sorts commands by category
     * @return Map category, list of commands
     */
    private static Map<String, List<OreganoCommand>> getCommandsByType() {
        OreganoCommand[] commands = CommandHandler.getCommands();

        Map<String, List<OreganoCommand>> commandsByType = new HashMap<>();

        for (CommandType type : CommandType.values()){
            commandsByType.put(type.name(), new ArrayList<>());
        }

        String commandType;
        List<OreganoCommand> commandList;

        for (OreganoCommand command : commands) {
            commandType = command.getType().name();
            commandList = commandsByType.get(commandType);
            commandList.add(command);
            commandsByType.replace(commandType, commandList);
        }

        return commandsByType;
    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        boolean filtered = false;
        String[][] createFilterResult = Utilities.createFilter(args, 0);
        String[] filter = createFilterResult[0];
        String[] unknownCommandTypes = createFilterResult[1];

        if (args.length > 0) filtered = true;

        Map<String, List<OreganoCommand>> commandsByType = getCommandsByType();

        // Builds help sheet

        StringBuilder helpSheet = new StringBuilder("# HELP SHEET:\n");

        String commandName;
        String commandDescription;
        String commandExample;

        // Go through each type and then each command adding relevant information to help sheet.

        for (String type : commandsByType.keySet()){

            // If filtered and filter does not contain type
            if (filtered && Arrays.stream(filter).noneMatch(x -> x.equals(type.toUpperCase(Locale.ROOT)))) continue;

            helpSheet.append("## ").append(type).append("\n"); // Command category heading

            for (OreganoCommand command : commandsByType.get(type)){
                commandName = command.getClass().getSimpleName();
                commandDescription = command.getPurpose();
                commandExample = command.getExample();

                helpSheet.append("**")
                        .append(commandName)
                        .append("**\n    ")
                        .append(commandDescription)
                        .append("\n    Example: ")
                        .append(commandExample)
                        .append("\n");
            }

        }

        // In-case user inputs invalid category to filter

        if (unknownCommandTypes.length > 0){
            helpSheet.append("Unknown command types: ")
                    .append(Arrays.toString(unknownCommandTypes)
                            .replace("[", "")
                            .replace("]", ""));
        }

        return helpSheet.toString();
    }
}
