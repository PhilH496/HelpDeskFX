package application;
import javafx.geometry.Insets;
import java.util.Random;
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
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AdminHomePage {
	
	//When inviting user, which role does the user have?
	private void roleType() {
	        List<String> roles = new ArrayList<>();
	        roles.add("Student");
	        roles.add("Instructor");

	        ChoiceDialog<String> type = new ChoiceDialog<>("Student", roles);
	        type.setContentText("Select a role:");
	        Optional<String> out = type.showAndWait();
	        out.ifPresent(role -> System.out.println("Selected role: " + role));
	}
	
    public Scene getScene(Stage primaryStage) {
    	Label AdminPage = new Label("Admin Dashboard");
    	AdminPage.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
    	
    	Button inviteUser = new Button("Invite User"); // needs to give user a role when using invite code
        inviteUser.setMaxWidth(300);   
    	
    	Button passReset = new Button("Manage Password Reset");
        passReset.setMaxWidth(300);    		
        
    	Button deleteUser = new Button("Delete User");
    	deleteUser.setMaxWidth(300); 
        
    	Button listUsers = new Button("List Users");
    	listUsers.setMaxWidth(300);   
        
    	Button roleManip = new Button("Role Manipulation");
    	roleManip.setMaxWidth(300);   
        
    	Button logout = new Button("Logout");
    	logout.setMaxWidth(300);   
 
        listUsers.setOnAction(e -> {
            System.out.println("Users");
        });
        
        deleteUser.setOnAction(e -> {
            TextInputDialog text = new TextInputDialog();
            text.setContentText("Please enter the user ID to delete:");

            Optional<String> confirm = text.showAndWait();

            confirm.ifPresent(userId -> {
                Alert confirming = new Alert(AlertType.CONFIRMATION);
                confirming.setHeaderText("Are you sure you want to delete this user?");
                Optional<ButtonType> confirmation = confirming.showAndWait();
            });
        });
        
        inviteUser.setOnAction(e -> {
        	Random random = new Random();
        	int OTP = random.nextInt(100000);
        	roleType();
        	System.out.println("OTP: " + OTP);

        });
        
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage, null));
        });
       
        passReset.setOnAction(e -> {
            PassReset pass = new PassReset();
            primaryStage.setScene(pass.getScene(primaryStage));
        });
    	
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);
        cb.getChildren().addAll(AdminPage, inviteUser, passReset, deleteUser, listUsers, roleManip, logout);

        return new Scene(cb, 600, 600);
    }

}
