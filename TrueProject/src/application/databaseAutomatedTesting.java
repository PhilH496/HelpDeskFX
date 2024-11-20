package application;
import java.sql.SQLException;
/*
 * Automated testing for the databaseHelper class
 */

public class databaseAutomatedTesting {
    public static void automateTesting() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            // Connect to the database
            dbHelper.connectToDatabase();
            System.out.println("Database connected successfully.");

            // Check if the database is empty
            System.out.println("Is database empty? " + dbHelper.isDatabaseEmpty());

            // Register a new user
            String testUsername = "BillyBob";
            String testPassword = "testPassword";
            String testRole = "Student";

            System.out.println("Registering a user...");
            dbHelper.register(testUsername, testPassword, testRole);

            // Check if the user exists
            System.out.println("Does user exist? " + dbHelper.doesUserExist(testUsername));

            // Attempt to log in with the new user
            System.out.println("Attempting to log in...");
            boolean loginSuccess = dbHelper.login(testUsername, testPassword, testRole);
            System.out.println("Login successful? " + loginSuccess);

            // Update user profile
            System.out.println("Updating profile...");
            dbHelper.updateProfile(testUsername, "test@example.com", "Test User", "Tester");

            // Check if profile is marked as completed
            System.out.println("Is profile completed? " + dbHelper.isProfileCompleted(testUsername));

            // Retrieve the preferred name
            String prefName = dbHelper.getPrefName(testUsername);
            System.out.println("Preferred name: " + prefName);

            // Assign a special access group
            System.out.println("Updating special access group...");
            dbHelper.updateSpecialGroup("SpecialGroup1", testUsername);
            System.out.println("Special access group: " + dbHelper.getSpecialAccessGroup(testUsername));

            // Assign viewing rights
            System.out.println("Updating viewing rights...");
            dbHelper.updateViewingRights(testUsername, "GroupA");

            // Assign admin rights
            System.out.println("Updating admin rights...");
            dbHelper.updateAdminRights(testUsername, "AdminGroupA");

            // Delete the test user
            System.out.println("Testing Delete user...");
            dbHelper.deleteUser(testUsername);
            System.out.println("User deleted.");

            // Check if user still exists
            System.out.println("Does user exist after deletion? " + dbHelper.doesUserExist(testUsername));
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Close the connection
            dbHelper.closeConnection();
            System.out.println("Database connection closed.");
        }
    }
}
