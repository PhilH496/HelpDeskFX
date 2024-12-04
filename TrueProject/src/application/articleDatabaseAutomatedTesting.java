package application;
/*
 * This class runs at main start and is meant to facilitate the testing 
 * of the methods contained within the articleDatabaseHelper.java class. 
 * A count of passed tests and failed tests is displayed after their completion.
 * Additional testcases may be added through the same process as demonstrated below.
 */
public class articleDatabaseAutomatedTesting {
    private static articleDatabaseHelper articleDBelper;

	static int numPassed = 0;
    static int numFailed = 0;

    public static void runTests() {
    	try {
			articleDBelper = new articleDatabaseHelper();
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("=====================================");
        System.out.println("Testing articleDatabaseHelper");
        System.out.println("=====================================");

        testConnectToDatabase();
        testCreateTables();
        // special access group : level : article group : title : author : abstract 
        // : keywords : body : references : edit existing/create new : id/none
        testArticleCreation("Eclipse Tutorials", "Intermediate", "Eclipse", "How to use Eclipse", "John Doe",
               "This article discusses Eclipse.", "Eclipse, IDE, Technology",
                "Here are a bunch of tips and tricks to learn when using Eclipse.", "Tech Journal, 2024", false, 0);
        testBackUpFile("backup.txt", "None"); // filename : article group/none
        testDeleteArticle("Test Title", "None"); // title : special access group/none
        testLoadFromFile("Backup1", true); // filename: true to replace db/false to update db
        testViewArticle(3, "admin"); // id : / username
        testDisplayArticles();
        testIsDatabaseEmpty();

        System.out.println("=====================================");
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
        System.out.println();
    }
    
    /* 
     * Private test method that checks for a valid connection to the article database. 
     */
    private static void testConnectToDatabase() {
        System.out.println("-------------------------------------");
        System.out.println("Test: connectToDatabase");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            System.out.println("Database connection successful.");
            articleDBelper.closeConnection();
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method that instantiates the table of the article database. 
     */
    private static void testCreateTables() {
        System.out.println("-------------------------------------");
        System.out.println("Test: createTables");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            articleDBelper.createTables();
            System.out.println("Tables created successfully.");
            articleDBelper.closeConnection();
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private method that takes in 2 inputs: the filename and an optional article group to
     * only back up articles with that specific article group.
     */
    private static void testBackUpFile(String file, String articleGroup) {
        System.out.println("-------------------------------------");
        System.out.println("Test: backUpFile");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            boolean result = articleDBelper.backUpFile(file, articleGroup);
            if (result) {
                System.out.println("Backup successful.");
                numPassed++;
            } else {
                System.out.println("***Failure*** Backup failed.");
                numFailed++;
            }
            articleDBelper.closeConnection();
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method that takes in 2 inputs: the filename and a boolean which decides 
     * if the article database gets replaced or updated with the data contained in the file.
     */
    private static void testLoadFromFile(String file, boolean replaceAll) {
        System.out.println("-------------------------------------");
        System.out.println("Test: loadFromFile");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            boolean result = articleDBelper.loadFromFile(file, replaceAll);
            if (result) {
                System.out.println("File loaded successfully.");
                numPassed++;
            } else {
                System.out.println("***Failure*** Load from file failed.");
                numFailed++;
            }
            articleDBelper.closeConnection();
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }

    /*
     * Private test method to delete an article by their title or delete article(s)
     * by their special access group.
     */
    private static void testDeleteArticle(String title, String groupType) {
        System.out.println("-------------------------------------");
        System.out.println("Test: deleteArticle");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            articleDBelper.deleteArticle(title, groupType);
            System.out.println("Article deleted successfully.");
            articleDBelper.closeConnection();
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method to view an articles content by their sequence number(id) and 
     * a username to verify the user has viewing rights.
     */
    private static void testViewArticle(int sequenceNumber, String userName) {
        System.out.println("-------------------------------------");
        System.out.println("Test: viewArticle");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            String article = articleDBelper.viewArticle(sequenceNumber, userName);
            if (article != null && !article.isEmpty()) {
                System.out.println("Article viewed successfully:\n" + article);
                numPassed++;
            } else {
                System.out.println("***Failure*** View article failed.");
                numFailed++;
            }
            articleDBelper.closeConnection();
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method to display a short overview of all articles.
     */
    private static void testDisplayArticles() {
        System.out.println("-------------------------------------");
        System.out.println("Test: displayArticles");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            String articles = articleDBelper.displayArticles();
            if (articles != null && !articles.isEmpty()) {
                System.out.println("Articles displayed successfully:\n" + articles);
                numPassed++;
            } else {
                System.out.println("***Failure*** Display articles failed.");
                numFailed++;
            }
            articleDBelper.closeConnection();
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method to test if the database is empty.
     */
    private static void testIsDatabaseEmpty() {
        System.out.println("-------------------------------------");
        System.out.println("Test: isDatabaseEmpty");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();
            boolean isEmpty = articleDBelper.isDatabaseEmpty();
            System.out.println("Database empty: " + isEmpty);
            articleDBelper.closeConnection();
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
    
    /*
     * Private test method to create an article with the following fields:
     * Special access group, level, group, title, author, abstract, keywords, body, references,
     * a flag that designates edit or article creation, and the article ID if it exists.
     */
    public static void testArticleCreation(String groupType, String level, String group, String title, String author,
            String abstracts, String keywords, String body, String references, boolean update, int id) {
        System.out.println("-------------------------------------");
        System.out.println("Test: articleCreation");
        System.out.println("-------------------------------------");
        try {
            articleDBelper.connectToDatabase();

            articleDBelper.articleCreation(groupType, level, group, title, author, abstracts, keywords, body, references,
                    update, id);

            System.out.println("Article created successfully.");
            articleDBelper.closeConnection();
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure*** Test failed.");
            e.printStackTrace();
            numFailed++;
        }
        System.out.println();
    }
}
