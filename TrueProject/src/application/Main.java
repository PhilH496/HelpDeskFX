package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;



public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			BorderPane root = new BorderPane();
            login loginScene = new login();
            CreateAccount user = new CreateAccount();

            //Set automatically to be at the login page. Just setting up basics
            primaryStage.setTitle("ASU Help System"); 
            primaryStage.setScene(loginScene.getScene(primaryStage));
            primaryStage.show();
            testDatabaseHelper();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	//For debugging purposes
	public void testDatabaseHelper() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            dbHelper.connectToDatabase();

       
            if (dbHelper.isDatabaseEmpty()) {
                System.out.println("Database is empty. Registering new users...");
            } else {
                System.out.println("Database already contains users. Registering additional user...");

                
            }

            System.out.println("Displaying all users (admin view):");
            dbHelper.displayUsersByAdmin();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }
    }
	
	//launch application
	public static void main(String[] args) {
		
		launch(args);
	}
	
}
