package Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent e){

        log.debug("ID: {}; AUTHOR: {}; CONTENT: {};", e.getMessage().getId(), e.getMessage().getAuthor().getAsTag(), e.getMessage().getContentRaw());

    }

}
