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

public class InstructorPage {
    public Scene getScene(Stage primaryStage) {
    	
    	Label instruPage = new Label("Instructor Page!");
    	//Need to create logout button
    	
    VBox cb = new VBox(20);
    cb.setAlignment(Pos.CENTER);
    cb.getChildren().addAll(instruPage);

    return new Scene(cb, 600, 600);
    }
}