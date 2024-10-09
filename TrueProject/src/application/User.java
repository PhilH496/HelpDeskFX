package application;

/**
 * The User class represents a user in the application, mapping to users stored in the database.
 * It includes attributes for user ID, username, password, and role. 
 * This class is primarily used to manage and retrieve user information.
 * Key features include:
 * - Constructor to initialize all user attributes.
 * - Getter methods to access user details such as ID, username, password, and role.
 */
public class User {
	private int id;
	private String username;
	private String password;
	private String role;
	
	// Constructor to initialize all user attributes
	public User(int id, String username, String password, String role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	// Getter Methods
	public int getID() {
		return this.id;
	}
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
	public String getRole() {
		return this.role;
	}
}