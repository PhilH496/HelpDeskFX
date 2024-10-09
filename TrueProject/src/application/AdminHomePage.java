package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

public class AdminHomePage {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
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
    	
    	roleManip.setOnAction(e -> {
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
        
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);
        cb.getChildren().addAll(AdminPage, inviteUser, passReset, deleteUser, listUsers, roleManip, logout);

        return new Scene(cb, 600, 600);
    }
    
    public static void deleteUser() {
    	TextInputDialog text = new TextInputDialog();
    	text.setContentText("Please enter the username to delete their account:");
    	
    	Optional<String> confirm = text.showAndWait();

        confirm.ifPresent(email -> {
        	// Create a confirmation alert dialog
        	Alert confirming = new Alert(AlertType.CONFIRMATION);
        	confirming.setHeaderText("Are you sure you want to delete this user?");
        	
        	// Wait for the user to respond
        	Optional<ButtonType> confirmation = confirming.showAndWait();
        	    
        	// Check if OK was clicked and delete the user with the specified email
        	if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
        		try {
        			databaseHelper.connectToDatabase();  
        			databaseHelper.deleteUser(email);   
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
	        
	        TableColumn<User, String> emailColumn = new TableColumn<>("Username");
	        emailColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
	        
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
    
    //When inviting user, which role does the user have?
  	private void inviteUser() {
  	    String password = PassReset.generateOTP();

  	    List<String> roles = new ArrayList<>();
  	    roles.add("Student");
  	    roles.add("Instructor");

  	    ChoiceDialog<String> type = new ChoiceDialog<>("Student", roles);
  	    type.setContentText("Select a role:");

  	    TextInputDialog emailTalk = new TextInputDialog();
  	    emailTalk.setHeaderText("Enter the username of the user you wish to invite:");

  	    Optional<String> emailOut = emailTalk.showAndWait();
  	    if (emailOut.isPresent()) {
  	        String email = emailOut.get(); 

  	        Optional<String> out = type.showAndWait();
  	        if (out.isPresent()) {
  	            String role = out.get();

  	            // Now register the user with the provided email, generated password, and selected role
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
  	
  	
  	private void changeUserRole() {
  	    List<String> roles = new ArrayList<>();
  	    roles.add("Student");
  	    roles.add("Instructor");

  	    ChoiceDialog<String> type = new ChoiceDialog<>("Student", roles);
  	    type.setContentText("Select a role:");

  	    TextInputDialog emailTalk = new TextInputDialog();
  	    emailTalk.setHeaderText("Enter the username of the user you wish to invite:");

  	    Optional<String> emailOut = emailTalk.showAndWait();
  	    if (emailOut.isPresent()) {
  	        String email = emailOut.get(); 

  	        Optional<String> out = type.showAndWait();
  	        if (out.isPresent()) {
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
  	        	Label successLabel = new Label("Role selection was canceled");
                successLabel.setTextFill(Color.RED);
  	        }
  	    } else {
  	    	Label successLabel = new Label("Username input was canceled");
	        successLabel.setTextFill(Color.RED);
  	    }
  	}
  	
  	private void updateUserRole(String role, String email) {
  		String sql = "UPDATE cse360users SET role = ? WHERE email = ?";
        //Label alertLabel = new Label("");

        try {
            databaseHelper.connectToDatabase();
            try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, role);
                pstmt.setString(2, email);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                	Label alertLabel = new Label("Role succesfully changed to " + role);
  	                alertLabel.setTextFill(Color.GREEN);
                } else {
                	Label alertLabel = new Label("Role succesfully changed to " + role);
                	alertLabel.setText("No user found with that username + " + email);
                	alertLabel.setTextFill(Color.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        	Label alertLabel = new Label("Role succesfully changed to " + role);
            alertLabel.setText("Error Changing Role");
            alertLabel.setTextFill(Color.RED);
        } finally {
            databaseHelper.closeConnection();
        }
  	}
}