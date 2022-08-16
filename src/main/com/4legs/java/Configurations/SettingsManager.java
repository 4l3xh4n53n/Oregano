package Configurations;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SettingsManager {

    private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);

    public static void loadSettings(JDA jda){
        List<Guild> guilds = jda.getGuilds();

        for (Guild guild : guilds){

            String guildID = guild.getId();

            log.debug("Getting settings for guild: {}", guildID);

            String prefixe = DatabaseSettingsManager.getGuildPrefix(guildID);
            prefixes.put(guildID, prefixe);

            Map<String, Boolean> features = DatabaseSettingsManager.getConfiguration(guildID);
            featuresConfiguration.put(guildID, features);

            Map<String, Object> administrative = DatabaseSettingsManager.getAdministrativeConfiguration(guildID);
            administrativeConfiguration.put(guildID, administrative);

        }

    }

    /**
     * Backs up the MariaDB database every 30 minutes
     */
    public static void startBackupTimer(){
        Timer timer = new Timer();

        timer.schedule( new TimerTask() {
            public void run() {

                log.info("Running MariaDB backup on {} guilds and {} administrativeConfigs", updatedGuilds.size(), updatedAdministrativeConfigs.size());

                for (String key : updatedPrefixes){
                    DatabaseSettingsManager.setGuildPrefix(key, prefixes.get(key));
                }

                for (String key : updatedGuilds){
                    DatabaseSettingsManager.editConfiguration(key, featuresConfiguration.get(key));
                }

                updatedGuilds.clear();

                for (String key : updatedAdministrativeConfigs){
                    DatabaseSettingsManager.editAdministrativeConfiguration(key, administrativeConfiguration.get(key));
                }

                updatedAdministrativeConfigs.clear();

            }
        }, 0, 108000000 ); // 30 minutes
    }

    /*
     * This stores the guilds that have had settings changed
     * even though settings can be accessed it doesn't mean, they have been changed and therefore should not be backed up
     * whereas with other features, accessing them may mean they change and so will be backed up based on whether they are loaded
     * Array takes in a key or the guildsID
     */
    private static final List<String> updatedPrefixes = new ArrayList<>();
    private static final List<String> updatedGuilds = new ArrayList<>();
    private static final List<String> updatedAdministrativeConfigs = new ArrayList<>();

    /*
     * See DatabaseSettingsManager for database tables
     */
    private final static HashMap<String, String> prefixes = new HashMap<>();
    private final static HashMap<String, Map<String, Boolean>> featuresConfiguration = new HashMap<>();
    private final static HashMap<String, Map<String, Object>> administrativeConfiguration = new HashMap<>();

    /**
     * Gets the prefix that a guild uses
     * @param guildID The ID of the guild
     * @return String the guilds prefix
     */
    public static String getGuildsPrefix(String guildID){
        return prefixes.get(guildID);
    }

    /**
     * Changes the prefix that a guild uses
     * @param guildID The ID of the guild
     * @param value The new prefix
     */
    public static void setGuildsPrefix(String guildID, String value){
        prefixes.put(guildID, value);
        updatedPrefixes.add(guildID);
    }

    /**
     * Tells you if a feature is turned on or off
     * @param guildID The ID of the guild
     * @param setting The setting that is needed
     * @return boolean True or False depending on whether the feature is on or off
     */
    public static boolean featureIsEnabled(String guildID, String setting){
        Map<String, Boolean> config = featuresConfiguration.get(guildID);
        return config.get(setting);
    }

    /**
     * Changes the settings for a guild
     * @param guildID The ID of the guild
     * @param setting The setting that is going to be changed
     * @param value boolean True or False depending on whether the feature is being turned on or off
     */
    public static void setGuildsFeature(String guildID, String setting, boolean value){

        Map<String, Boolean> config = featuresConfiguration.get(guildID);
        config.put(setting, value);
        featuresConfiguration.put(guildID, config);

        updatedGuilds.add(guildID);

    }

    /**
     * Gets an administrative based setting
     * @param guildID The ID of the guild
     * @param setting Roles that need to be gotten (should only pass the command name e.g. ban)
     * @return Returns an array of role IDs
     */
    public static String[] getAdministrativeRoles(String guildID, String setting) {

        Map<String, Object> config = administrativeConfiguration.get(guildID);
        Object object = config.get(setting + "_roles");
        if (object.getClass() == Boolean.class) return null; // error checking
        String string = (String) object;
        return string.split(",");

    }

    /**
     * Checks if a command is using roles or discords built in permissions
     * @param guildID The ID of the guild
     * @param setting The command (do NOT use warn since there is no discord built in permissions for this)
     * @return boolean True if it uses roles False if it uses discords built in permissions
     */
    public static boolean administrativeCommandUsesRoles(String guildID, String setting){

        Map<String, Object> config = administrativeConfiguration.get(guildID);
        Object object = config.get(setting + "_use_role");
        if (object.getClass() == String.class) return true; // error checking
        return (boolean) object;

    }

    /**
     * Changes the setting for an administrative command
     * @param guildID The guilds ID
     * @param setting The setting that is going to be changed
     * @param value What the setting is being set to
     */
    public static void setAdministrativeSetting(String guildID, String setting, Object value){

        Map<String, Object> config = administrativeConfiguration.get(guildID);
        config.put(setting, value);
        administrativeConfiguration.put(guildID, config);
        updatedAdministrativeConfigs.add(guildID);

    }

}
