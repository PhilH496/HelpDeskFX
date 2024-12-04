package application;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class junitTesting {

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"; // In-memory database for testing
    private static final String USER = "sa";
    private static final String PASS = "";

    private Connection connection;
    private DatabaseHelper dbHelper;

    @BeforeEach
    void setUp() throws Exception {
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testDatabaseConnection() {
        assertNotNull(connection, "Connection should not be null");
    }

    @Test
    void testRegisterAndLoginUser() throws SQLException {
        dbHelper.register("Ben", "password123", "Admin");
        //assertFalse(dbHelper.isDatabaseEmpty(), "Database should not be empty after registering a user");
        assertTrue(dbHelper.login("Ben", "password123", "Admin"), "User should be able to log in with correct credentials");
        assertFalse(dbHelper.login("Ben", "wrongpassword", "Admin"), "Login should fail with incorrect password");
        assertTrue(dbHelper.doesUserExist("Ben"), "User should exist after registration"); // Test user Existence
    }
    
    @Test
    void testUpdateViewAndAdminRights() throws SQLException {
        dbHelper.updateViewingRights("Ben", "GroupA");
        assertTrue(dbHelper.isGroupInViewingRights("Ben", "GroupA"), "Group should be in viewing Rights");
    	dbHelper.updateAdminRights("Ben", "GroupB");
    	assertTrue(dbHelper.isGroupInAdminRights("Ben", "GroupB"), "Group should be in Admin Rights");
    }
    
    @Test
    void testDeleteAdminAndViewRights() throws SQLException {
        dbHelper.deleteViewingRights("Ben", "GroupA");
        assertFalse(dbHelper.isGroupInViewingRights("Ben", "GroupA"), "Group should not be in viewing Rights anymore");
    	dbHelper.deleteAdminRights("Ben", "GroupB");
    	assertFalse(dbHelper.isGroupInAdminRights("Ben", "GroupB"), "Group should not be in Admin Rights anymore");
    }
    
    @Test
    void testDeleteUser() throws SQLException {
    	dbHelper.deleteUser("Ben");
    	assertFalse(dbHelper.doesUserExist("Ben"), "User should not exist anymore. Delete Successful");
    }


}
