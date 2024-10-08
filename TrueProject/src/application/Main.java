package application;
	
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
            primaryStage.setTitle("Learning Platform"); 
            primaryStage.setScene(loginScene.getScene(primaryStage, user));
            primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//launch application
	public static void main(String[] args) {
		launch(args);
	}
}
