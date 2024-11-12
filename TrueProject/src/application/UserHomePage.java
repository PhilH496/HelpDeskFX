package application;

import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * The UserHomePage class creates the main user interface for the the user homepage.
 * It includes a header, placeholder sections for courses, schedule, and messages, and a 
 * placeholder for recent activity. The class also handles logout functionality by 
 * switching back to the login screen. Reusable menu items are generated through a helper method.
 */
public class UserHomePage {
	// Method to create and return the UI for the user homepage
    public Scene getScene(Stage primaryStage, String username) throws SQLException {
    	Label titleLabel = new Label("ASU Help System");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        
        
		final String prefName = getPrefName(username);
        Label welcomeLabel = new Label("Welcome, " + prefName);
        Button logout = new Button("Logout");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(titleLabel, spacer, welcomeLabel, logout);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #f0f0f0;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        
        Button helpButton = new Button("Help");
        helpButton.setMaxWidth(500);   
    	helpButton.setMinHeight(50);
    	
    	Button articleButton = new Button("Article Management");
    	articleButton.setMaxWidth(500);   
    	articleButton.setMinHeight(50);
    	
        articleButton.setOnAction(e -> {
            articleManagement articleItem = new articleManagement();
            primaryStage.setScene(articleItem.getScene(primaryStage, "Student", username));
        });
        
        // Box layout
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.getChildren().addAll(helpButton, articleButton);

        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(header, mainContent);
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
        });
        
        return new Scene(contentBox, 800, 600);
    }
    
    private String getPrefName(String username) throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();
        String prefName = db.getPrefName(username);
        db.closeConnection();
        return prefName;
    }
}