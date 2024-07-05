package Configurations;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class SettingsManager {

    private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);

    public static void loadSettings(JDA jda) {
        List<Guild> guilds = jda.getGuilds();

        for (Guild guild : guilds) {

            String guildID = guild.getId();

            log.debug("Getting settings for guild: {}", guildID);

            String prefix = DatabaseSettingsManager.getGuildPrefix(guildID);
            prefixes.put(guildID, prefix);

            Map<String, Boolean> features = DatabaseSettingsManager.getConfiguration(guildID);
            featuresConfiguration.put(guildID, features);

            Map<String, Boolean> commandUsesPermissions = DatabaseSettingsManager.getCommandUsesPermissions(guildID);
            usesPermissions.put(guildID, commandUsesPermissions);

            Map<String, String[]> commandRequiredRoles = DatabaseSettingsManager.getRoles(guildID);
            requiredRoles.put(guildID, commandRequiredRoles);

        }

    }

    /**
     * Backs up the MariaDB database every 30 minutes
     */
    public static void startBackupTimer() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {

                log.info("Running MariaDB backup on {} prefixes {} guilds {} permissions and {} roles",
                        updatedPrefixes.size(),
                        updatedFeatures.size(),
                        updatedUsesPermissions.size(),
                        updatedRequiredRoles.size());

                for (String guildID : updatedPrefixes) {
                    DatabaseSettingsManager.setGuildPrefix(guildID, prefixes.get(guildID));
                }

                updatedPrefixes.clear();

                for (String guildID : updatedFeatures) {
                    DatabaseSettingsManager.editConfiguration(guildID, featuresConfiguration.get(guildID));
                }

                updatedFeatures.clear();

                for (String guildID : updatedUsesPermissions) {
                    DatabaseSettingsManager.editCommandUsesPermissions(guildID, usesPermissions.get(guildID));
                }

                updatedUsesPermissions.clear();

                for (String guildID : updatedRequiredRoles) {
                    DatabaseSettingsManager.editRoles(guildID, requiredRoles.get(guildID));
                }

                updatedRequiredRoles.clear();

            }
        }, 0, 108000000); // 30 minutes
    }

    /*
     * This stores the guilds that have had settings changed
     * even though settings can be accessed it doesn't mean, they have been changed and therefore should not be backed up
     * whereas with other features, accessing them may mean they change and so will be backed up based on whether they are loaded
     * Array takes in a key or the guildsID
     */
    private static final List<String> updatedPrefixes = new ArrayList<>();
    private static final List<String> updatedFeatures = new ArrayList<>();
    private static final List<String> updatedUsesPermissions = new ArrayList<>();
    private static final List<String> updatedRequiredRoles = new ArrayList<>();

    /*
     * See DatabaseSettingsManager for database tables
     */
    private final static HashMap<String, String> prefixes = new HashMap<>();
    private final static HashMap<String, Map<String, Boolean>> featuresConfiguration = new HashMap<>();
    private final static HashMap<String, Map<String, String[]>> requiredRoles = new HashMap<>();
    private final static HashMap<String, Map<String, Boolean>> usesPermissions = new HashMap<>();

    /**
     * Gets the prefix that a guild uses
     *
     * @param guildID The ID of the guild
     * @return String the guilds prefix
     */
    public static String getGuildsPrefix(String guildID) {
        return prefixes.get(guildID);
    }

    /**
     * Changes the prefix that a guild uses
     *
     * @param guildID The ID of the guild
     * @param value   The new prefix
     */
    public static void setGuildsPrefix(String guildID, String value) {
        prefixes.put(guildID, value);
        updatedPrefixes.add(guildID);
    }

    /**
     * Returns a list of features that the bot has
     *
     * @return Array of feature names
     */
    public static String[] getFeatures() {
        Map<String, Boolean> featuresConfig = featuresConfiguration.values().iterator().next();
        Stream<String> features = featuresConfig.keySet().stream();
        return features.toArray(String[]::new);
    }

    /**
     * Tells you if a feature is turned on or off
     *
     * @param guildID The ID of the guild
     * @param setting The setting that is needed
     * @return boolean True or False depending on whether the feature is on or off
     */
    public static Boolean featureIsEnabled(String guildID, String setting) {
        Map<String, Boolean> config = featuresConfiguration.get(guildID);
        if (!config.containsKey(setting)) return null;
        return config.get(setting);
    }

    /**
     * Changes the settings for a guild
     *
     * @param guildID The ID of the guild
     * @param setting The setting that is going to be changed
     * @param value   boolean True or False depending on whether the feature is being turned on or off
     */
    public static void setGuildsFeature(String guildID, String setting, boolean value) {
        Map<String, Boolean> config = featuresConfiguration.get(guildID);
        config.put(setting, value);
        featuresConfiguration.put(guildID, config);

        updatedFeatures.add(guildID);
    }

    /**
     * Says whether the command is configured to use Discord's built-in permissions
     *
     * @param guildID The ID of the guild
     * @param setting The setting that is being checked
     * @return true if command uses built-in permissions, false if it uses custom roles
     */
    public static boolean commandUsesPermissions(String guildID, String setting) {
        Map<String, Boolean> config = usesPermissions.get(guildID);
        return config.get(setting);
    }

    public static void setCommandUsesPermission(String guildID, String setting, boolean value) {
        Map<String, Boolean> config = usesPermissions.get(guildID);
        config.put(setting, value);
        usesPermissions.put(guildID, config);

        updatedUsesPermissions.add(guildID);
    }

    /**
     * Returns a list of role ID's that can use a certain feature
     *
     * @param guildID The ID of the guild
     * @param setting The setting that requires roles
     * @return String[] of role ID's
     */
    public static String[] getRequiredRoles(String guildID, String setting) {
        Map<String, String[]> config = requiredRoles.get(guildID);
        return config.get(setting);
    }

    /**
     * Changes the required roles for a feature
     *
     * @param guildID The guilds ID that you want to change required roles for
     * @param setting The feature name that you want to change required roles for
     * @param value   A String[] of role ID's
     */
    public static void setRequiredRoles(String guildID, String setting, String[] value) {
        Map<String, String[]> config = requiredRoles.get(guildID);
        config.put(setting, value);
        requiredRoles.put(guildID, config);

        updatedRequiredRoles.add(guildID);
    }

}
