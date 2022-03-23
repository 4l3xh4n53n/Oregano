import Commands.CommandHandler;
import Configurations.DatabaseConnector;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Stops the program from running without the database
     */
    private static void isDatabaseIsOnline(){
        boolean online = DatabaseConnector.isDatabaseOnline();

        while (!online){
            log.debug("Database is not online.");
            online = DatabaseConnector.isDatabaseOnline();
        }

        log.info("Database is online.");
    }

    public static void main(String[] args) throws LoginException, InterruptedException {

        isDatabaseIsOnline();
        SettingsManager.startBackupTimer();


        JDA jda = JDABuilder.createDefault(System.getenv("TOKEN"))
                .addEventListeners(new CommandHandler())
                .build();

        jda.awaitReady();

        log.info("Logged in as: {}", jda.getSelfUser().getAsTag());

    }

}
