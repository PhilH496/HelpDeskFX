package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class login {
    
    public Scene getScene(Stage primaryStage, CreateAccount createAccountScene) {
        // Title
        Label title = new Label("Welcome to Learning Platform");
        title.setFont(new Font("Roboto", 25));

        // Area for Username
        Label userName = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(300);

        // Area for Password
        Label password = new Label("Password");

        // Create an HBox and add the label
        HBox hbox = new HBox(password);
        
        // Set alignment to left
        hbox.setAlignment(Pos.CENTER_LEFT);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(300);
        
        // Sign-in Button
        Button signInButton = new Button("Sign in");
        signInButton.setMaxWidth(300);
        signInButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");

        // Create Account Button
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setMaxWidth(300);

        // Event: Clicking "Create Account" switches to the Create Account Scene
        createAccountButton.setOnAction(e -> primaryStage.setScene(createAccountScene.getScene(primaryStage)));

        // Layout setup
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, userName, usernameField, password, passwordField, signInButton, createAccountButton);

        return new Scene(layout, 400, 400);
    }
}