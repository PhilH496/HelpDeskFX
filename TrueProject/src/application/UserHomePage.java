package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    	
    	helpButton.setOnAction(e -> {
            sendMessage();
        });
    	
        articleButton.setOnAction(e -> {
            listAllArticles articleList= new listAllArticles();
            primaryStage.setScene(articleList.getScene(primaryStage, "Student", username));
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
    
    private void sendMessage() {
    	List<String> messageOptions = new ArrayList<>();
    	messageOptions.add("Generic");
    	messageOptions.add("Specific");

  	    ChoiceDialog<String> type = new ChoiceDialog<>("Generic", messageOptions);
  	    type.setContentText("Select a message type:");

  	    TextInputDialog messageInput = new TextInputDialog();
  	    messageInput.setHeaderText("Enter your message:");

  	    Optional<String> messageOutput = messageInput.showAndWait();
  	    if (messageOutput.isPresent()) {							// If the email was entered
  	        String message = messageOutput.get(); 
  	        
  	        Optional<String> out = type.showAndWait();	
  	        if (out.isPresent()) {							// If the role was chosen
  	            String messageType = out.get();

  	            // Change the role of the user
  	            System.out.print("Message posted: " + message + " as " + messageType);
  	        } else {
  	        	Label roleInputCanceled = new Label("Message type not selected");
  	        	roleInputCanceled.setTextFill(Color.RED);
  	        }
  	    } else {
  	    	Label usernameInputCanceled = new Label("Message input was canceled");
  	    	usernameInputCanceled.setTextFill(Color.RED);
  	    }
    }
}