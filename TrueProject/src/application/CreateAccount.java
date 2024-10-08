package application;

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
import javafx.scene.control.TextArea;

public class CreateAccount {
    
    public Scene getScene(Stage primaryStage) {

       Label createAccount = new Label("Create Your Account");
       createAccount.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");

        //This area includes the user class for people to goto
        Label userNameArea = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setMaxWidth(300);

        // Password area for the 360 user to fill out
        Label passWordArea = new Label("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Choose a password");
        passField.setMaxWidth(300);
        
        PasswordField passFieldTwo = new PasswordField();
        passFieldTwo.setPromptText("Confirm Password");
        passFieldTwo.setMaxWidth(300);
        
        // Invitation code in case someone gets a link from the admin
        Label inviteLabel = new Label("OR Invitation Code");
        TextArea invitationArea = new TextArea();
        invitationArea.setPromptText("Invite Code");
        invitationArea.setMaxHeight(25); 
        invitationArea.setMinHeight(25);  
        invitationArea.setMaxWidth(300);

        // When we finish, we get transfered back to login page
        Button backButton = new Button("Finish Set Up");
        backButton.setMaxWidth(300);

       
        backButton.setOnAction(e -> {
            login loginScene = new login();
            primaryStage.setScene(loginScene.getScene(primaryStage, this));
        });
      
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.CENTER);
        cb.getChildren().addAll(createAccount, userNameArea, usernameField, passWordArea, passField,
        		passFieldTwo, inviteLabel, invitationArea, backButton);

        return new Scene(cb, 600, 600);
    }
}