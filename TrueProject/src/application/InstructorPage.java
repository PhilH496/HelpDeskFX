package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * The UserHomePage class creates the main user interface for the the instructor homepage.
 * The class also handles logout functionality by switching back to the login screen.
 */
public class InstructorPage {
	// Method to create and return the UI for the instructor homepage
    public Scene getScene(Stage primaryStage) {
        Label titleLabel = new Label("ASU Help System");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        
        Label welcomeLabel = new Label("Welcome, Instructor");
        Button logout = new Button("Logout");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(titleLabel, spacer, welcomeLabel, logout);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #f0f0f0;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        
        VBox activityList = new VBox(10);
        activityList.setPadding(new Insets(200));
        activityList.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        mainContent.getChildren().addAll(activityList);

        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(header, mainContent);
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
        });
        
        return new Scene(contentBox, 800, 600);
    }
}
