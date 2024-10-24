package application;
import java.sql.*;
import java.util.ArrayList;


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

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20),"
				+ "email VARCHAR(255),"
				+ "name VARCHAR(255),"
				+ "preferred_name VARCHAR(255),"
				+ "profile_completed BOOLEAN DEFAULT FALSE)";
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
			String prefName = rs.getString("preferred_name");
			String name = rs.getString("name");
			String email = rs.getString("email");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
			System.out.print(", Preferred name: " + prefName); 
			System.out.print(", Name: " + name); 
			System.out.println(", Email: " + email); 
		} 
	}
	
	// Method to return all users stored in the database into an ArrayList
	public ArrayList<User> getAllUsers() throws SQLException{
        ArrayList<User> users = new ArrayList<User>();
		String sql = "SELECT * FROM cse360users"; 
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
	
	// Method to return connection
	public Connection getConnection() {
		return this.connection;
	}
}