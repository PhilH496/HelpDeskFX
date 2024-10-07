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

public class PassReset {
    public Scene getScene(Stage primaryStage) {
    	
    Label passResetPage = new Label("pass reset page");
	
    VBox cb = new VBox(20);
    cb.setAlignment(Pos.CENTER);
    cb.getChildren().addAll(passResetPage);

    return new Scene(cb, 600, 600);
    }
}
