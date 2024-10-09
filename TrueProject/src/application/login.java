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
import java.sql.SQLException;


public class login {
    
    public Scene getScene(Stage primaryStage) {

        Label title = new Label("Welcome to Learning Platform");
        title.setFont(new Font("Roboto", 25));

        // Create user field and also align left
        Label userName = new Label("Username");
        HBox posit = new HBox(userName);
        posit.setAlignment(Pos.CENTER_LEFT);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(300);



        // Set alignment pass to left according to design
        Label password = new Label("Password");
        HBox diff = new HBox(password);
        diff.setAlignment(Pos.CENTER_LEFT);
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
        //Clicking Sign in will prompt us with a mini questionnaire about ourselves
        accButt.setOnAction(e -> {
        CreateAccount acc = new CreateAccount();
        primaryStage.setScene(acc.getScene(primaryStage));
        });
        
        signingInButt.setOnAction(e -> {
           String email = usernameField.getText();
           String passw = passwordField.getText();
           DatabaseHelper dbHelper = new DatabaseHelper();
           try {
               // Connect to the database
               dbHelper.connectToDatabase();
               
               // Perform login check
               
               if (dbHelper.login(email, passw, "admin")) {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   ChooseRole pageType = new ChooseRole();
            		   primaryStage.setScene(pageType.getScene(primaryStage));
            	   }
            	   else
            	   {
                   Profile profileSc = new Profile();
                   dbHelper.markProfileCompleted(email);
                   primaryStage.setScene(profileSc.getScene(primaryStage));
                   
            	   }
               } 
               else if (dbHelper.login(email, passw, "Student"))
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
                   primaryStage.setScene(profileSc.getScene(primaryStage));
                   
            	   }        	   
               }
               else if (dbHelper.login(email, passw, "Instructor"))
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
                   primaryStage.setScene(profileSc.getScene(primaryStage));
                   
            	   }   	   
               }
               else if (dbHelper.login(email, passw, "All"))
               {
            	   if (dbHelper.isProfileCompleted(email))
            	   {
            		   ChooseRole pageType = new ChooseRole();
            		   primaryStage.setScene(pageType.getScene(primaryStage));
            	   }
            	   else
            	   {
            		   Profile profileSc = new Profile();
            		   dbHelper.markProfileCompleted(email);
                   	   primaryStage.setScene(profileSc.getScene(primaryStage));
            		   ChooseRole pageType = new ChooseRole();
            		   primaryStage.setScene(pageType.getScene(primaryStage));
                   
            	   } 
               }
            	   
               else {
                   userOrPass.setStyle("-fx-text-fill: red;");
                   userOrPass.setText("ERROR: Username or Password is incorrect");
               }
           } catch (SQLException ex) {
               ex.printStackTrace(); // Handle any SQL errors here
           } finally {
               // Ensure the database connection is closed
               dbHelper.closeConnection();
           }
        });


        // Layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, userName, usernameField, password, passwordField, userOrPass, signingInButt, accButt);
        return new Scene(layout, 600, 600);
    }
}