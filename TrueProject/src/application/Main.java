package application;
import java.sql.SQLException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;


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
