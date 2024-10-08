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
	        
	        TextInputDialog emailTalk = new TextInputDialog();
	        
	        emailTalk.setHeaderText("Email:");
	        Optional<String> emailOut = emailTalk.showAndWait();
	        emailOut.ifPresent(email -> System.out.println("Email: " + email));
	        Optional<String> out = type.showAndWait();
	        out.ifPresent(role -> System.out.println("Select your role: " + role));
	        
	}
	static String OTP()
	{
		Random random = new Random();
    	int OTP = random.nextInt(100000);
    	String OTPNew = String.valueOf(OTP);
    	System.out.println("OTP: " + OTPNew);
    	return OTPNew;
	}
	
    public Scene getScene(Stage primaryStage) {
    	Label AdminPage = new Label("Admin Dashboard");
    	AdminPage.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
    	
    	Button inviteUser = new Button("Invite User"); // needs to give user a role when using invite code
        inviteUser.setMaxWidth(500);   
        inviteUser.setMinHeight(50);

    	Button passReset = new Button("Manage Password Reset");
        passReset.setMaxWidth(500);    	
        passReset.setMinHeight(50);
        
    	Button deleteUser = new Button("Delete User");
    	deleteUser.setMaxWidth(500); 
    	deleteUser.setMinHeight(50);
        
    	Button listUsers = new Button("List Users");
    	listUsers.setMaxWidth(500);   
    	listUsers.setMinHeight(50);
        
    	Button roleManip = new Button("Role Manipulation");
    	roleManip.setMaxWidth(500);   
    	roleManip.setMinHeight(50);
        
    	Button logout = new Button("Logout");
    	logout.setMaxWidth(500);   
    	logout.setMinHeight(50);
 
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
        	roleType();
        	OTP();
        });
        
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
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
