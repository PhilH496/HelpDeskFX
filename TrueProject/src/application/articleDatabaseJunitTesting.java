package application;
import java.sql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class articleDatabaseJunitTesting {
	private static articleDatabaseHelper articleDBHelper;

	@BeforeEach // JUnit 5
	public void setUp() throws Exception {
		articleDBHelper = new articleDatabaseHelper();
	}

	@Test
	public void testSearchReturnsShortFormOnly() throws Exception {
		// Expected output format (example values)
		String expectedOutput = """
Total Article Count: 2

Group Type: General Group
ID: 20
Level: Expert
Group: Eclipse
Title: Learn Java
Author: John Doe
Abstract: A guide to Java programming
Keyword(s): java, programming, eclipse
-------------
Group Type: General Group
ID: 21
Level: Intermediate
Group: Eclipse
Title: Learn Javafx
Author: John Doe
Abstract: A guide to Java programming
Keyword(s): javafx, programming, eclipse
-------------
					        """;

		// Connect to the test database

		// Execute
		articleDBHelper.connectToDatabase();
		String result = articleDBHelper.displayArticles();
		articleDBHelper.closeConnection();
		// Print outputs for debugging
		System.out.println("Expected Output:");
		System.out.println(expectedOutput);
		System.out.println("Actual Result:");
		System.out.println(result);
		// Assertions
		assertEquals("The result should only contain the short form fields.", expectedOutput.trim(), result.trim());
	}

	@Test
	public void testSearchFiltersBySkillLevel() throws SQLException {
	    // Inputs - change as needed
	    String keyword = "java";
	    String skillLevel = "Expert"; // Filter by skill level
	    
	    // Expected output format (example values)
	    String expectedOutput = """
Total Article Count: 1

Sequence Number: 20
Level: Expert
Group: Eclipse
Title: Learn Java
Author: John Doe
Abstract: A guide to Java programming
Keyword(s): java, programming, eclipse
-------------
	        """;

	    // Connect to the test database
	    // Execute
	    articleDBHelper.connectToDatabase();
	    String result = articleDBHelper.searchByKeywordAndLevel(keyword, skillLevel);
	    articleDBHelper.closeConnection();
	    // Print outputs for debugging
	    System.out.println("testSearchFiltersBySkillLevel Test Results:");
	    System.out.println("Expected Output:");
	    System.out.println(expectedOutput);
	    System.out.println("Actual Result:");
	    System.out.println(result);
	    // Assertions
	    assertEquals("The result should only include articles matching the skill level.", expectedOutput.trim(), result.trim());
	}

	@Test
	public void testBackupArticles() throws Exception {
		articleDBHelper.connectToDatabase();
		// Assuming a method to back up the articles
		boolean result = articleDBHelper.backUpFile("writeMe.txt", "None");
		articleDBHelper.closeConnection();
		assertTrue("Articles should have been backed up successfully.", result);
	}

	/*
	 * Uncomment to test article creation. Cannot be tested with
	 * testSearchReturnsShortFormOnly or testSearchFiltersBySkillLevel
	 */
	@Test
	public void testArticleCreation() throws SQLException {
		// Assuming there is already an article in the database with ID = 1
		int articleId = -1;

		// New data to update the article
		String groupType = "General Group";
		String level = "Advanced";
		String group = "Eclipse";
		String title = "Test Title";
		String author = "Jane Doe";
		String abstracts = "A comprehensive guide to Java programming at an advanced level.";
		String keywords = "java, programming, advanced";
		String body = "Detailed explanation of Java concepts and features.";
		String references = "Reference 1, Reference 2";

		// Call articleCreation to update the existing article
		articleDBHelper.connectToDatabase();
		articleDBHelper.articleCreation(groupType, level, group, title, author, abstracts, keywords, body, references,
				false, articleId);

		// Fetch the updated article from the database
		String sql = "SELECT * FROM cse360article WHERE title = ?";
		String titleCheck = null;
		try (PreparedStatement pstmt = articleDBHelper.getConnection().prepareStatement(sql)) {
			pstmt.setString(1, "Test Title");
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				titleCheck = rs.getString("title");
			}
		}
		articleDBHelper.closeConnection();
		// Assertions to check if the data has been updated correctly
		assertNotNull(titleCheck, "Article title should not be null after update");
		assertEquals("The article should be created.", "Test Title", titleCheck);
	}

	/*
	 * Uncomment to test delete functionality. 
	 */
	@Test
	public void testDeleteArticleByTitle() throws SQLException {
		articleDBHelper.connectToDatabase();
		articleDBHelper.deleteArticle("Test Title", "None");

		String sql = "SELECT * FROM cse360article WHERE title = ?";
		try (PreparedStatement stmt = articleDBHelper.getConnection().prepareStatement(sql)) {
			stmt.setString(1, "Test Title");
			ResultSet rs = stmt.executeQuery();
			assertFalse("Article with title 'Test Title' should be deleted.", rs.next());
		}
		articleDBHelper.closeConnection();
	}

	/*
	 * Uncomment to test edit functionality. Cannot be tested with
	 * testSearchReturnsShortFormOnly or testSearchFiltersBySkillLevel
	 */
	@Test
	public void testArticleCreationEditFunctionality() throws SQLException {
		// Assuming there is already an article in the database with ID = 1
		int articleId = 1;

		// New data to update the article
		String groupType = "General Group";
		String level = "Advanced";
		String group = "Eclipse";
		String title = "Advanced Java Programming";
		String author = "Jane Doe";
		String abstracts = "A comprehensive guide to Java programming at an advanced level.";
		String keywords = "java, programming, advanced";
		String body = "Detailed explanation of Java concepts and features.";
		String references = "Reference 1, Reference 2";

		// Call articleCreation to update the existing article
		articleDBHelper.connectToDatabase();
		articleDBHelper.articleCreation(groupType, level, group, title, author, abstracts, keywords, body, references,
				true, articleId);

		// Fetch the updated article from the database
		String sql = "SELECT * FROM cse360article WHERE id = ?";
		String updatedTitle = null;
		String updatedAuthor = null;
		String updatedLevel = null;
		try (PreparedStatement pstmt = articleDBHelper.getConnection().prepareStatement(sql)) {
			pstmt.setInt(1, articleId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				updatedTitle = rs.getString("title");
				updatedAuthor = rs.getString("author");
				updatedLevel = rs.getString("level");
			}
		}
		articleDBHelper.closeConnection();
		// Assertions to check if the data has been updated correctly
		assertNotNull(updatedTitle, "Article title should not be null after update");
		assertEquals("The article title should be updated.", "Advanced Java Programming", updatedTitle);
		assertEquals("The article author should be updated.", "Jane Doe", updatedAuthor);
		assertEquals("The article level should be updated.", "Advanced", updatedLevel);
	}
	/*
	 * Uncomment to test edit functionality. 
	 */
	@Test
	public void testLoadFromFile() throws SQLException {
	    // Ensure the connection is valid
	    articleDBHelper.connectToDatabase();

	    String testFile = "writeMe.txt";
	    boolean replaceAll = true; // Replace the existing database

	    boolean result = articleDBHelper.loadFromFile(testFile, replaceAll);

	    // Verify the method returned true, indicating success
	    assertTrue("The loadFromFile method should return true on success.", result);

	    // Validate that data from the file is now in the database
	    String sql = "SELECT * FROM cse360article";
	    try (PreparedStatement stmt = articleDBHelper.getConnection().prepareStatement(sql)) {
	        ResultSet rs = stmt.executeQuery();

	        // Ensure the ResultSet is not empty
	        assertTrue("The database should contain articles after loading from the file.", rs.next());
	    }
	    articleDBHelper.closeConnection();
	}
	/*
	 * 
	 */
}


