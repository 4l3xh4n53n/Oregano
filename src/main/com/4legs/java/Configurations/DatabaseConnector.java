package Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnector.class);


    /**
     * The databases host IP address
     */
    private static final String databaseHost = System.getenv("DATABASEHOST");

    /**
     * Username for MariaDB user
     */
    private static final String mariaUser = System.getenv("MARIAUSER");

    /**
     * Password for MariaDB user
     */
    private static final String mariaPass = System.getenv("MARIAPASS");

    /**
     * Tries to connect to the Settings database to see if the database is online
     * @return boolean value representing databases status
     */
    public static boolean isDatabaseOnline(){
        try {
            Class.forName ("org.mariadb.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mariadb://"+ databaseHost + "/Settings", mariaUser, mariaPass); // Connecting to the Settings database to test connection
            if (con != null){
                con.close();
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.fillInStackTrace().toString());
        }
        return false;
    }

    /**
     * Connects you to a database
     * @param database Name of the database you want to connect to
     * @return Connection to that database
     */
    public static Connection connect(String database) {
        try {
            Class.forName ("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mariadb://" + databaseHost + "/" + database, mariaUser, mariaPass);
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.fillInStackTrace().toString());
        }
        return null;
    }

}
