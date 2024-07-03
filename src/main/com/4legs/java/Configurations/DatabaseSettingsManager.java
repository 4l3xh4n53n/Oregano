package Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSettingsManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSettingsManager.class);

    /**
     * Creates a new entry into database table
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
     * Checks if a guilds row in table exists, if it doesn't it creates one
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
     *
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
     *
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
                   ban = ?,
                   kick = ?,
                   warn = ?,
                   mute = ?,
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

      administrative_config ( the table for configurations of the ADMINISTRATIVE commands )

      +----------------+------------+---------+
      | Field          | Type       | Default |
      +----------------+------------+---------+
      | guild_id       | text       | NULL    |
      | ban_use_role   | tinyint(1) | 1       |
      | ban_roles      | text       | ''      |
      | kick_use_role  | tinyint(1) | 1       |
      | kick_roles     | text       | ''      |
      | warn_roles     | text       | ''      |
      | mute_use_role  | tinyint(1) | 1       |
      | mute_roles     | text       | ''      |
      | purge_use_role | tinyint(1) | 1       |
      | purge_roles    | text       | ''      |
      +----------------+------------+---------+

     */

    /**
     * Changes the values inside the database ( should only be called every 30 minutes )
     *
     * NOTE:
     * checkIfSettingsExist() should NOT need to be called because this should only be called for servers which already have settings
     *
     * @param guildID The ID of the guild that is being backed up
     * @param value The new configuration
     */
    public static void editAdministrativeConfiguration(String guildID, Map<String, Object> value){

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return;
            PreparedStatement ps = con.prepareStatement("""
                    UPDATE administrative_config SET
                    ban_use_role = ?,
                    ban_roles = ?,
                    kick_use_role = ?,
                    kick_roles = ?,
                    warn_roles = ?,
                    mute_use_role = ?,
                    mute_roles = ?,
                    purge_use_role = ?,
                    purge_roles = ?
                    WHERE guild_id = '""" + guildID + "'");
            ps.setObject(1, value.get("ban_use_role"));
            ps.setObject(2, value.get("ban_roles"));
            ps.setObject(3, value.get("kick_use_role"));
            ps.setObject(4, value.get("kick_roles"));
            ps.setObject(5, value.get("warn_roles"));
            ps.setObject(6, value.get("mute_use_role"));
            ps.setObject(7, value.get("mute_roles"));
            ps.setObject(8, value.get("purge_use_role"));
            ps.setObject(9, value.get("purge_roles"));
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID: {} value: {}", guildID, value);
        }

    }

    /**
     * Loads a configuration from the table administrative_config
     * @param guildID The ID of the guild that needs administrative configurations
     * @return Map String, Object (boolean and String) which contains the configuration
     */
    public static Map<String, Object> getAdministrativeConfiguration(String guildID){

        Map<String, Object> row = new HashMap<>();
        checkIfSettingsExist(guildID, "administrative_config");

        try {
            Connection con = Configurations.DatabaseConnector.connect("Settings");
            if (con == null) return null;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM administrative_config WHERE guild_id = '" + guildID + "'");
            rs.next();

            for (int i = 1; rs.getMetaData().getColumnCount() > i; i++) {
                String key = rs.getMetaData().getColumnName(i + 1).replace("`", "");
                Object value;
                if (key.endsWith("_use_role")){
                    value = rs.getBoolean(i + 1);
                } else {
                    value = rs.getString(i + 1);
                }
                row.put(key, value);
            }

            rs.close();
            st.close();
            con.close();

        } catch (SQLException e){
            log.error(e.getMessage() + " ; VARIABLES: guildID {}", guildID);
        }

        return row;
    }

}
