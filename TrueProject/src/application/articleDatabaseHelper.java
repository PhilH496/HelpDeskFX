package application;
import java.sql.*;
import java.util.Base64;

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
	// Used to create database table, adding necessary article items like ID, level, group title, author, 
	// abstract, keywords, body, references
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360article ("
				+ "groupType VARCHAR(255), "
		        + "id INT AUTO_INCREMENT PRIMARY KEY, "
		        + "level VARCHAR(255), "
		        + "article_group VARCHAR(255), "
		        + "title VARCHAR(255), "
		        + "author VARCHAR(255), "
		        + "abstract VARCHAR(255), "
		        + "keywords VARCHAR(255), "
		        + "body TEXT, "
		        + "references VARCHAR(255))";
		statement.execute(userTable);
	}

	// Backs up articles to a file, optionally filtered by group.
	public boolean backUpFile(String file, String userGroup) throws Exception{
		String sql = null;
		 // Construct SQL based on groups
	    if (userGroup.equals("None")) {
	        sql = "SELECT * FROM cse360article";  // Select all if no specific groups
	    } else {
	    	sql = "SELECT * FROM cse360article WHERE article_group = "+ "'" + userGroup + "'";
	    }
	   
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		FileWriter myWriter = new FileWriter(file);
		int count = 0;
		while(rs.next()) { 
			String groupType = rs.getString("groupType");
			String level = rs.getString("level");
			String group = rs.getString("article_group");
			String title = rs.getString("title");
            String author = rs.getString("author");
            String abstracts = rs.getString("abstract");
            String keywords = rs.getString("keywords");
            String body = rs.getString("body");
            String references = rs.getString("references");
			try {
				myWriter.write("Group Type: " + groupType + "\n");
                myWriter.write("Level: " + level + "\n");
                myWriter.write("Group: " + group + "\n");
                myWriter.write("Title: " + title + "\n");
                myWriter.write("Author(s): " + author + "\n");
                myWriter.write("Abstract: " + abstracts + "\n");
                myWriter.write("Keyword(s): " + keywords + "\n");
                myWriter.write("Body: " + body + "\n");
                myWriter.write("Reference(s): " + references + "\n");
                myWriter.write("==========\n");
  
			} catch(IOException IO) {
            	System.out.print("Unexpected IO exception occured\n");
            	return false;
            }
		}
		myWriter.close();
		return true;
	}
	
	// loadFromFile loads data from a file and is passed a boolean to determine how it does so. 
	// True will delete all existing articles and replace them with what is in the specified file.
	// False will instead update the current database with the data from the file while skipping duplicate titles.
	public boolean loadFromFile(String file, boolean replaceAll) {
	    String sql = "INSERT INTO cse360article (groupType, level, article_group, title, author, "
	    		+ "abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        BufferedReader reader = new BufferedReader(new FileReader(file));
	        if (replaceAll) {
	            // Delete all existing articles
	            String deleteSQL = "DELETE FROM cse360article";
	            try (Statement stmt = connection.createStatement()) {
	                stmt.executeUpdate(deleteSQL);
	            }
	        }
	        
	        String line;
	        String groupType = null, level = null, group = null, title = null, author = null, 
	        		abstracts = null, keywords = null, body = null, references = null;
	        
	        while ((line = reader.readLine()) != null) {
	            if (line.startsWith("Group Type: ")) {
	                groupType = line.substring(11).trim();
	            } else if (line.startsWith("Level: ")) {
	                level = line.substring(7).trim();
	            } else if (line.startsWith("Group: ")) {
	                group = line.substring(7).trim();
	            } else if (line.startsWith("Title: ")) {
	                title = line.substring(7).trim();
	            } else if (line.startsWith("Author(s): ")) {
	                author = line.substring(11).trim();
	            } else if (line.startsWith("Abstract: ")) {
	                abstracts = line.substring(10).trim();
	            } else if (line.startsWith("Keyword(s): ")) {
	                keywords = line.substring(12).trim();
	            } else if (line.startsWith("Body: ")) {
	                body = line.substring(6).trim();
	            } else if (line.startsWith("Reference(s): ")) {
	                references = line.substring(14).trim();
	            } else if (line.startsWith("==========")) {
	                // While updating current database, skip adding existing articles based on title.
	                if (!replaceAll) {
	                    String checkSQL = "SELECT COUNT(*) FROM cse360article WHERE title = ?";
	                    try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
	                        checkStmt.setString(1, title);
	                        ResultSet rs = checkStmt.executeQuery();
	                        if (rs.next() && rs.getInt(1) > 0) {
	                            // Skip this article if a duplicate is found
	                            continue;
	                        }
	                    }
	                }
	                
	                // Insert the article into the database
	                pstmt.setString(1, groupType);
	                pstmt.setString(2, level);
	                pstmt.setString(3, group);
	                pstmt.setString(4, title);
	                pstmt.setString(5, author);
	                pstmt.setString(6, abstracts);
	                pstmt.setString(7, keywords);
	                pstmt.setString(8, body);
	                pstmt.setString(9, references);
	                pstmt.executeUpdate();
	                
	                // Reset fields for the next article
	                groupType = level = group = title = author = abstracts = keywords = body = references = null;
	            }
	        }
	        reader.close();
	    } catch (IOException | SQLException e) {
	        System.out.println("Error reading from file: " + e.getMessage());
	        return false;
	    }
	    return true;
	}
	
	// deleteArticle will delete a specific article specified by its title 
	public void deleteArticle(String title) throws SQLException {
	    String deleteUserQuery = "DELETE FROM cse360article WHERE title = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
	        pstmt.setString(1, title);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("Article with title \"" + title + "\" deleted successfully.");
	        } else {
	            System.out.println("No article found with title \"" + title + "\".");
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
	public String viewArticle(int sequenceNumber) throws SQLException {
		String findUserQuery = "SELECT * FROM cse360article WHERE id = ?";
		StringBuilder article = new StringBuilder();
	    try (PreparedStatement stmt = connection.prepareStatement(findUserQuery)) {
            stmt.setInt(1, sequenceNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
    	        String level = rs.getString("level");
    	        String group = rs.getString("article_group");
    	        String title = rs.getString("title");
    	        String author = rs.getString("author");
    	        String abstracts = rs.getString("abstract");
    	        String keywords = rs.getString("keywords");
    	        String body = rs.getString("body");
    	        String references = rs.getString("references");

    	        article.append("Sequence Number: ").append(id).append("\n")
    	        		.append("Level: ").append(level).append("\n")
    	        		.append("Group: ").append(group).append("\n")
    	                .append("Title: ").append(title).append("\n")
    	                .append("Author: ").append(author).append("\n")
    	                .append("Abstract: ").append(abstracts).append("\n")
    	                .append("Keyword(s): ").append(keywords).append("\n")
    	                .append("Body: ").append(body).append("\n")
    	                .append("Reference(s): ").append(references).append("\n")
    	                .append("-------------\n");
            }
        }
	    return article.toString();
	}
	
	public String displayArticles(String userRole) throws Exception {
	    StringBuilder articleDetails = new StringBuilder();
	    StringBuilder result = new StringBuilder();
	    String sql = "SELECT * FROM cse360article"; 
	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);
	    
	    int count = 0;
	    while (rs.next()) {
	    	String groupType = rs.getString("groupType");
	    	int id = rs.getInt("id");
	        String level = rs.getString("level");
	        String group = rs.getString("article_group");
	        String title = rs.getString("title");
	        String author = rs.getString("author");
	        String abstracts = rs.getString("abstract");
	        String keywords = rs.getString("keywords");
	        //String body = rs.getString("body");
	        //String references = rs.getString("references");
	        

	        // Append article details to the StringBuilder
	        articleDetails.append("Group Type: ").append(groupType).append("\n")
	        		.append("ID: ").append(id).append("\n")
	        		.append("Level: ").append(level).append("\n")
	        		.append("Group: ").append(group).append("\n")
	                .append("Title: ").append(title).append("\n")
	                .append("Author: ").append(author).append("\n")
	                .append("Abstract: ").append(abstracts).append("\n")
	                .append("Keyword(s): ").append(keywords).append("\n")
	        		.append("-------------\n");
	        
	        /*If admin, hide body and references
	        if (!userRole.equals("Admin")) {
	            articles.append("Body: ").append(body).append("\n")
                .append("Reference(s): ").append(references).append("\n");
	        } else {
	            articles.append("Body: [Hidden]\n")
	                    .append("Reference(s): [Hidden]\n");
	        }
			*/
	        //articles.append("-------------\n");
	        
	  /*
	   * This part will encrypt all the fields we want encrypted
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

	   * 
	   * This part will decrypt everything	        
	         String decryptedAbstract = new String(encryptionHelper.decrypt(
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
	        
	       count++;
	    }
	    result.append("Total Article Count: " + count + "\n\n");
        result.append(articleDetails);
	    rs.close();
	    stmt.close();

	    // Return the collected articles as a string
	    return result.toString();
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
	 *  articleCreation will just add all the parts the user specifies they want for level, group, title, author,
	 *  abstract, keywords, body, and references and add it to the database.
	 */
	public void articleCreation(String groupType, String level, String group, String title, String author, 
			String abstracts, String keywords, String body, String references, boolean update, int id) {
		String sql;
		if (update == true) {
			sql = "UPDATE cse360article SET groupType = ?, level = ?, article_group = ?, title = ?, " +
		              "author = ?, abstract = ?, keywords = ?, body = ?, references = ? " +
		              "WHERE id = " + "'" + id + "'";
		} else {
			sql = "INSERT INTO cse360article (groupType, level, article_group, title, author, abstract, keywords, body, references) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		}
		
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, groupType);
			pstmt.setString(2, level);
			pstmt.setString(3, group);
			pstmt.setString(4, title);
			pstmt.setString(5, author);
			pstmt.setString(6, abstracts);
			pstmt.setString(7, keywords);
			pstmt.setString(8, body);
			pstmt.setString(9, references);
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	//This portions helps to facilitate the 'search' function and query articles based on keywords
    public String searchByKeywordAndLevel(String keyword, String skillLevel) throws SQLException {
        StringBuilder articleDetails = new StringBuilder();
        StringBuilder result = new StringBuilder();
        StringBuilder query = new StringBuilder("SELECT * FROM cse360article WHERE (keywords LIKE ? OR title LIKE ? OR author LIKE ? OR abstract LIKE ?)");
        if ((skillLevel != null) && (!skillLevel.isEmpty()))
        {
        	query.append(" AND level = ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            if (skillLevel != null && !skillLevel.isEmpty()) {
                stmt.setString(5, skillLevel);
            }
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
            	int id = rs.getInt("id");
    	        String level = rs.getString("level");
    	        String group = rs.getString("article_group");
    	        String title = rs.getString("title");
    	        String author = rs.getString("author");
    	        String abstracts = rs.getString("abstract");
    	        String keywords = rs.getString("keywords");

    	        articleDetails.append("Sequence Number: ").append(id).append("\n")
    	        		.append("Level: ").append(level).append("\n")
    	        		.append("Group: ").append(group).append("\n")
    	                .append("Title: ").append(title).append("\n")
    	                .append("Author: ").append(author).append("\n")
    	                .append("Abstract: ").append(abstracts).append("\n")
    	                .append("Keyword(s): ").append(keywords).append("\n")
    	                .append("-------------\n");
    	        count++;
            }
            result.append("Total Article Count: " + count + "\n\n");
            result.append(articleDetails);
        }
        return result.toString();
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
	
	public String[] updateArticle(int sequenceNumber) throws Exception {
	    String sql = "SELECT * FROM cse360article WHERE id = " + "'" + sequenceNumber + "'";
	    String article[] = new String[9];
	    
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		while(rs.next()) { 
			String groupType = rs.getString("groupType");
			String level = rs.getString("level");
			String group = rs.getString("article_group");
			String title = rs.getString("title");
			String author = rs.getString("author");
			String abstracts = rs.getString("abstract");
			String keywords = rs.getString("keywords");
			String body = rs.getString("body");
			String references = rs.getString("references");
			
			article[0] = groupType;
			article[1] = level;
			article[2] = group;
			article[3] = title;
			article[4] = author;
			article[5] = abstracts;
			article[6] = keywords;
			article[7] = body;
			article[8] = references;			
		}
		return article;
	}

}
