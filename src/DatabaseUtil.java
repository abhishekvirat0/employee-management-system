import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // URL to connect to the MySQL database 'employeedb' on localhost at port 3306
    private static final String URL = "jdbc:mysql://localhost:3306/employeedb";
    
    // Retrieve the database username from the environment variable 'DB_USER'
    private static final String USER = System.getenv("DB_USER");
    
    // Retrieve the database password from the environment variable 'DB_PASSWORD'
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    /*
     * The class DriverManager handles all open connections.
     * The function getConnection takes three arguments:
     * - URL: the database URL in the form jdbc:<database_system>:<database_name>
     * - USER: the username for the database connection
     * - PASSWORD: the password for the database connection
     * This method retrieves a connection to the database using the provided URL, username, and password.
     */
    public static Connection getConnection() throws SQLException {
        // Check if USER and PASSWORD are properly set
        if (USER == null || PASSWORD == null) {
            throw new IllegalArgumentException("Database username and/or password not provided as environment variables.");
        }
        
        // Establish and return the connection to the database
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
