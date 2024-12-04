package application;

import java.sql.SQLException;

import Encryption.EncryptionHelper;

/*
 * Automated testing for the DatabaseHelper class, including encryption
 */
public class databaseAutomatedTesting {
    public static void automateTesting() throws Exception {
        DatabaseHelper dbHelper = new DatabaseHelper();
        EncryptionHelper encryptionHelper = new EncryptionHelper();
        articleDatabaseHelper articleDbHelper = new articleDatabaseHelper();

        try {
            // Connect to the database, else will output error
            dbHelper.connectToDatabase();
            articleDbHelper.connectToDatabase();
            System.out.println("Database connected successfully.");
            System.out.println("=================");

            // Check if the database is empty
            System.out.println("Is database empty? " + dbHelper.isDatabaseEmpty());
            System.out.println("=================");

            // Register a new user and then checks if exists
            String testUsername = "Maxwell";
            String testPassword = "1Qaz2wsx!";
            String testRole = "Admin";
            System.out.println("Registering a user...");
            dbHelper.register(testUsername, testPassword, testRole);
            System.out.println("=================");
            System.out.println("Does user exist? " + dbHelper.doesUserExist(testUsername));
            System.out.println("=================");
            // Additional validation: Check if registration succeeded
            if (dbHelper.doesUserExist(testUsername)) {
                System.out.println("User registration successful.");
                
                // Attempt to log in with the new user
                System.out.println("Attempting to log in...");
                boolean loginSuccess = dbHelper.login(testUsername, testPassword, testRole);
                System.out.println("Login successful? " + loginSuccess);
                System.out.println("=================");

                // Verify login result to continue forward
                if (loginSuccess) {
                    System.out.println("Login succeeded.");
                    
                    // Update user profile
                    System.out.println("Updating profile...");
                    dbHelper.updateProfile(testUsername, "test@example.com", "Test User", "Tester");
                    System.out.println("=================");

                    // Additional validation: Check if profile is marked as completed
                    if (dbHelper.isProfileCompleted(testUsername)) {
                        System.out.println("Profile updated successfully.");
                    } else {
                        System.out.println("Profile update failed.");
                    }
                    System.out.println("=================");

                    // Retrieve the preferred name
                    String prefName = dbHelper.getPrefName(testUsername);
                    System.out.println("Preferred name: " + prefName);
                    System.out.println("=================");

                    // Additional validation: Check if preferred name retrieval succeeded
                    if (prefName != null && !prefName.isEmpty()) {
                        System.out.println("Preferred name retrieved successfully: " + prefName);
                    } else {
                        System.out.println("Failed to retrieve preferred name.");
                    }
                    System.out.println("=================");

                    // Assign a special access group
                    System.out.println("Updating special access group...");
                    dbHelper.updateSpecialGroup("SpecialGroup1", testUsername);
                    System.out.println("Special access group: " + dbHelper.getSpecialAccessGroup(testUsername));
                    System.out.println("=================");

                    // Additional validation: Confirm special access group update
                    if ("SpecialGroup1".equals(dbHelper.getSpecialAccessGroup(testUsername))) {
                        System.out.println("Special access group updated successfully.");
                    } else {
                        System.out.println("Special access group update failed.");
                    }
                    System.out.println("=================");

                    // Assign viewing rights
                    System.out.println("Updating viewing rights...");
                    dbHelper.updateViewingRights(testUsername, "Special Group");
                    if (dbHelper.isGroupInViewingRights(testUsername, "Special Group")) {
                        System.out.println("Updating success!");
                    } else {
                        System.out.println("Updating failed!");
                    }
                    System.out.println("=================");

                    // Assign admin rights
                    System.out.println("Updating admin rights...");
                    dbHelper.updateAdminRights(testUsername, "AdminGroupA");
                    if (dbHelper.isGroupInAdminRights(testUsername, "AdminGroupA")) {
                        System.out.println("Updating success!");
                    } else {
                        System.out.println("Updating failed!");
                    }
                    System.out.println("=================");
                    //displays if viewing and admin rights updated or not
                    dbHelper.displayUsersByAdmin(testUsername);
                    System.out.println("=================");
                    
                    
                    // Test Data for Encrpytion with correct access
                    String groupType = "Special Group";
                    String level = "Intermediate";
                    String group = "Test Group";
                    String title = "Encryption Test Article";
                    String author = "John Doe";
                    String abstracts = "This is an abstract.";
                    String keywords = "test, encryption, security";
                    String body = "This is the body of the article.";
                    String references = "References for the article.";
                    boolean update = false; 
                    articleDbHelper.articleCreation(groupType, level, group, title, author, abstracts, keywords, body, references, update, 0);
                    System.out.println("Loading article...");
                    int articleNum = articleDbHelper.getArticleId(title); //will autocheck article and determine if error or not
                    String retrievedArticle = articleDbHelper.viewArticle(articleNum, testUsername);
                    if (retrievedArticle != null && retrievedArticle.contains(body)) {
                        System.out.println("Article access granted!");
                    } else {
                        System.out.println("Article access not granted!");
                    }
                    System.out.println("=================");
                    System.out.println(retrievedArticle);
                    System.out.println("=================");
                    

                    // Delete viewing rights
                    System.out.println("Deleting viewing rights...");
                    dbHelper.deleteViewingRights(testUsername, "Special Group");
                    if (!dbHelper.isGroupInViewingRights(testUsername, "Special Group")) {
                        System.out.println("Deleting success!");
                    } else {
                        System.out.println("Deleting failed!");
                    }
                    System.out.println("=================");

                    // Delete admin rights
                    System.out.println("Deleting admin rights...");
                    dbHelper.deleteAdminRights(testUsername, "AdminGroupA");
                    if (!dbHelper.isGroupInAdminRights(testUsername, "AdminGroupA")) {
                        System.out.println("Deleting success!");
                    } else {
                        System.out.println("Deleting failed!");
                    }
                    System.out.println("=================");
                  //displays if viewing and admin rights deleted or not
                    dbHelper.displayUsersByAdmin(testUsername);
                    System.out.println("=================");
                    
                    // Test Data for Encrpytion with incorrect access
                    System.out.println("Loading article...");
                    retrievedArticle = articleDbHelper.viewArticle(articleNum, testUsername);
                    if (retrievedArticle != null && retrievedArticle.contains(body)) {
                        System.out.println("Article access granted!");
                    } else {
                        System.out.println("Article access not granted!");
                    }
                    System.out.println("=================");
                    System.out.println(retrievedArticle);
                    System.out.println("=================");
                    
                    

                    // Delete the test user and check if user actually deleted
                    System.out.println("Testing Delete user...");
                    dbHelper.deleteUser(testUsername);
                    System.out.println("User deleted.");
                    System.out.println("=================");
                    if (!dbHelper.doesUserExist(testUsername)) {
                        System.out.println("User deletion successful.");
                    } else {
                        System.out.println("User deletion failed.");
                    }
                    System.out.println("=================");
                    
                    
                } else {
                    System.out.println("Login failed.");
                }
                System.out.println("=================");

            } else {
                System.out.println("User registration failed.");
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Close the connection
            dbHelper.closeConnection();
            System.out.println("Database connection closed.");
        }
    }
}
