import java.sql.*;

public class ConnectionCheck {
    public static void main(String[] args) {
        // Get a database connection
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            System.out.println("Connection established: " + conn);
        } catch (SQLException e) {
            System.err.println("Failed to establish connection: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the connection if it's open
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
