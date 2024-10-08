package application;
import java.util.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;

public class PassReset {
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

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(40, 40, 40, 40));
        contentBox.getChildren().addAll(titleLabel, alertLabel, emailField, sendResetLinkButton);
        
        sendResetLinkButton.setOnAction(e -> {
            sendOTP();
            alertLabel.setText("One Time Password Successfully sent");
            alertLabel.setTextFill(Color.RED);
        });

        return new Scene(contentBox, 600, 400);
    }
    
    private static void sendOTP() {
    	Random rand = new Random();
        StringBuilder OneTimePassword = new StringBuilder(8);
       	
    	for (int i = 0; i <= 8; i++) { // Generate a 8 character long random OTP
    		int result = rand.nextInt(10);
    		OneTimePassword = OneTimePassword.append(result);
    	}
    	
    	System.out.println("User OTP: " + OneTimePassword.toString()); // "Send" to user's email
    }
}