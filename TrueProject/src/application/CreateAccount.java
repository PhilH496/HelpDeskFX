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
        // Title
        Label createAccount = new Label("Create Your Account");
       createAccount.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");

        // Username area to fill
        Label userNameArea = new Label("Username");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Choose a username");
        newUsernameField.setMaxWidth(300);

        // Password area to fill
        Label passWordArea = new Label("Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Choose a password");
        newPasswordField.setMaxWidth(300);
        
        // Invitation code area to fill
        Label inviteLabel = new Label("Invitation Code");
        TextArea invitationArea = new TextArea();
        invitationArea.setPromptText("Invite Code");
        invitationArea.setMaxHeight(25);    // Maximum height
        invitationArea.setMinHeight(25);      // Minimum height
        invitationArea.setMaxWidth(300);

        // Back Button
        Button backButton = new Button("Finish Set Up");
        backButton.setMaxWidth(300);

        // Event: Clicking "Back to Login" switches back to the login scene
        backButton.setOnAction(e -> {
            login loginScene = new login();
            primaryStage.setScene(loginScene.getScene(primaryStage, this));  // Return to the login scene
        });

        // Setup using vbox
        VBox createAccountLayout = new VBox(10);
        createAccountLayout.setAlignment(Pos.CENTER);
        createAccountLayout.setPadding(new Insets(20));
        createAccountLayout.getChildren().addAll(createAccount, userNameArea, newUsernameField, passWordArea, newPasswordField, inviteLabel, invitationArea, backButton);

        return new Scene(createAccountLayout, 400, 400);
    }
}