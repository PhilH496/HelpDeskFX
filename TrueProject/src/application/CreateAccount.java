package application;

import java.sql.SQLException;
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
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class CreateAccount {
	// Code to help setup and register a person's role of student/instructor
	public void accountSetter(String username, String gettingPass, String role) {
           DatabaseHelper db = new DatabaseHelper();
           try {
              db.connectToDatabase();
               
              db.register(username, gettingPass, role);
              System.out.println("Registering " + username + "....");
              if (role.equals("Student"))
              {
            	  db.updateSpecialGroup("General Group", username); //Automatically puts student into the General Group
              }
           } catch (SQLException ex) {
               ex.printStackTrace(); 
           } finally {
  
               db.closeConnection();
           }
	}
	
	// Checks if the database is empty. If it is, we let the user when creating account to become admin
	public boolean isAdmin() {	
        DatabaseHelper db = new DatabaseHelper();
        try {
            db.connectToDatabase();
            if (db.isDatabaseEmpty())
            {
            	return true;
            }
           
        } catch (SQLException ex) {
            ex.printStackTrace(); 
        } finally {

            db.closeConnection();
        }
        
        return false;
	}
    
    public Scene getScene(Stage primaryStage) {
    	boolean adminOrNot = isAdmin();
    	
        Label createAccount = new Label("Create Your Account");
        createAccount.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");

        // This area includes the user class for people to goto
        Label userNameArea = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setMaxWidth(300);
        
        Label passText = new Label();
        passText.setText("At least one uppercase and lowercase letter, one digit,"
        		+ "\nspecial character, and a minimum of 8 characters long");
        
        // Password area along with a confirmation area
        Label passWordArea = new Label("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Choose a password");
        passField.setMaxWidth(300);

        PasswordField passFieldTwo = new PasswordField();
        passFieldTwo.setPromptText("Confirm Password");
        passFieldTwo.setMaxWidth(300);
        
        //List of roles you can choose from
        Label Role = new Label("Role");
        Role.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-font-family: 'Roboto';");
        RadioButton studentButton = new RadioButton("Student");
        RadioButton instructorButton = new RadioButton("Instructor");
        RadioButton all = new RadioButton("All");
        RadioButton adminOption = new RadioButton("Admin");
        ToggleGroup roleGroup = new ToggleGroup();
        studentButton.setToggleGroup(roleGroup);
        instructorButton.setToggleGroup(roleGroup);
        all.setToggleGroup(roleGroup);
        adminOption.setToggleGroup(roleGroup);
        roleGroup.selectToggle(studentButton);

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
        String OTP = PassReset.generateOTP();
       
        // Back button with functionality as to what to return with
        // additionally checks if username and password are of correct status
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

        	if (inviteText.equals(OTP))
        	{
                login loginScene = new login();
                primaryStage.setScene(loginScene.getScene(primaryStage));
        	}
        	else if (!nameCorrectness.equals(""))
        	{
        		alertUser.setTextFill(Color.RED);
                alertUser.setText(nameCorrectness);
                System.out.print(alertUser.getText());
                
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
      
        //Layout for one part aside from admin and including admin
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.CENTER);
        cb.getChildren().addAll(createAccount, userNameArea, usernameField, alertUser, passText, passWordArea, passField,
        		passFieldTwo, alert, Role, studentButton, instructorButton, all);
        if (adminOrNot == true)
        {
        	cb.getChildren().addAll(adminOption);
        }
        
        cb.getChildren().addAll(inviteLabel, invitationArea, backButton);
        return new Scene(cb, 800, 800);
    }
}