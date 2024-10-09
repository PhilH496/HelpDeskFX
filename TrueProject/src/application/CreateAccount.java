package application;

import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class CreateAccount {
	
	public void accountSetter(String email, String gettingPass, String role) {
		   
           DatabaseHelper db = new DatabaseHelper();
           try {
               // Connect to the database
               db.connectToDatabase();
               
              db.register(email, gettingPass, role);
              System.out.println("Registering " + email + "....");
           } catch (SQLException ex) {
               ex.printStackTrace(); // Handle any SQL errors here
           } finally {
               // Ensure the database connection is closed
               db.closeConnection();
           }
	}
    
    public Scene getScene(Stage primaryStage) {

       Label createAccount = new Label("Create Your Account");
       createAccount.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");

        //This area includes the user class for people to goto
        Label userNameArea = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setMaxWidth(300);
        
        Label passText = new Label();
        passText.setText("At least one uppercase and lowercase letter, one digit,"
        		+ "\nspecial character, and a minimum of 8 characters long");
        
        // Password area for the 360 user to fill out
        Label passWordArea = new Label("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Choose a password");
        passField.setMaxWidth(300);

        PasswordField passFieldTwo = new PasswordField();
        passFieldTwo.setPromptText("Confirm Password");
        passFieldTwo.setMaxWidth(300);
        
        Label Role = new Label("Role");
        Role.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-font-family: 'Roboto';");
        RadioButton studentButton = new RadioButton("Student");
        RadioButton instructorButton = new RadioButton("Instructor");
        RadioButton all = new RadioButton("All");
        ToggleGroup roleGroup = new ToggleGroup();
        studentButton.setToggleGroup(roleGroup);
        instructorButton.setToggleGroup(roleGroup);
        all.setToggleGroup(roleGroup);
        
        
        
        
        
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
        
        
        
        Label alert = new Label();
        Label alertUser = new Label();
        String OTP = AdminHomePage.OTP();
       
        backButton.setOnAction(e -> {
        	alertUser.setText("");
        	alert.setText("");
        	String confirm = passField.getText();
        	String confirmTwo = passFieldTwo.getText();
        	String correctness = PasswordEvaluationTestingAutomation.performTestCase(1, confirm, true);
        	String name = usernameField.getText();
        	String nameCorrectness = UserNameRecognizer.checkForValidUserName(name);
        	String inviteText = invitationArea.getText();
        	RadioButton selectedRadioButton = (RadioButton) roleGroup.getSelectedToggle();
        	String roleType = selectedRadioButton.getText();
        	
        	//System.out.println(confirm);
        	//System.out.println(confirmTwo);
        	if (inviteText.equals(OTP))
        	{
                login loginScene = new login();
                primaryStage.setScene(loginScene.getScene(primaryStage));
        	}
        	else if (!nameCorrectness.equals(""))
        	{
        		alertUser.setTextFill(Color.RED);
                alertUser.setText(nameCorrectness);
                
        	}
        	else if (!confirm.equals(confirmTwo))
        	{
        		alert.setTextFill(Color.RED);
                alert.setText("Password does not match!");
                
        	}
        	else if (!correctness.equals(""))
        	{
        		alert.setTextFill(Color.RED);
        		alert.setText(correctness);
        	}
        	else
        	{
        		 
        		accountSetter(name, confirm, roleType);
        		login loginScene = new login();
        		primaryStage.setScene(loginScene.getScene(primaryStage));
        	}
        });
      
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.CENTER);
        cb.getChildren().addAll(createAccount, userNameArea, usernameField, alertUser, passText, passWordArea, passField,
        		passFieldTwo, alert, Role, studentButton, instructorButton, all, inviteLabel, invitationArea, backButton);

        return new Scene(cb, 800, 800);
    }
}