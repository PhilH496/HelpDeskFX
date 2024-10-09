package application;

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
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
    public Scene getScene(Stage primaryStage) {
    	Label titleLabel = new Label("Reset User Password");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 24));
        
    	Label alertLabel = new Label("");
        alertLabel.setFont(Font.font("Roboto", 14));
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter user email");
        emailField.setPrefWidth(400);
        
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
            sendOTP(userEmail, alertLabel);
        });
        
        returnAdmin.setOnAction(e -> {
        	AdminHomePage adminPage = new AdminHomePage();
        	primaryStage.setScene(adminPage.getScene(primaryStage));
        });

        return new Scene(contentBox, 600, 400);
    }
    
    static String generateOTP() {
        Random rand = new Random();
        StringBuilder otp = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            otp.append(rand.nextInt(10));
        }
        return otp.toString();
    }
    
    private static void sendOTP(String email, Label alertLabel) {
        String sql = "UPDATE cse360users SET password = ? WHERE email = ?";
        String oneTimePassword = generateOTP();

        try {
            databaseHelper.connectToDatabase();
            try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, oneTimePassword);
                pstmt.setString(2, email);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    System.out.println("User OTP: " + oneTimePassword); // "Send" to user's email
                    alertLabel.setText("One Time Password Successfully sent");
                    alertLabel.setTextFill(Color.GREEN);
                } else {
                    alertLabel.setText("No user found with email: " + email);
                    alertLabel.setTextFill(Color.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alertLabel.setText("Error Sending OTP");
            alertLabel.setTextFill(Color.RED);
        } finally {
            databaseHelper.closeConnection();
        }
    }
}
