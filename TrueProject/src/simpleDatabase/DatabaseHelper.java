package simpleDatabase;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.io.*;
import java.util.*;
//import org.bouncycastle.util.Arrays;
import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
/*
 * This portion creates the main database functionalities of when called upon in the main.
 */
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
	
	private EncryptionHelper encryptionHelper;
	
	public DatabaseHelper() throws Exception {
		encryptionHelper = new EncryptionHelper();
	}

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
	//Used to create database table, adding necessary article items like title, author, abstract, keywords, body, references
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360article ("
		        + "id INT AUTO_INCREMENT PRIMARY KEY, "
		       // + "email VARCHAR(255) UNIQUE, "
		       // + "password VARCHAR(255), "
		        + "title VARCHAR(255), "
		        + "author VARCHAR(255), "
		        + "abstract VARCHAR(255), "
		        + "keywords VARCHAR(255), "
		        + "body TEXT, "
		        + "references VARCHAR(255))";
		statement.execute(userTable);
	}

	//backedUp will ask for either an input of 'backup' or 'restore' to successfully save data and restore data if accidentally lost
	//THis portion will also write to a specified file 'backup.txt' if backup is inputed to save for 'restore' use
	public void backedUp(String item) throws SQLException, IOException{
	    System.out.println("Type 'backup' to back up articles or 'restore' to restore from backup:");
	    
	    if (item.contains("Backup")) {
	        // Backup articles to a specific file
	        String articleFiles = "SELECT * FROM cse360article";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(articleFiles);
	             BufferedWriter writer = new BufferedWriter(new FileWriter("backup.txt"))) {
	            
	            while (rs.next()) {
	                String article = rs.getString("title") + "," +
	                                 rs.getString("author") + "," +
	                                 rs.getString("abstract") + "," +
	                                 rs.getString("keywords") + "," +
	                                 rs.getString("body") + "," +
	                                 rs.getString("references");
	                writer.write(article);
	                writer.newLine();
	            }
	            System.out.println("Backup completed successfully.");
	        } catch (SQLException | IOException e) {
	            e.printStackTrace();
	        }
	        
	    } else if (item.contains("Restore")) {
	        // Restore articles from backup file
	        String deleteArticle = "DELETE FROM cse360article";
	        String addingArticle = "INSERT INTO cse360article (title, author, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
	        
	        try (BufferedReader reader = new BufferedReader(new FileReader("backup.txt"))) {
	            
	            try (Statement stmt = connection.createStatement()) {
	                stmt.executeUpdate(deleteArticle);
	            }
	            
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] fields = line.split(",", 6);
	                
	                try (PreparedStatement pstmt = connection.prepareStatement(addingArticle)) {
	                    pstmt.setString(1, fields[0]); // title part
	                    pstmt.setString(2, fields[1]); // author part
	                    pstmt.setString(3, fields[2]); // abstract part
	                    pstmt.setString(4, fields[3]); // keywords part 
	                    pstmt.setString(5, fields[4]); // body part
	                    pstmt.setString(6, fields[5]); // references part
	                    pstmt.executeUpdate();
	                }
	            }
	            System.out.println("Restore completed successfully.");
	        } catch (IOException | SQLException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("Invalid input. Please type 'backup' or 'restore'.");
	    }
	}
	
	
	//deleteArticle will delete a specific article specified by its id number
	public void deleteArticle(int id) throws SQLException {
	    String deleteUserQuery = "DELETE FROM cse360article WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
	        pstmt.setInt(1, id);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article ID: " + id + " has been deleted successfully.");
	        } else {
	            System.out.println("No article ID: " + id + " found");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    
	}
	/*
	 * displayArticles will grab every item in the database for articles and list it out dependent on what the user wants.
	 * Besides the title, author, and id number, everything else will be encrypted and only decrypted when called upon. This
	 * feature will be added later on.
	 */
	public void displayArticles() throws Exception {
	    String sql = "SELECT * FROM cse360article"; 
	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);

	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String title = rs.getString("title");
	        String author = rs.getString("author");
	        String abstracts = rs.getString("abstract");
	        String keywords = rs.getString("keywords");
	        String body = rs.getString("body");
	        String references = rs.getString("references");

	        // This part will encrypt all the fields we want encrypted
	        String encryptedAbstract = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(abstracts.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
	        );

	        String encryptedKeywords = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(keywords.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
	        );

	        String encryptedBody = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
	        );

	        String encryptedReferences = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(references.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
	        );
	        

	        // Print article details
	        System.out.println("ID: " + id);
	        System.out.println("Title: " + title);
	        System.out.println("Author: " + author);
	        System.out.println("Abstract: " + encryptedAbstract);
	        System.out.println("Keywords: " + encryptedKeywords);
	        System.out.println("Body: " + encryptedBody);
	        System.out.println("References: " + encryptedReferences);
	        System.out.println("-------------");
	       
	       //To decrypt text for future use on project phase 2
