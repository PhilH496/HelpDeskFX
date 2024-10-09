package application;

import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AdminHomePage {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();

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
	
	static String OTP() {
		Random random = new Random();
    	int OTP = random.nextInt(100000);
    	String OTPNew = String.valueOf(OTP);
    	System.out.println("OTP: " + OTPNew);
    	return OTPNew;
	}
	
    public Scene getScene(Stage primaryStage) {
    	Label AdminPage = new Label("Admin Dashboard");
    	AdminPage.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
    	
    	Button inviteUser = new Button("Invite User"); 
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
        	displayUsers(primaryStage);
        });
        
        deleteUser.setOnAction(e -> {
        	deleteUser();
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
    
    private static void deleteUser() {
    	TextInputDialog text = new TextInputDialog();
    	text.setContentText("Please enter the user ID to delete:");
    	
    	Optional<String> confirm = text.showAndWait();

        confirm.ifPresent(userId -> {
        	// Create a confirmation alert dialog
        	Alert confirming = new Alert(AlertType.CONFIRMATION);
        	confirming.setHeaderText("Are you sure you want to delete this user?");
        	
        	// Wait for the user to respond
        	Optional<ButtonType> confirmation = confirming.showAndWait();
        	    
        	// Check if OK was clicked and delete the user with the specified ID
        	if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
        		try {
        			databaseHelper.connectToDatabase();  
        			databaseHelper.deleteUser(userId);   
        			databaseHelper.closeConnection();    
        			
        			Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        		    successAlert.setTitle("Success");
        		    successAlert.setHeaderText("User deleted successfully.");
        		    successAlert.showAndWait();
        		} catch (SQLException e1) {
        			e1.printStackTrace();
        		}
        	} else {
        		Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
        		cancelAlert.setTitle("Failure");
        		cancelAlert.setHeaderText("User deletion was canceled.");
        		cancelAlert.showAndWait();
        	}
        });
    }
    
    private static void displayUsers(Stage primaryStage) {
    	try {
    		databaseHelper.connectToDatabase();  
            List<User> users = databaseHelper.getAllUsers();
			databaseHelper.closeConnection();    
			
	        TableView<User> table = new TableView<>();
	        
	        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
	        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
	        
	        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
	        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
	        
	        table.getColumns().addAll(idColumn, emailColumn);
	        table.getItems().addAll(users);
	        
	        Button backButton = new Button("Return");
	        backButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
	        backButton.setPrefWidth(400);
	        backButton.setOnAction(event -> {
	            AdminHomePage adminPage = new AdminHomePage();
	        	primaryStage.setScene(adminPage.getScene(primaryStage));
	        });

	        VBox vbox = new VBox(table, backButton);
	        Scene scene = new Scene(vbox);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("User List:");
	        primaryStage.show();
    	} catch (SQLException e1) {
			e1.printStackTrace();
		}
    }
}