package application;

public class User {
	private int id;
	private String username;
	private String password;
	private String role;
	
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