package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Profile {
	
    public Scene getScene(Stage primaryStage, String roleKind) {
        VBox cb = new VBox(40);
        cb.setPadding(new Insets(40));
        cb.setAlignment(Pos.TOP_LEFT);
    	Label profileFinish = new Label("Complete Your Profile");
    	profileFinish.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");
        
        Label FirstName = new Label("First Name");
        TextField firstArea = new TextField();
        firstArea.setMinHeight(20); 
        firstArea.setMaxWidth(300);  
        
        Label MiddleName = new Label("Middle Name");
        TextField middleArea = new TextField();
        middleArea.setMinHeight(20); 
        middleArea.setMaxWidth(300); 
        
        Label LastName = new Label("Last Name");
        TextField lastArea = new TextField();
        lastArea.setMinHeight(20); 
        lastArea.setMaxWidth(300);   
        
        Label emailAddress = new Label("Email Address");
        TextField emailArea = new TextField();
        emailArea.setMinHeight(20); 
        emailArea.setMaxWidth(300);    
        
        Label preferredName = new Label("Preferred Name (Optional)");
        TextField preferArea = new TextField();
        preferArea.setMinHeight(20); 
        preferArea.setMaxWidth(300);        
        
        
        Button studentPage = new Button("Finish Setting Up Profile (Student)");
        studentPage.setMaxWidth(300);

        
        studentPage.setOnAction(e -> {
            UserHomePage userHome = new UserHomePage();
            primaryStage.setScene(userHome.getScene(primaryStage));
        });
        
        Button adminPage = new Button("Finish Setting Up Profile (Admin)");
        adminPage.setMaxWidth(300);
        
        adminPage.setOnAction(e -> {
             AdminHomePage adminHome = new AdminHomePage();
            primaryStage.setScene(adminHome.getScene(primaryStage));
        });
        
        Button teacherPage = new Button("Finish Setting Up Profile (Instructor)");
        teacherPage.setMaxWidth(300);

        
        teacherPage.setOnAction(e -> {
            InstructorPage instru = new InstructorPage();
            primaryStage.setScene(instru.getScene(primaryStage));
        });

        ScrollPane scrolling = new ScrollPane(cb);
        scrolling.setFitToWidth(true); // Ensure scroll pane resizes with the window  
        
        cb.getChildren().addAll(profileFinish, FirstName, firstArea, MiddleName, middleArea, LastName, lastArea,
       		 emailAddress, emailArea, preferredName, preferArea);		
        		System.out.println(roleKind);
   	 			if ("admin".equals(roleKind)) {
   	 				cb.getChildren().addAll(adminPage, studentPage, teacherPage); 
   	 			} 
   	 			else if ("All".equals(roleKind)) {
   	 				cb.getChildren().addAll(studentPage, teacherPage);  
   	 			}
   	 			else if ("Student".equals(roleKind))
   	 			{
   	 				cb.getChildren().addAll(studentPage);
   	 			}
   	 			else
   	 			{
   	 				cb.getChildren().addAll(teacherPage);
   	 			}    


         return new Scene(scrolling, 800, 800);
    }
    
}
