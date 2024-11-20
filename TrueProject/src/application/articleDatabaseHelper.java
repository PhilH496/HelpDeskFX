package application;

import java.sql.*;
import java.util.Base64;
import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
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
	
	private EncryptionHelper encryptionHelper;
	
	public articleDatabaseHelper() throws Exception {
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
	
	/*
	 *  Used to create database table, adding necessary article items such as:
	 *  special access group, ID, level, article group, title, 
	 *  author, abstract, keywords, body, references.
	 */
	public void createTables() throws SQLException {
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

	/*
	 *  Stores articles data to the given filename. Articles can be selectively 
	 *  chosen to be backed up by their article group.
	 */
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
	
	/*
	 *  loadFromFile loads data from a file and is passed a boolean to determine how it does so. 
	 *  True will delete all existing articles and replace them with what is in the specified file.
	 *  False will instead update the current database with the data from the file 
	 *  while skipping duplicate titles.
	 */
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
	
	/*
	 *  deleteArticle will delete an article specified by it's given title 
	 *  or delete article(s) by their special access group.
	 */  
	public void deleteArticle(String title, String groupType) throws SQLException {
		String sql; 
		if (groupType.equals("None")) { // Construct sql based on given args
			sql = "DELETE FROM cse360article WHERE title = ?"; 
		} else {
			sql = "DELETE FROM cse360article WHERE groupType = ?";
		}
		    
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			if (groupType.equals("None")) {
				preparedStatement.setString(1, title); // Delete by title
			} else {
				preparedStatement.setString(1, "General Group"); // Delete by group type
			}
			int rowsDeleted = preparedStatement.executeUpdate();
			System.out.println(rowsDeleted + " rows deleted.");
		} catch (SQLException e) { // Error occured
			e.printStackTrace();
		}
	}
	
	/*
	 * viewArticle will return the article specified by the sequence number. The body and references are 
	 * initially encrypted. The passed username will be checked to see if they have the correct
	 * viewing rights, then the body and references will be displayed unencrypted.
	 */
	public String viewArticle(int sequenceNumber, String userName) throws Exception {
		String findUserQuery = "SELECT * FROM cse360article WHERE id = ?";
		StringBuilder article = new StringBuilder();
	    try (PreparedStatement stmt = connection.prepareStatement(findUserQuery)) {
            stmt.setInt(1, sequenceNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String groupType = rs.getString("groupType");
    	        String level = rs.getString("level");
    	        String group = rs.getString("article_group");
    	        String title = rs.getString("title");
    	        String author = rs.getString("author");
    	        String abstracts = rs.getString("abstract");
    	        String keywords = rs.getString("keywords");
    	        String body = rs.getString("body");
    	        String references = rs.getString("references");
    	        
    	        //This portion will encrypt and decrypt the body and reference for security
    	        String encryptedBody = Base64.getEncoder().encodeToString(
    		            encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
    		        );

    		    String encryptedReferences = Base64.getEncoder().encodeToString(
    		            encryptionHelper.encrypt(references.getBytes(), EncryptionUtils.getInitializationVector(author.toCharArray()))
    		        );

    		        String decryptedBody = new String(encryptionHelper.decrypt(
    		            Base64.getDecoder().decode(encryptedBody), 
    		            EncryptionUtils.getInitializationVector(author.toCharArray())
    		        ));
    		
    		        String decryptedReferences = new String(encryptionHelper.decrypt(
    		            Base64.getDecoder().decode(encryptedReferences), 
    		            EncryptionUtils.getInitializationVector(author.toCharArray())
    		        ));      
    	        
                DatabaseHelper databaseHelp = new DatabaseHelper();
                databaseHelp.connectToDatabase();
                String userGroupType = databaseHelp.getSpecialAccessGroup(userName);
    	        if (!groupType.equals("General Group")) // if this is a special group
    	        { // Depending if the user has access, it will only show the encrypted portion if user does not. Else, shows the decrypted portion
    	        	if ((userGroupType != null && groupType.equals(userGroupType)) || 
    	        			(userGroupType != null && databaseHelp.isGroupInViewingRights(userName, groupType)) ||
    	        			(userGroupType == null && databaseHelp.isGroupInViewingRights(userName, groupType))) //if special group name == user's group
    	        		{
    	        			article.append("Sequence Number: ").append(id).append("\n")
    	        			.append("Level: ").append(level).append("\n")
	        				.append("Group: ").append(group).append("\n")
	        				.append("Title: ").append(title).append("\n")
	        				.append("Author: ").append(author).append("\n")
	        				.append("Abstract: ").append(abstracts).append("\n")
	        				.append("Keyword(s): ").append(keywords).append("\n")
	        				.append("Body: ").append(decryptedBody).append("\n")
	        				.append("Reference(s): ").append(decryptedReferences).append("\n")
	        				.append("-------------\n");
    	        		}
    	        	else // if special group != what the user special group is
    	        	{
	        			article.append("Sequence Number: ").append(id).append("\n")
	        			.append("Level: ").append(level).append("\n")
        				.append("Group: ").append(group).append("\n")
        				.append("Title: ").append(title).append("\n")
        				.append("Author: ").append(author).append("\n")
        				.append("Abstract: ").append(abstracts).append("\n")
        				.append("Keyword(s): ").append(keywords).append("\n")
        				.append("Body: ").append(encryptedBody).append("\n")
        				.append("Reference(s): ").append(encryptedReferences).append("\n")
        				.append("-------------\n");	
    	        	}
    	        }
    	        else
    	        {//If this is a general group, no need to encrypt
    	        article.append("Sequence Number: ").append(id).append("\n")
    	        		.append("Level: ").append(level).append("\n")
    	        		.append("Group: ").append(group).append("\n")
    	                .append("Title: ").append(title).append("\n")
    	                .append("Author: ").append(author).append("\n")
    	                .append("Abstract: ").append(abstracts).append("\n")
    	                .append("Keyword(s): ").append(keywords).append("\n")
    	                .append("Body: ").append(decryptedBody).append("\n")
    	                .append("Reference(s): ").append(decryptedReferences).append("\n")
    	                .append("-------------\n");
    	        }
            }
        }
	    return article.toString();
	}
	
	/*
	 * displayArticle will display all articles in short form(without the body or references)
	 * and display it with proper formatting.
	 */
	public String displayArticles() throws Exception {
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

	        // Append article details to the StringBuilder to show everything
	        articleDetails.append("Group Type: ").append(groupType).append("\n")
	        		.append("ID: ").append(id).append("\n")
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
	    rs.close();
	    stmt.close();

	    // Return the collected articles as a string
	    return result.toString();
	}
	
	/*
	 * Checks if the database is empty or not.
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360article";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	/*
	 * Creates an article with the following arguments:
     * Special access group, level, group, title, author, abstract, keywords, body, references,
     * a flag that designates edit or article creation, and the article ID if it exists(depends on flag).
	 */
	public void articleCreation(String groupType, String level, String group, String title, String author, 
			String abstracts, String keywords, String body, String references, boolean update, int id) {
		String sql;
		if (update == true) { // edit existing article
			sql = "UPDATE cse360article SET groupType = ?, level = ?, article_group = ?, title = ?, " +
		              "author = ?, abstract = ?, keywords = ?, body = ?, references = ? " +
		              "WHERE id = " + "'" + id + "'";
		} else { 			  // create new article
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
	
	/*
	 * Helper method to facilitate the 'search' function and query articles based on keywords.
	 */
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
            //In case skilllevel unchecked, set everything
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
    
    /*
     * Simple utility method to close the article database connection after opening it.
     */
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
	
	/*
	 * Returns article data by it's sequence number where it is then fed into the article creation GUI.
	 * Method technically doesn't update article on it's own but instead helps to keep original
	 * article data persistent.
	 */
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

	/*
	 * Returns the special access group (groupType) of the user as either general or specific special group.
	 */
	public String getGroupType(int articleId) throws SQLException {
	    String groupType = null;
	    String sql = "SELECT groupType FROM cse360article WHERE id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, articleId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            groupType = rs.getString("groupType");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving groupType: " + e.getMessage());
	    }
	    return groupType;
	}
}