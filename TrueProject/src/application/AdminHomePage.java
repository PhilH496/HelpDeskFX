package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
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
/**
 * The AdminHomePage class creates the admin dashboard interface. 
 * Key features include:
 * - Inviting users to the system with assigned roles.
 * - Managing password resets.
 * - Deleting user accounts.
 * - Viewing the list of registered users.
 * - Changing user roles.
 * 
 * Each feature is linked to its respective action handler, and the class uses 
 * the DatabaseHelper to interact with the database.
 */
public class AdminHomePage {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	// Method to create and return the UI for the admin homepage
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
    	
    	Button changeUserRoleButton = new Button("Role Manipulation");
    	changeUserRoleButton.setMaxWidth(500);   
    	changeUserRoleButton.setMinHeight(50);
    	
    	Button logout = new Button("Logout");
    	logout.setMaxWidth(500);   
    	logout.setMinHeight(50);
    	
    	changeUserRoleButton.setOnAction(e -> {
    		changeUserRole();
    	});
    	
        listUsers.setOnAction(e -> {
        	displayUsers(primaryStage);
        });
        
        deleteUser.setOnAction(e -> {
        	deleteUser();
        });
        
        inviteUser.setOnAction(e -> {
        	inviteUser();
        });
       
        passReset.setOnAction(e -> {
            PassReset pass = new PassReset();
            primaryStage.setScene(pass.getScene(primaryStage));
        });
    	
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
        });
        
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.getChildren().addAll(AdminPage, inviteUser, passReset, deleteUser, listUsers, changeUserRoleButton, logout);

        return new Scene(contentBox, 600, 600);
    }
    
    // Method to handle the deletion of a user's account
    public static void deleteUser() {
    	TextInputDialog text = new TextInputDialog();
    	text.setContentText("Please enter the username to delete their account:");
    	
    	Optional<String> confirm = text.showAndWait();

        confirm.ifPresent(username -> {
        	// Create a confirmation alert dialog
        	Alert confirming = new Alert(AlertType.CONFIRMATION);
        	confirming.setHeaderText("Are you sure you want to delete this user?");
        	
        	// Wait for the user to respond
        	Optional<ButtonType> confirmation = confirming.showAndWait();
        	    
        	// Check if OK was clicked and delete the user with the specified username
        	if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
        		try {
        			databaseHelper.connectToDatabase();  
        			databaseHelper.deleteUser(username);   
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
    
    // Method to display all users in a TableView object
    private static void displayUsers(Stage primaryStage) {
    	try {
    		databaseHelper.connectToDatabase();  
            List<User> users = databaseHelper.getAllUsers();
			databaseHelper.closeConnection();    
			
	        TableView<User> table = new TableView<>();
	        
	        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
	        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));			 // Return the ID from the getID() method in User.java
	        
	        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
	        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username")); // Return the username from the getUsername() method 
	        
	        table.getColumns().addAll(idColumn, usernameColumn);
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
    
    // Method to update user role in the database
  	private void inviteUser() {
  	    String password = PassReset.generateOTP();

  	    List<String> roles = new ArrayList<>();
  	    roles.add("Student");
  	    roles.add("Instructor");

  	    ChoiceDialog<String> type = new ChoiceDialog<>("Student", roles);
  	    type.setContentText("Select a role:");

  	    TextInputDialog emailTalk = new TextInputDialog();
  	    emailTalk.setHeaderText("Enter the email of the user you wish to invite:");

  	    Optional<String> emailOut = emailTalk.showAndWait();
  	    if (emailOut.isPresent()) {
  	        String email = emailOut.get(); 

  	        Optional<String> out = type.showAndWait();
  	        if (out.isPresent()) {
  	            String role = out.get();

  	            // Register the user with the provided username, generated password, and selected role
  	            try {
  	                databaseHelper.connectToDatabase();
  	                databaseHelper.register(email, password, role);
  	                databaseHelper.closeConnection();
  	                Label successLabel = new Label("User succesfully invited");
  	                successLabel.setTextFill(Color.GREEN);
  	            } catch (SQLException e) {
  	                e.printStackTrace();
  	            }
  	        } else {
  	        	Label successLabel = new Label("Role selection was canceled");
                successLabel.setTextFill(Color.RED);
  	        }
  	    } else {
  	    	Label successLabel = new Label("Username input was canceled");
	        successLabel.setTextFill(Color.RED);
  	    }
  	}
  	
  	// Method to handle user role change process
  	private void changeUserRole() {
  	    List<String> roles = new ArrayList<>();
  	    roles.add("Student");
  	    roles.add("Instructor");

  	    ChoiceDialog<String> type = new ChoiceDialog<>("Student", roles);
  	    type.setContentText("Select a role:");

  	    TextInputDialog emailTalk = new TextInputDialog();
  	    emailTalk.setHeaderText("Enter the email of the user you wish to invite:");

  	    Optional<String> emailOut = emailTalk.showAndWait();
  	    if (emailOut.isPresent()) {							// If the email was entered
  	        String email = emailOut.get(); 
  	        
  	        Optional<String> out = type.showAndWait();	
  	        if (out.isPresent()) {							// If the role was chosen
  	            String role = out.get();

  	            // Change the role of the user
  	            try {
  	                databaseHelper.connectToDatabase();
  	                updateUserRole(role, email);
  	                databaseHelper.closeConnection();
  	                Label successLabel = new Label("User succesfully changed roles");
  	                successLabel.setTextFill(Color.GREEN);
  	            } catch (SQLException e) {
  	                e.printStackTrace();
  	            }
  	        } else {
  	        	Label roleInputCanceled = new Label("Role selection was canceled");
  	        	roleInputCanceled.setTextFill(Color.RED);
  	        }
  	    } else {
  	    	Label usernameInputCanceled = new Label("Username input was canceled");
  	    	usernameInputCanceled.setTextFill(Color.RED);
  	    }
  	}
  	
  	// Method to update user role in the database
  	private void updateUserRole(String role, String username) {
  		String sql = "UPDATE cse360users SET role = ? WHERE email = ?";

        try {
            databaseHelper.connectToDatabase();
            try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, role);
                pstmt.setString(2, username);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) { 		// Checks if theres information for that user
                	Label successLabel = new Label("Role succesfully changed to " + role);
                	successLabel.setTextFill(Color.GREEN);
                } else {
                	Label failureLabel = new Label("No user found with that username + " + username);
                	failureLabel.setTextFill(Color.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        	Label errorLabel = new Label("Error Changing Role");
        	errorLabel.setTextFill(Color.RED);
        } finally {
            databaseHelper.closeConnection();
        }
  	}
}