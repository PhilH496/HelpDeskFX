package simpleDatabase;

import java.sql.SQLException;
import java.util.Scanner;
/*
 * Main page that creates the home page for the article management database
 * Displays the functionalities of the articles such as list, restore/backup, create, etc
 */
public class StartCSE360 {

	private static DatabaseHelper databaseHelper;
	private static final Scanner scanner = new Scanner(System.in);

	public static void main( String[] args ) throws Exception
	{
		databaseHelper = new DatabaseHelper();
		try { 
			
			databaseHelper.connectToDatabase();  // Connect to the database
				System.out.println("Welcome to article management!"); 
				startMain(); //Go straight to main where we can list our articles
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage()); //incase an error
			e.printStackTrace();
		}
		finally {
			System.out.println("Good Bye!!"); //finally ends connection
			databaseHelper.closeConnection();
		}
	}
	
	//Start of where we list the user's options for what one wants to do
	public static void startMain() throws Exception {
		System.out.println("Optons:\n1. List Articles\n2. Create Articles\n"
				+ "3. Delete Articles\n4. Backup/Restore Articles\nQ. Exit\nEnter your choice:  " );
	
		String articleNum = scanner.nextLine();
		//Switch statements to move between which option one wants to choose
		switch (articleNum) {
		case "1":
			listArticles();
			System.out.println("Successfully Listed Article!");
			break;
		case "2":
			createArticle();
			System.out.println("Successfully Created Article!");
			break;
		case "3":
			//in case of deleting an article, we need the id number
			System.out.print("Enter ID of article to delete: ");
			String idNum = scanner.nextLine();
			int idNumber = Integer.parseInt(idNum);
			databaseHelper.deleteArticle(idNumber);
			break;
		case "4":
			//An option to either choose between backing up the save file for articles or needing to restore it to previous state
			System.out.print("Enter 'Backup' or 'Restore'");
			String item = scanner.nextLine();
			databaseHelper.backedUp(item);
			System.out.println("Successfully Backedup/Restored Article!");
			break;
		case "Q":
			System.out.println("Successfully exited");
			databaseHelper.closeConnection();
			System.exit(0); //exits the database entirely
			break;
		default:
			System.out.println("Invalid choice. Please select a valid number!");
			databaseHelper.closeConnection();
		}
		startMain(); //calls main again to reset the process so you don't have to run over and over
	}
	//Calls upon the listArticles function
	private static void listArticles() throws Exception {
		databaseHelper.displayArticles();
	}
	
	//Creates multiple scanner lines to accept User input and save it for later use. Calls upon the function articleCreation to do this
	private static void createArticle() throws Exception {
		String title = "How to install IntelliJ";
		System.out.println("Title: " + title);
		//String title = scanner.nextLine();
		String author = "Nathan";
		System.out.println("Author(s): " + author);
		//String author = scanner.nextLine();
		String abstracts = "(Abstract items)";
		System.out.println("Abstract: " + abstracts);
		//String abstracts = scanner.nextLine();
		String keywords = "Install, IntelliJ";
		System.out.println("Set of keywords: " + keywords);
		//String keywords = scanner.nextLine();
		String body = "Body layout";
		System.out.println("Body: " + body);
		//String body = scanner.nextLine();
		String references = "www.IntelliJ.com";
		System.out.println("Set of references: " + references);
		//String references = scanner.nextLine();
		databaseHelper.articleCreation(title, author, abstracts, keywords, body, references);
	}
	
	
	//Code that is from the homework and not specifically used for the article management system at the moment.
	
	private static void setupAdministrator() throws Exception {
		System.out.println("Setting up the Administrator access.");
		System.out.print("Enter Admin Email: ");
		String email = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		databaseHelper.register(email, password, "admin");
		System.out.println("Administrator setup completed.");

	}

	private static void userFlow() throws Exception {
		String email = null;
		String password = null;
		System.out.println("user flow");
		System.out.print("What would you like to do 1.Register 2.Login  ");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1": 
			System.out.print("Enter User Email: ");
			email = scanner.nextLine();
			System.out.print("Enter User Password: ");
			password = scanner.nextLine(); 
			// Check if user already exists in the database
		    if (!databaseHelper.doesUserExist(email)) {
		        databaseHelper.register(email, password, "user");
		        System.out.println("User setup completed.");
		    } else {
		        System.out.println("User already exists.");
		    }
			break;
		case "2":
			System.out.print("Enter User Email: ");
			email = scanner.nextLine();
			System.out.print("Enter User Password: ");
			password = scanner.nextLine();
			if (databaseHelper.login(email, password, "user")) {
				System.out.println("User login successful.");
//				databaseHelper.displayUsers();

			} else {
				System.out.println("Invalid user credentials. Try again!!");
			}
			break;
		}
	}

	private static void adminFlow() throws Exception {
		System.out.println("admin flow");
		System.out.print("Enter Admin Email: ");
		String email = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		if (databaseHelper.login(email, password, "admin")) {
			System.out.println("Admin login successful.");
			databaseHelper.displayUsersByAdmin();

		} else {
			System.out.println("Invalid admin credentials. Try again!!");
		}
	}


}
