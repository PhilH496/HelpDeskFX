package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UserHomePage {
    public Scene getScene(Stage primaryStage) {
        // Header
        Label titleLabel = new Label("ASU Help System");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        
        Label welcomeLabel = new Label("Welcome, Student");
        Button logout = new Button("Logout");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(titleLabel, spacer, welcomeLabel, logout);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #f0f0f0;");

        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // My Courses
        HBox coursesBox = createMenuItemBox("My Courses", "Access your enrolled courses");
        
        // Schedule
        HBox scheduleBox = createMenuItemBox("Schedule", "View upcoming classes");
        
        // Messages
        HBox messagesBox = createMenuItemBox("Messages", "Communicate with instructors");

        // Recent Activity
        Label recentActivityLabel = new Label("Recent Activity");
        recentActivityLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 16));
        
        VBox activityList = new VBox(10);
        activityList.setPadding(new Insets(50));
        activityList.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        mainContent.getChildren().addAll(coursesBox, scheduleBox, messagesBox, recentActivityLabel, activityList);

        // Root layout
        VBox root = new VBox();
        root.getChildren().addAll(header, mainContent);
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
        });
        
        return new Scene(root, 800, 600);
    }

    private HBox createMenuItemBox(String title, String description) {
        VBox textBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: #666666;");
        textBox.getChildren().addAll(titleLabel, descLabel);
        
        // White box that the title and escription nest in 
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(textBox);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        return box;
    }
}