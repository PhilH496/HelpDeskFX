package application;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
/*
 * This portion creates the main database functionalities of when called upon in the main.
 */
class articleDatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	
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
	//This portion will also write to a specified file 'backup.txt' if backup is inputed to save for 'restore' use
	public boolean backedUp(String item) {
	    // Split the input into operation (backup/restore) and file name
	    String[] parts = item.split(" ", 2);

	    if (parts.length < 2) {
	        System.out.println("Invalid input. Please provide a file name. Example: 'backup FILEONE' or 'restore FILEONE'");
	        return false;
	    }

	    String operation = parts[0].toLowerCase();
	    String fileName = parts[1];

	    try {
	        if (operation.equals("backup")) {
	            // Backup articles to the specified file
	            String articleQuery = "SELECT * FROM cse360article";
	            try (Statement stmt = connection.createStatement();
	                 ResultSet rs = stmt.executeQuery(articleQuery);
	                 BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

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
	                System.out.println("Backup completed successfully to file: " + fileName);
	                return true;

	            } catch (IOException e) {
	                System.out.println("Error writing to file: " + fileName);
	                e.printStackTrace();
	            }

	        } else if (operation.equals("restore")) {
	            // Restore articles from the specified backup file
	            String deleteArticles = "DELETE FROM cse360article";
	            String insertArticle = "INSERT INTO cse360article (title, author, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";

	            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	                try (Statement stmt = connection.createStatement()) {
	                    stmt.executeUpdate(deleteArticles);
	                }

	                String line;
	                while ((line = reader.readLine()) != null) {
	                    String[] fields = line.split(",", 6); // Expecting 6 fields

	                    if (fields.length == 6) {
	                        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
	                            pstmt.setString(1, fields[0]);
	                            pstmt.setString(2, fields[1]);
	                            pstmt.setString(3, fields[2]);
	                            pstmt.setString(4, fields[3]);
	                            pstmt.setString(5, fields[4]);
	                            pstmt.setString(6, fields[5]);
	                            pstmt.executeUpdate();
	                        }
	                    } else {
	                        System.out.println("Invalid data format in backup file: " + fileName);
	                    }
	                }
	                System.out.println("Restore completed successfully from file: " + fileName);
	                return true;

	            } catch (FileNotFoundException e) {
	                System.out.println("Backup file not found: " + fileName);
	                e.printStackTrace();
	            } catch (IOException e) {
	                System.out.println("Error reading from file: " + fileName);
	                e.printStackTrace();
	            }
	        } else {
	            System.out.println("Invalid operation. Please type 'backup' or 'restore' followed by the file name.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Database error occurred.");
	        e.printStackTrace();
	    }

	    return false;
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
	public String displayArticles() throws Exception {
	    StringBuilder articles = new StringBuilder();
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

	        // Append article details to the StringBuilder
	        articles.append("ID: ").append(id).append("\n")
	                .append("Title: ").append(title).append("\n")
	                .append("Author: ").append(author).append("\n")
	                .append("Abstract: ").append(abstracts).append("\n")
	                .append("Keywords: ").append(keywords).append("\n")
	                .append("Body: ").append(body).append("\n")
	                .append("References: ").append(references).append("\n")
	                .append("-------------\n");
	    }

	    rs.close();
	    stmt.close();

	    // Return the collected articles as a string
	    return articles.toString();
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
