package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Profile {
    public Scene getScene(Stage primaryStage) {
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
        Label Role = new Label("Role");
        Role.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-font-family: 'Roboto';");
        RadioButton adminButton = new RadioButton("Admin");
        RadioButton studentButton = new RadioButton("Student");
        RadioButton instructorButton = new RadioButton("Instructor");
        ToggleGroup roleGroup = new ToggleGroup();
        instructorButton.setToggleGroup(roleGroup);
        instructorButton.setToggleGroup(roleGroup);
        instructorButton.setToggleGroup(roleGroup);
        
    	
        
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
        		 emailAddress, emailArea, preferredName, preferArea, Role, studentButton, instructorButton, 
        		 studentPage, adminPage, teacherPage);

         return new Scene(scrolling, 800, 800);
    }
}
