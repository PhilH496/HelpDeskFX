package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class PassReset {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
    public Scene getScene(Stage primaryStage) {
        // Title
    	Label titleLabel = new Label("Reset User Password");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 24));

        // Email input
    	Label alertLabel = new Label("");

        alertLabel.setFont(Font.font("Roboto", 14));
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter user email");
        emailField.setPrefWidth(400);
        
        // Send Reset Link button
        Button sendResetLinkButton = new Button("Send Reset Link");
        sendResetLinkButton.setStyle("-fx-background-color: #5D5FEF; -fx-text-fill: white;");
        sendResetLinkButton.setPrefWidth(400);
        sendResetLinkButton.setPrefHeight(40);
        
        Button returnAdmin = new Button("Return");
        returnAdmin.setStyle("-fx-background-color: #5D5FEF; -fx-text-fill: white;");

        
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(40, 40, 40, 40));
        contentBox.getChildren().addAll(titleLabel, alertLabel, emailField, sendResetLinkButton, returnAdmin);
        
        sendResetLinkButton.setOnAction(e -> {
        	String userEmail = emailField.getText();
            sendOTP(userEmail);
            alertLabel.setText("One Time Password Successfully sent");
            alertLabel.setTextFill(Color.RED);
        });
        
        sendResetLinkButton.setOnAction(e -> {
        	AdminHomePage adminPage = new AdminHomePage();
        	primaryStage.setScene(adminPage.getScene(primaryStage));
        });

        return new Scene(contentBox, 600, 400);
    }
    
    private static void sendOTP(String email) {
    	Connection connection = null;
		String resetPassword = "UPDATE cse360users SET password = ? WHERE email = ?";

    	Random rand = new Random();
        StringBuilder OneTimePassword = new StringBuilder(8);
       	
    	for (int i = 0; i <= 8; i++) { // Generate a 8 character long random OTP
    		int result = rand.nextInt(10);
    		OneTimePassword = OneTimePassword.append(result);
    	}
    	
    	try(PreparedStatement pstmt = connection.prepareStatement(resetPassword)) {
    		databaseHelper.connectToDatabase();
			pstmt.setString(1, email);
			pstmt.setString(2, OneTimePassword.toString());   
			System.out.println("User OTP: " + OneTimePassword.toString()); // "Send" to user's email
			databaseHelper.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
    }
}
