package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {	
		try {
            login loginScene = new login();
            //databaseAutomatedTesting.automateTesting(); // to simulate testing, uncomment
            //articleDatabaseAutomatedTesting.runTests(); // to simulate testing, uncomment
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
            System.out.println("========== START OF ADMIN VIEW ==========");
            dbHelper.displayUsersByAdmin();
            System.out.println("========== END OF ADMIN VIEW ==========");
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
