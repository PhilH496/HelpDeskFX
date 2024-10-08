package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Profile {
    public Scene getScene(Stage primaryStage) {
        
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

        
    	 
       
         VBox cb = new VBox(20);
         cb.setAlignment(Pos.CENTER);
         cb.getChildren().addAll(studentPage, adminPage, teacherPage);

         return new Scene(cb, 600, 600);
    }
}
