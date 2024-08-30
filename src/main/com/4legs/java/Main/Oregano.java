package Main;

import Commands.CommandHandler;
import Configurations.DatabaseConnector;
import Configurations.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oregano {

    private static final Logger log = LoggerFactory.getLogger(Oregano.class);


    private static JDA jda;

    public static JDA getJDA(){
        return jda;
    }

    public static void main(String[] args) throws InterruptedException {

        boolean databaseOnline = DatabaseConnector.isDatabaseOnline();

        while (!databaseOnline) {
            databaseOnline = DatabaseConnector.isDatabaseOnline();
        }

        jda = JDABuilder.createDefault(System.getenv("TOKEN"))
                .addEventListeners(new CommandHandler())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ROLE_TAGS)
                .build();

        SettingsManager.startBackupTimer();
        CommandHandler.loadCommands();
        CommandHandler.registerSlashCommands();

        jda.awaitReady();

        SettingsManager.loadSettings(jda);

        log.info("Logged in as: {}", jda.getSelfUser().getAsTag());

    }

}