/*	        String decryptedAbstract = new String(encryptionHelper.decrypt(
            Base64.getDecoder().decode(encryptedAbstract), 
            EncryptionUtils.getInitializationVector(author.toCharArray())
        ));

        String decryptedKeywords = new String(encryptionHelper.decrypt(
            Base64.getDecoder().decode(encryptedKeywords), 
            EncryptionUtils.getInitializationVector(author.toCharArray())
        ));

        String decryptedBody = new String(encryptionHelper.decrypt(
            Base64.getDecoder().decode(encryptedBody), 
            EncryptionUtils.getInitializationVector(author.toCharArray())
        ));

        String decryptedReferences = new String(encryptionHelper.decrypt(
            Base64.getDecoder().decode(encryptedReferences), 
            EncryptionUtils.getInitializationVector(author.toCharArray())
        )); */
	        
	        
	       // System.out.println("Abstract: " + decryptedAbstract);
	       // System.out.println("Keywords: " + decryptedKeywords);
	       // System.out.println("Body: " + decryptedBody);
	       // System.out.println("References: " + decryptedReferences);
	       // System.out.println("-------------");
	        
	    }

	    rs.close();
	    stmt.close();
	}
	
	
	// Check if the database is empty or not
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360article";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	/*
	 *  articleCreation will just add all the parts the user specifies they want for title, author,
	 *  abstract, keywords, body, and references and add it to the database.
	 */
	public void articleCreation(String title, String author, String abstracts, String keywords, String body, String references)
	{
		String insertArticle = "INSERT INTO cse360article (title, author, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, abstracts);
			pstmt.setString(4, keywords);
			pstmt.setString(5, body);
			pstmt.setString(6, references);
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
// Everything else underneath is from the homework not needed at the moment.
	
	
	public void register(String email, String password, String role) throws Exception {
		String encryptedPassword = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(password.getBytes(), EncryptionUtils.getInitializationVector(email.toCharArray()))
		);
		
		String insertUser = "INSERT INTO cse360users (email, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, encryptedPassword);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}
	

	public boolean login(String email, String password, String role) throws Exception {
		String encryptedPassword = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(password.getBytes(), EncryptionUtils.getInitializationVector(email.toCharArray()))
		);	
		
		String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, encryptedPassword);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public boolean doesUserExist(String email) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, email);
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


	public void displayUsersByAdmin() throws Exception{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String role = rs.getString("role");  
			String encryptedPassword = rs.getString("password"); 
			char[] decryptedPassword = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt(
							Base64.getDecoder().decode(
									encryptedPassword
							), 
							EncryptionUtils.getInitializationVector(email.toCharArray())
					)	
			);

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Encrypted Password: " + encryptedPassword); 
			System.out.print(", Decrypted Password: "); 
			EncryptionUtils.printCharArray(decryptedPassword);
			System.out.println(", Role: " + role); 
			
			Arrays.fill(decryptedPassword, '0');
		} 
	}
	
	public void displayUsersByUser() throws Exception{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String role = rs.getString("role");  
			String encryptedPassword = rs.getString("password"); 
			char[] decryptedPassword = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt(
							Base64.getDecoder().decode(
									encryptedPassword
							), 
							EncryptionUtils.getInitializationVector(email.toCharArray())
					)	
			);

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Password: "); 
			EncryptionUtils.printCharArray(decryptedPassword);
			System.out.println(", Role: " + role); 
			
			Arrays.fill(decryptedPassword, '0');
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

}
