package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ChooseRole {
	 public Scene getScene(Stage primaryStage, String roleKind) {
		 
		 	Label roleCall = new Label("Choose your role:");
		 	roleCall.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
		 	
	    	Button admin = new Button("Admin");
	        admin.setMaxWidth(500);   
	        admin.setMinHeight(50);

	    	Button instructor = new Button("Instructor");
	    	instructor.setMaxWidth(500);    	
	    	instructor.setMinHeight(50);
	        
	    	Button student = new Button("Student");
	    	student.setMaxWidth(500); 
	    	student.setMinHeight(50);
	        
	    	Button listUsers = new Button("List Users");
	    	listUsers.setMaxWidth(500);   
	    	listUsers.setMinHeight(50);
	    	
	        admin.setOnAction(e -> {
	            AdminHomePage adminPage = new AdminHomePage();
	            primaryStage.setScene(adminPage.getScene(primaryStage));
	        });
		 
	        instructor.setOnAction(e -> {
	            InstructorPage instructorHome = new InstructorPage();
	            primaryStage.setScene(instructorHome.getScene(primaryStage));
	        });
	    	
	        student.setOnAction(e -> {
	            UserHomePage userHome = new UserHomePage();
	            primaryStage.setScene(userHome.getScene(primaryStage));
	        });
		    
	        VBox cb = new VBox(20);
	        cb.setAlignment(Pos.TOP_CENTER);
	        cb.getChildren().addAll(roleCall);
	       if (roleKind.equals("admin"))
	       {
	    	   cb.getChildren().addAll(admin);
	       }

	        cb.getChildren().addAll(instructor, student);

	        return new Scene(cb, 600, 600);
	 }
	
}
