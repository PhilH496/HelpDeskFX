package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	//Creation of sql table for needed saved information
	private void createTables() throws SQLException {
	    String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "username VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "role VARCHAR(20), "
	            + "email VARCHAR(255), "
	            + "name VARCHAR(255), "
	            + "preferred_name VARCHAR(255), "
	            + "profile_completed BOOLEAN DEFAULT FALSE,"  
	            + "specialAccessGroup VARCHAR(255), "          
	            + "viewingRights VARCHAR(255) DEFAULT '', "
	            + "adminRights VARCHAR(255) DEFAULT ''"
	            + ")";
	    statement.execute(userTable);
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	//registers user into database with username, password, role
	public void register(String username, String password, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}

	public boolean login(String username, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	public boolean doesUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	//Displays all users along with their roles, rights, etc.
	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id = rs.getInt("id"); 
			String username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  
			String specialGroup = rs.getString("specialAccessGroup");
			String viewingRights = rs.getString("viewingRights");
			String adminRights = rs.getString("adminRights");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role + ", "); 
			System.out.println("Special Access Group: " + specialGroup);
			System.out.println("ViewingRights: " + viewingRights);
			System.out.println("AdminRights: " + adminRights);
			System.out.println();
		} 
	}
	
	// Method to return all users stored in the database into an ArrayList
	public ArrayList<User> getAllUsers(String specifiedRole) throws SQLException{
        ArrayList<User> users = new ArrayList<User>();
        String sql = null;
		 // Construct SQL based on groups
	    if (specifiedRole.equals("None")) {
	        sql = "SELECT * FROM cse360users";  // Select all if no specific groups
	    } else {
	    	sql = "SELECT * FROM cse360users WHERE role = "+ "'" + specifiedRole + "'";
	    }
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id = rs.getInt("id"); 
			String username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			users.add(new User(id, username, password, role));
		} 
		return users;
	}
	
	// Method to delete user from the database
	public void deleteUser(String username) throws SQLException {
	    String deleteUserQuery = "DELETE FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
	        pstmt.setString(1, username);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("User with username " + username + " deleted successfully.");
	        } else {
	            System.out.println("No user found with username " + username + ".");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public boolean isProfileCompleted(String username) throws SQLException {
	    String query = "SELECT profile_completed FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("profile_completed");
	        }
	    }
	    return false; 
	}
	
	public void markProfileCompleted(String username) throws SQLException {
	    String query = "UPDATE cse360users SET profile_completed = TRUE WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	    }
	}
	
	public void closeConnection() {
		try{
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if(connection!=null) connection.close();
		} catch(SQLException se){
			se.printStackTrace();
		} 
	}
	// Updates user data in the database with their email, name and preferredName 
	public void updateProfile(String username, String email, String name, String preferredName) throws SQLException {
	    String updateProfileQuery = "UPDATE cse360users SET email = ?, name = ?, preferred_name = ?, profile_completed = TRUE WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateProfileQuery)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, name);
	        pstmt.setString(3, preferredName);
	        pstmt.setString(4, username);
	        int rowsUpdated = pstmt.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            System.out.println("Profile updated created for " + username);
	        } else {
	            System.out.println("No user found with username " + username);
	        }
	    }
	}
	// Returns preferred name from database given the user's username
	public String getPrefName(String username) throws SQLException {
	    String sql = "SELECT preferred_name FROM cse360users WHERE username = ?";
	    String prefname = null;

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) { 
	            prefname = rs.getString("preferred_name");
	        }
	    }
	    if (prefname == null) {
	        return "User"; // Default message or return username as fallback
	    }
	    return prefname;
	}
	
	//Obtains the user special access group via sql. Mainly for comparison checking
	public String getSpecialAccessGroup(String userName) throws SQLException {
	    String sql = "SELECT specialAccessGroup FROM cse360users WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	        pstmt.setString(1, userName);  
	        ResultSet rs = pstmt.executeQuery(); 

	        // If a record exists, return the special access group
	        if (rs.next()) {
	            return rs.getString("specialAccessGroup");
	        } else {
	            return null; // If the user is not found, return null
	        }
	    }
	}

	//Updates the special group if no group is found or it needs to change
	public void updateSpecialGroup(String newGroup, String userName) {
	    if (newGroup == null || newGroup.isEmpty()) {
	        System.out.println("Invalid group name.");
	        return; // if newGroup is invalid
	    }

	    if (userName == null || userName.isEmpty()) {
	        System.out.println("Invalid username.");
	        return; // userName is invalid
	    }

	    String updateQuery = "UPDATE cse360users SET specialAccessGroup = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newGroup);  // Set the new group for the specialAccessGroup column
	        pstmt.setString(2, userName);  // Set username

	        System.out.println("Executing query: " + updateQuery);
	        System.out.println("With values: newGroup=" + newGroup + ", userName=" + userName);

	        int rowsAffected = pstmt.executeUpdate();  // Execute the update
	       
	        if (rowsAffected > 0) {
	            System.out.println("Special access group updated successfully for user: " + userName);
	        } else {
	            System.out.println("No user found with username: " + userName);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error executing update: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	//Update the user's viewing rights to add people to see articles
	public void updateViewingRights(String username, String group) throws SQLException {
	    // Retrieve the current viewing rights for the user
	    String query = "SELECT viewingRights FROM cse360users WHERE username = ?";
	    String currentViewingRights = "";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, username);  // Set the username parameter
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            currentViewingRights = resultSet.getString("viewingRights");
	        }
	    }

	    // Check if the viewing rights are null or empty
	    if (currentViewingRights == null) {
	        currentViewingRights = "";  // In case the viewingRights column is null
	    }

	    // Only appends group name if it's not already present
	    if (!currentViewingRights.contains(group)) {
	        // If currentViewingRights is empty, set group as the first group
	        String updatedViewingRights = currentViewingRights.isEmpty() ? group : currentViewingRights + "," + group;

	        // Update the viewing rights in the database
	        String updateQuery = "UPDATE cse360users SET viewingRights = ? WHERE username = ?";

	        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	            updateStmt.setString(1, updatedViewingRights);
	            updateStmt.setString(2, username);
	            int rowsAffected = updateStmt.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("Viewing rights updated successfully.");
	            } else {
	                System.out.println("Failed to update viewing rights.");
	            }
	        }
	    } else {
	        System.out.println("Group is already in viewing rights.");
	    }
	}

	
	//Similar to updateViewingRights, this will update admin rights so a user may have admin rights
	public void updateAdminRights(String userName, String groupName) throws SQLException {
	    // Retrieve the current admin rights for the user
	    String query = "SELECT adminRights FROM cse360users WHERE username = ?";
	    String currentAdminRights = "";
	    
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, userName);  // Set the username parameter
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            currentAdminRights = resultSet.getString("adminRights");
	        }
	    }
	    
	    // Check if the group is already in the admin rights
	    if (currentAdminRights == null) {
	        currentAdminRights = ""; // In case the adminRights column is null
	    }

	    if (!currentAdminRights.contains(groupName)) {
	        // If currentAdminRights is empty, set groupName as the first group
	        String updatedAdminRights = currentAdminRights.isEmpty() ? groupName : currentAdminRights + "," + groupName;

	        // Update the admin rights in the database
	        String updateQuery = "UPDATE cse360users SET adminRights = ? WHERE username = ?";
	        
	        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	            updateStmt.setString(1, updatedAdminRights);
	            updateStmt.setString(2, userName);
	            int rowsAffected = updateStmt.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("Admin rights updated successfully.");
	            } else {
	                System.out.println("Failed to update admin rights.");
	            }
	        }
	    } else {
	        System.out.println("Group is already in admin rights.");
	    }
	}
	
	//To delete special access group from a user if needed
	public void deleteSpecialAccess(int id) throws SQLException { 
		 String fetchGroupSql = "SELECT specialAccessGroup FROM cse360users WHERE id = ?";
		    String group = null;

		    try (PreparedStatement fetchStmt = connection.prepareStatement(fetchGroupSql)) {
		        fetchStmt.setInt(1, id);
		        try (ResultSet resultSet = fetchStmt.executeQuery()) {
		            if (resultSet.next()) {
		                group = resultSet.getString("specialAccessGroup");
		            } else {
		                System.out.println("No user found with the specified ID.");
		                return; // Exit if no user is found
		            }
		        }
		    }
	    String updateSql = """
	        UPDATE cse360users 
	        SET 
	            specialAccessGroup = NULL, 
	            viewingRights = REPLACE(viewingRights, ?, ''), 
	            adminRights = REPLACE(adminRights, ?, '') 
	        WHERE id = ?
	    """;
	    
	    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
	        updateStmt.setString(1, group); // Remove matching group from viewingRights
	        updateStmt.setString(2, group); // Remove matching group from adminRights
	        updateStmt.setInt(3, id); // Specify the user ID

	        int rowsAffected = updateStmt.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("Special access rights updated successfully.");
	        } else {
	            System.out.println("Failed to update special access rights.");
	        }
	    }
	}
	
	// Deletes viewing rights of a user, typicallly student (usually if user is admin or owner of group)
	public void deleteViewingRights(String username, String group) throws SQLException {
	    // Retrieve the current viewing rights for the user
	    String query = "SELECT viewingRights FROM cse360users WHERE username = ?";
	    String currentViewingRights = "";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, username);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            currentViewingRights = resultSet.getString("viewingRights");
	        }
	    }

	    // Check if the group exists in viewingRights and remove it
	    if (currentViewingRights != null && !currentViewingRights.isEmpty()) {
	        String[] rightsArray = currentViewingRights.split(",");
	        List<String> updatedRights = new ArrayList<>();

	        for (String right : rightsArray) {
	            if (!right.trim().equals(group)) {
	                updatedRights.add(right.trim());
	            }
	        }

	        String updatedViewingRights = String.join(",", updatedRights);

	        // Update the viewing rights in the database
	        String updateQuery = "UPDATE cse360users SET viewingRights = ? WHERE username = ?";
	        
	        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	            updateStmt.setString(1, updatedViewingRights);
	            updateStmt.setString(2, username);
	            int rowsAffected = updateStmt.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("Viewing rights updated successfully.");
	            } else {
	                System.out.println("Failed to update viewing rights.");
	            }
	        }
	    } else {
	        System.out.println("Group not found in viewing rights.");
	    }
	}
	
	//Deletes admin rights in case user should not be apart of it
	public void deleteAdminRights(String username, String group) throws SQLException {
	    // Retrieve the current admin rights for the user
	    String query = "SELECT adminRights FROM cse360users WHERE username = ?";
	    String currentAdminRights = "";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, username);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            currentAdminRights = resultSet.getString("adminRights");
	        }
	    }

	    // Check if the group exists in adminRights and remove it
	    if (currentAdminRights != null && !currentAdminRights.isEmpty()) {
	        String[] rightsArray = currentAdminRights.split(",");
	        List<String> updatedRights = new ArrayList<>();

	        for (String right : rightsArray) {
	            if (!right.trim().equals(group)) {
	                updatedRights.add(right.trim());
	            }
	        }

	        String updatedAdminRights = String.join(",", updatedRights);

	        // Update the admin rights in the database
	        String updateQuery = "UPDATE cse360users SET adminRights = ? WHERE username = ?";
	        
	        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	            updateStmt.setString(1, updatedAdminRights);
	            updateStmt.setString(2, username);
	            int rowsAffected = updateStmt.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("Admin rights updated successfully.");
	            } else {
	                System.out.println("Failed to update admin rights.");
	            }
	        }
	    } else {
	        System.out.println("Group not found in admin rights.");
	    }
	}
	
	//Checks if a user has admin rights to a group. So, this checks if the user is in that group first
	public boolean isGroupInAdminRights(String username, String group) throws SQLException {
	    String query = "SELECT adminRights FROM cse360users WHERE username = ? AND adminRights IS NOT NULL";
	    
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, username);  // Set the username parameter
	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            if (resultSet.next()) {
	                String adminRights = resultSet.getString("adminRights");
	                
	                // Split adminRights by commas and check if the group is present
	                String[] rightsArray = adminRights.split(",");
	                for (String right : rightsArray) {
	                    if (right.trim().equals(group)) { // Check for exact match after trimming
	                        return true; // Group found in this user's admin rights
	                    }
	                }
	            }
	        }
	    }
	    return false; // Group not found in the admin rights for this user
	}
	
	//Checks if user has viewing rights instead to a group and compares it via what group user is in
	public boolean isGroupInViewingRights(String username, String group) throws SQLException {
	    // Return false immediately if group is null
	    if (group == null) {
	        return false;
	    }

	    // Query to select viewingRights for the specified user
	    String query = "SELECT viewingRights FROM cse360users WHERE username = ? AND viewingRights IS NOT NULL";
	    
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, username); // Set the username parameter
	        
	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            if (resultSet.next()) {
	                String viewingRights = resultSet.getString("viewingRights");
	                
	                // Split viewingRights by commas and check if the group is present
	                String[] rightsArray = viewingRights.split(",");
	                for (String right : rightsArray) {
	                    if (right.trim().equals(group)) { // Check for exact match
	                        return true; // Group found in the viewing rights for this user
	                    }
	                }
	            }
	        }
	    }
	    return false; // In case not found
	}
	
	//returns all usernames in the viewing rights group
    public List<String> getUsernamesByViewingRightsGroup(String group) throws SQLException {
        List<String> usernames = new ArrayList<>();
        
        String query = "SELECT username FROM cse360users WHERE viewingRights LIKE ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + group + "%"); 
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }
        }
        
        return usernames;
    }
	
    // returns all usernames in the admin rights group
    public List<String> getUsernamesByAdminRightsGroup(String group) throws SQLException {
        List<String> usernames = new ArrayList<>();
        
        String query = "SELECT username FROM cse360users WHERE adminRights LIKE ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + group + "%"); 
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }
        }
        
        return usernames;
    }

	
	// Method to return connection
	public Connection getConnection() {
		return this.connection;
	}
}