package application;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class ChooseRole {
	 public Scene getScene(Stage primaryStage, String roleKind, String username) {
		 	//Page to choose your role of whether you're an admin, instructor, or student depending
		 	//on if you chose the admin role or "ALL" role
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
	    	
	    	//Functional buttons when clicked to redirect to specific home pages.
	        admin.setOnAction(e -> {
	            AdminHomePage adminPage = new AdminHomePage();
	            primaryStage.setScene(adminPage.getScene(primaryStage, username));
	        });
		 
	        instructor.setOnAction(e -> {
	            InstructorPage instructorHome = new InstructorPage();
	            try {
					primaryStage.setScene(instructorHome.getScene(primaryStage, username));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	        });
	    	
	        student.setOnAction(e -> {
	            UserHomePage userHome = new UserHomePage();
	            try {
					primaryStage.setScene(userHome.getScene(primaryStage, username));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	        });
		    
	        //Setting up layout and whether user is either admin or the ALL role to correctly display choices
	        VBox cb = new VBox(20);
	        cb.setAlignment(Pos.TOP_CENTER);
	        cb.getChildren().addAll(roleCall);
	       if (roleKind.equals("Admin"))
	       {
	    	   cb.getChildren().addAll(admin);
	       }

	        cb.getChildren().addAll(instructor, student);
	        return new Scene(cb, 600, 600);
	 }
	
}
