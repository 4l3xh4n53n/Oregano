package Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSettingsManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSettingsManager.class);

    /**
     * Creates a new entry into database table with default values
     * @param guildID The ID of the guild being added to the configuration
     * @param table The table that the guild is being added to
     */
    private static void createNewSettings(String guildID, String table) {
        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return;
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " (guild_id) VALUES(?)");
            ps.setString(1, guildID);
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID: {}", guildID);
        }
    }

    /**
     * Checks if a guilds row in table exists, if settings don't exist it runs createNewSettings
     * @param guildID The ID of the guild to be checked
     */
    private static void checkIfSettingsExist(String guildID, String table){
        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT guild_id FROM " + table + " WHERE guild_id = '" + guildID + "'");

            if(!rs.next()){
                createNewSettings(guildID, table);
            }

            rs.close();
            st.close();
            con.close();

        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID: {}", guildID);
        }
    }

    /*

      prefixes ( the table that stores the command prefix for each guild )

      +----------+------+---------+
      | Field    | Type | Default |
      +----------+------+---------+
      | guild_id | text | NULL    |
      | prefix   | text | 'o.'    |
      +----------+------+---------+

     */

    /**
     * Changes the values inside the database ( should only be called every 30 minutes )
     * NOTE:
     * checkIfSettingsExist() should NOT need to be called because this should only be called for servers which already have settings
     *
     * @param guildID The ID of the guild that is being backed up
     * @param value The new prefix
     */
    public static void setGuildPrefix(String guildID, String value){

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return;
            PreparedStatement ps = con.prepareStatement("UPDATE prefixes SET prefix = ? WHERE guild_id = '" + guildID + "'");
            ps.setString(1, value);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID: {} value: {}", guildID, value);
        }

    }

    /**
     * Loads a configuration from the table prefixes
     * @param guildID The ID of the guild that needs a prefix
     * @return String which is the prefix
     */
    public static String getGuildPrefix(String guildID){
        checkIfSettingsExist(guildID, "prefixes");

        String prefix = "o.";

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return null;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT prefix FROM prefixes WHERE guild_id = '" + guildID + "'");
            rs.next();
            prefix = rs.getString("prefix");
            st.close();
            rs.close();
            con.close();

        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID: {}", guildID);
        }

        return prefix;

    }

    /*

      features_config ( the table for which features are on and off )

      +----------+------------+---------+
      | Field    | Type       | Default |
      +----------+------------+---------+
      | guild_id | text       | NULL    |
      | ban      | tinyint(1) | 0       |
      | kick     | tinyint(1) | 0       |
      | warn     | tinyint(1) | 0       |
      | mute     | tinyint(1) | 0       |
      | purge    | tinyint(1) | 0       |
      +-----------+------------+---------+

     */

    /**
     * Changes the values inside the database ( should only be called every 30 minutes )
     * NOTE:
     * checkIfSettingsExist() should NOT need to be called because this should only be called for servers which already have settings
     *
     * @param guildID The ID of the guild that is being backed up
     * @param value The new configuration
     */
    public static void editConfiguration(String guildID, Map<String, Boolean> value){

       try {
           Connection con = Configurations.DatabaseConnector.connect("Settings");
           if (con == null) return;
           PreparedStatement ps = con.prepareStatement("""
                   UPDATE features_config SET
                   `ban` = ?,
                   `kick` = ?,
                   `warn` = ?,
                   `mute` = ?,
                   `purge` = ?
                   WHERE guild_id = '""" + guildID + "'");
           ps.setBoolean(1, value.get("ban"));
           ps.setBoolean(2, value.get("kick"));
           ps.setBoolean(3, value.get("warn"));
           ps.setBoolean(4, value.get("mute"));
           ps.setBoolean(5, value.get("purge"));
           ps.executeUpdate();
           ps.close();
           con.close();
       } catch (SQLException e){
           log.error(e.getMessage() + " ; VARIABLES: guildID: {} value: {}", guildID, value);
       }

    }

    /**
     * Loads a configuration from the table features_config
     * @param guildID The ID of the guild that needs a configuration
     * @return Map String, Boolean which contains the configuration
     */
    public static Map<String, Boolean> getConfiguration(String guildID) {

        checkIfSettingsExist(guildID, "features_config");

        Map<String, Boolean> row = new HashMap<>();

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return null;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM features_config WHERE guild_id = '" + guildID + "'");
            rs.next();

            for (int i = 1; rs.getMetaData().getColumnCount() > i; i++) {
                String key = rs.getMetaData().getColumnName(i + 1).replace("`", "");
                boolean value = rs.getBoolean(i + 1);
                row.put(key, value);
            }

            st.close();
            rs.close();
            con.close();

        } catch (SQLException e) {
            log.error(e.getMessage() + " ; VARIABLES: guildID {}", guildID);
        }

        return row;

    }

    /*
        roles ( table that stores the required roles for each feature )

        +----------+------+---------+
        | Field    | Type | Default |
        +----------+------+---------+
        | guild_id | text | NULL    |
        | ban      | text | ''      |
        | kick     | text | ''      |
        | mute     | text | ''      |
        | purge    | text | ''      |
        | warn     | text | ''      |
        +----------+------+---------+

     */

    /**
     * Returns the roles that each feature requires to be run
     * @param guildID The ID of the guild that you want settings for
     * @return Map of String[] for each feature's roles
     */
    public static Map<String, String[]> getRoles(String guildID) {
        Map<String, String[]> row = new HashMap<>();
        checkIfSettingsExist(guildID, "roles");

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return null;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM roles WHERE guild_id = '" + guildID + "'");
            rs.next();

            for (int i = 1; rs.getMetaData().getColumnCount() > i; i++) {
                String key = rs.getMetaData().getColumnName(i + 1);

                String value =  rs.getString(i + 1);
                if (value.isEmpty()) row.put(key, new String[0]); // Make an empty array if it's empty
                else row.put(key, value.split(",")); // Otherwise this will make an array with 1 empty string

            }

            rs.close();
            st.close();
            con.close();

        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID {}", guildID);
        }

        return row;
    }

    /**
     * Changes the list of role IDs stored in the database
     * @param guildID The ID of the guild that you want to change settings for
     * @param value The new settings
     */
    public static void editRoles(String guildID, Map<String, String[]> value) {

        StringBuilder statement = new StringBuilder("UPDATE roles SET ");

        for (String key : value.keySet()) {
            statement.append("`").append(key).append("` = ?, ");
        }
        statement = new StringBuilder(statement.substring(0, statement.length() - 2));
        statement.append(" WHERE guild_id = '").append(guildID).append("'");

        try {
            Connection con = DatabaseConnector.connect("Settings");
            if (con == null) return;
            PreparedStatement ps = con.prepareStatement(statement.toString());

            String[][] values = value.values().toArray(new String[0][0]);

            for (int i = 1; i <= values.length; i++) {
                ps.setString(i, Arrays.toString(values[i-1]).replace("[", "").replace("]", ""));
            }

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            log.error(e.getMessage() + " ; VARIABLES: guildID: {} value: {}", guildID, value);
        }

    }

    /*
        use_permissions ( says whether the command uses Discord's built-in permissions or roles )

        +----------+------------+---------+
        | Field    | Type       | Default |
        +----------+------------+---------+
        | guild_id | text       | NULL    |
        | ban      | tinyint(1) | 0       |
        | kick     | tinyint(1) | 0       |
        | mute     | tinyint(1) | 0       |
        | purge    | tinyint(1) | 0       |
        +----------+------------+---------+

     */

    /**
     * Returns the configuration for which commands use Discord's built-in permissions from the database
     * @param guildID The ID of the guild that you want settings for
     * @return Map of Booleans for each feature that supports built in permissions
     */
    public static Map<String, Boolean> getCommandUsesPermissions(String guildID) {
        Map<String, Boolean> row = new HashMap<>();
        checkIfSettingsExist(guildID, "use_permissions");

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return null;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM use_permissions WHERE guild_id = '" + guildID + "'");
            rs.next();

            for (int i = 1; rs.getMetaData().getColumnCount() > i; i++) {
                String key = rs.getMetaData().getColumnName(i + 1);
                row.put(key, rs.getBoolean(i + 1));
            }

            rs.close();
            st.close();
            con.close();

        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID {}", guildID);
        }

        return row;
    }

    /**
     * Changes the values for whether the command will use Discord's built-in permissions
     * @param guildID The ID of the guild that you want to change settings for
     * @param value The new settings
     */
    public static void editCommandUsesPermissions(String guildID, Map<String, Boolean> value){

        StringBuilder statement = new StringBuilder("UPDATE use_permissions SET ");

        for (Map.Entry<String, Boolean> entry : value.entrySet()) {
            statement.append("`").append(entry.getKey()).append("` = ?, ");
        }
        statement = new StringBuilder(statement.substring(0, statement.length() - 2));
        statement.append(" WHERE guild_id = '").append(guildID).append("'");

        try {
            Connection con = DatabaseConnector.connect("Settings");
            if (con == null) return;
            PreparedStatement ps = con.prepareStatement(statement.toString());

            for (int i = 1; i <= value.size(); i++) {
                ps.setBoolean(i, (Boolean) value.values().toArray()[i-1]);
            }

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            log.error(e.getMessage() + " ; VARIABLES: guildID: {} value: {}", guildID, value);
        }

    }

}
