package Commands.Informative;

import Commands.CommandHandler;
import Commands.CommandType;
import Commands.OreganoCommand;
import Util.Utilities;
import Util.Tuple;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("help", "Tells you how to use commands.")
                .addOptions(new OptionData(
                                OptionType.STRING, "type", "Filters by a type of feature", false, false),
                        new OptionData(OptionType.STRING, "feature", "Filters by a certain feature", false, false)
                )
                .setGuildOnly(true);

        // todo ask on the super duper scary JDA Discord server, how I can have infinite number of filters
        // todo also ask how I can make it so I can only have one or the other of these filters

    }

    @Override
    public String onSlashCommand(SlashCommandInteractionEvent e, Guild guild, List<OptionMapping> options) {
        return null;
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
        Tuple<String[], String[]> createFilterResult = Utilities.createFilter(args, 0);
        String[] filter = createFilterResult.x();
        String[] unknownCommandTypes = createFilterResult.y();

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
