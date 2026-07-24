import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckSchema {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/tradeasset";
        String user = "postgres";
        String password = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("Checking user_roles table...");

            // Check if user_roles exists and in which schema
            String sql = "SELECT table_schema, table_name FROM information_schema.tables WHERE table_name = 'user_roles'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("Found: " + rs.getString("table_schema") + "." + rs.getString("table_name"));
            } else {
                System.out.println("user_roles table NOT FOUND");
            }
            rs.close();

            // Check users table
            sql = "SELECT table_schema, table_name FROM information_schema.tables WHERE table_name = 'users'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("Found users: " + rs.getString("table_schema") + "." + rs.getString("table_name"));
            }
            rs.close();

            // Check all schemas
            System.out.println("\nAll schemas:");
            rs = stmt.executeQuery("SELECT schema_name FROM information_schema.schemata ORDER BY schema_name");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("schema_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

