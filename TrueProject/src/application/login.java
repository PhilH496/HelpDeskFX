package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.SQLException;


public class login {
    
    public Scene getScene(Stage primaryStage) {
    	
    	//Starting page to login
        Label title = new Label("ASU Help System");
        title.setFont(new Font("Roboto", 25));

        // Create user field 
        Label userName = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(300);



        // Set password field for code
        Label password = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(300);
        
        // Sign-in button to navigate to the profile page and log in
        Button signingInButt = new Button("Sign in");
        signingInButt.setMaxWidth(300);
        signingInButt.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");

        Button accButt = new Button("Create Account");
        accButt.setMaxWidth(300);

        Label userOrPass = new Label();
        
        //Clicking the "Create Account" switches to the Create Account Scene where we can then finish up and go back to login and login   
        accButt.setOnAction(e -> {
        CreateAccount acc = new CreateAccount();
        primaryStage.setScene(acc.getScene(primaryStage));
        });
        
        //Clicking Sign in will prompt us with a mini questionnaire about ourselves. Additional login info as well
        signingInButt.setOnAction(e -> {
           String email = usernameField.getText();
           String passw = passwordField.getText();
           DatabaseHelper dbHelper = new DatabaseHelper();
           String roleOne = "admin";
           String roleTwo = "Student";
           String roleThree = "Instructor";
           String roleFour = "All";
           try {
               dbHelper.connectToDatabase();
               
               // Perform login check and goto either profile page or straight to home page or choose specific role
               
               //if as admin
               if (dbHelper.login(email, passw, roleOne)) {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   ChooseRole pageType = new ChooseRole();
            		   primaryStage.setScene(pageType.getScene(primaryStage, roleOne));
            	   }
            	   else
            	   {
                   Profile profileSc = new Profile();
                   dbHelper.markProfileCompleted(email);
                   primaryStage.setScene(profileSc.getScene(primaryStage, roleOne));
                   
            	   }
               } // if as a student
               else if (dbHelper.login(email, passw, roleTwo))
               {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   UserHomePage userHome = new UserHomePage();
            		   primaryStage.setScene(userHome.getScene(primaryStage));
            	   }
            	   else
            	   {
                   Profile profileSc = new Profile();
                   dbHelper.markProfileCompleted(email);
                   primaryStage.setScene(profileSc.getScene(primaryStage, roleTwo));
                   
            	   }        	   
               } // if as an instructor
               else if (dbHelper.login(email, passw, roleThree))
               {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   InstructorPage instructorHome = new InstructorPage();
            		   primaryStage.setScene(instructorHome.getScene(primaryStage));
            	   }
            	   else
            	   {
                   Profile profileSc = new Profile();
                   dbHelper.markProfileCompleted(email);
                   primaryStage.setScene(profileSc.getScene(primaryStage, roleThree));
                   
            	   } 
               } // if both student + instructor
               else if (dbHelper.login(email, passw, roleFour))
               {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   ChooseRole pageType = new ChooseRole();
            		   primaryStage.setScene(pageType.getScene(primaryStage, roleFour));
            	   }
            	   else
            	   {
            		   Profile profileSc = new Profile();
            		   dbHelper.markProfileCompleted(email);
                   	   primaryStage.setScene(profileSc.getScene(primaryStage, roleFour));
                   
            	   }
               }
            	   
               else {
                   userOrPass.setStyle("-fx-text-fill: red;");
                   userOrPass.setText("ERROR: Username or Password is incorrect");
               }
           } catch (SQLException ex) {
               ex.printStackTrace();
           } finally {
             
               dbHelper.closeConnection();
           }
        });


        // Layout for how it will look
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, userName, usernameField, password, passwordField, userOrPass, signingInButt, accButt);
        return new Scene(layout, 600, 600);
    }
}