package Commands;

import Configurations.SettingsManager;
import com.google.common.reflect.ClassPath;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private static final Map<String, OreganoCommand> commands = new HashMap<>();

    /**
     * Finds all the commands under the Commands package and adds them to a list where they can be accessed later
     * This will call the command's constructor which will call the addCommand()
     */
    public static void loadCommands() {

        try {
            Set<Class<?>> classes = ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz ->
                            clazz.getPackageName().toLowerCase(Locale.ROOT).contains("commands"))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());

            for (Class<?> clazz : classes) {

                if (clazz.getSuperclass() == null) continue; // has no super class
                if (clazz.getSuperclass() == OreganoCommand.class) continue; // If super is OreganoCommand it is abstract class for the commands

                Class<?>[] interfaces = clazz.getSuperclass().getInterfaces(); // If super class interfaces oregano command it is a command class

                if (Arrays.asList(interfaces).contains(OreganoCommand.class)){
                    clazz.getDeclaredConstructor().newInstance();
                    // todo OreganoCommand command = (OreganoCommand) clazz.getDeclaredConstructor().newInstance();
                }

            }
        } catch (Exception e){
            log.warn(e.getMessage());
        }

    }

    /**
     * Allows you to add a command to the commands HashMap
     * @param command Command being added to HashMap
     */
    public static void addCommand(OreganoCommand command){

        String name = command.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        commands.put(name, command);
        log.info("Added command: {}", name);

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e){

        Guild guild = e.getGuild();
        String guildID = guild.getId();

        String prefix = SettingsManager.getGuildsPrefix(guildID);

        log.debug("ID: {}; AUTHOR: {}; CONTENT: {};", e.getMessage().getId(), e.getMessage().getAuthor().getAsTag(), e.getMessage().getContentRaw());

        Message message = e.getMessage();
        String messageContent = message.getContentRaw();
        User author = e.getAuthor();

        if (author.isBot() || author.isSystem()) return;

        if (messageContent.toLowerCase().contains("prefix")) {
            e.getChannel().sendMessage("The guilds prefix is: " + prefix).queue();
        }

        if (!messageContent.startsWith(prefix)) return;

        String[] args = messageContent.replace(prefix, "").split("\\s+");
        String commandWord = args[0].toLowerCase(Locale.ROOT);
        args = Arrays.copyOfRange(args, 1, args.length); // Removes the command word from the array

        if (commands.containsKey(commandWord)){

            String response = commands.get(commandWord).onCommand(e, message, guild, guildID, args);

            if (response == null) return;

            e.getChannel().sendMessage(response).queue();

        }

    }

    /**
     * Gets the command, useful for accessing data such as command purpose, builtInPermissions, etc
     * @param commandName The name of the command you want to get
     * @return OreganoCommand
     */
    public static OreganoCommand getCommandByName(String commandName){
        return commands.get(commandName);
    }

}
