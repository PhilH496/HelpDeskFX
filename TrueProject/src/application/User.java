package application;

public class User {
	private int id;
	private String email;
	private String password;
	private String role;
	
	public User(int id, String email, String password, String role) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
	// Getter Methods
	public int getID() {
		return this.id;
	}
	public String getEmail() {
		return this.email;
	}
	public String getPassword() {
		return this.password;
	}
	public String getRole() {
		return this.role;
	}
}