package Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnector.class);

    /**
     * Tells the program whether to connect to the test or production database , leave blank for production use TEST for test
     */
    private static final String test = "TEST";

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
            Connection con = DriverManager.getConnection("jdbc:mariadb://192.168.1.177:3306/Oregano" + test + "Settings", mariaUser, mariaPass);
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
            return DriverManager.getConnection("jdbc:mariadb://192.168.1.177:3306/Oregano" + test + database, mariaUser, mariaPass);
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.fillInStackTrace().toString());
        }
        return null;
    }

}
