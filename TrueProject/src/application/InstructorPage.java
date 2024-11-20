package application;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * The UserHomePage class creates the main user interface for the the instructor homepage.
 * The class also handles logout functionality by switching back to the login screen.
 */
public class InstructorPage {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	// Method to create and return the UI for the instructor homepage
    public Scene getScene(Stage primaryStage, String username) throws SQLException {
        Label titleLabel = new Label("ASU Help System");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        
        
		final String prefName = getPrefName(username);
        Label welcomeLabel = new Label("Welcome, " + prefName);
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
        
    	Button articleButton = new Button("Article Management");
    	articleButton.setMaxWidth(500);   
    	articleButton.setMinHeight(50);
        
    	Button addButton = new Button("Add Student");
    	addButton.setMaxWidth(500);   
    	addButton.setMinHeight(50);
    	
    	Button viewButton = new Button("View Student");
    	viewButton.setMaxWidth(500);
    	viewButton.setMinHeight(50);
    	
    	Button removeButton = new Button("Remove Student");
    	removeButton.setMaxWidth(500);
    	removeButton.setMinHeight(50);
    	
        articleButton.setOnAction(e -> {
            articleManagement articleItem = new articleManagement();
            primaryStage.setScene(articleItem.getScene(primaryStage, "Instructor", username));
        });
        
        addButton.setOnAction(e -> {
        	inviteStudent();
        });
        
        viewButton.setOnAction(e -> {
        	displayStudents(primaryStage, username);
        });
        
        removeButton.setOnAction(e -> {
        	removeStudent();
        });
 
        //commented out part for future
      /*  VBox activityList = new VBox(10);
        activityList.setPadding(new Insets(200));
        activityList.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");*/
        
        // Box layout
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.getChildren().addAll(articleButton, addButton, viewButton, removeButton);

        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(header, mainContent);
        
        logout.setOnAction(e -> {
            login loginPart = new login();
            primaryStage.setScene(loginPart.getScene(primaryStage));
        });
        
        return new Scene(contentBox, 800, 600);
    }
    //gets the preferred name for display
    private String getPrefName(String username) throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();
        String prefName = db.getPrefName(username);
        db.closeConnection();
        return prefName;
    }
    
    private static void displayStudents(Stage primaryStage, String username) {
    	try {
    		databaseHelper.connectToDatabase();  
            List<User> users = databaseHelper.getAllUsers("Student");
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
	            InstructorPage instructorPage = new InstructorPage();
	        	try {
					primaryStage.setScene(instructorPage.getScene(primaryStage, username));
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
    
    private void inviteStudent() {
  	    String password = PassReset.generateOTP();

  	    TextInputDialog emailTalk = new TextInputDialog();
  	    emailTalk.setHeaderText("Enter the email of the student you wish to invite:");

  	    Optional<String> emailOut = emailTalk.showAndWait();
  	    if (emailOut.isPresent()) {
  	        String email = emailOut.get(); 
  	            // Register the user with the provided username, generated password, and selected role
  	            try {
  	                databaseHelper.connectToDatabase();
  	                databaseHelper.register(email, password, "Student");
  	                databaseHelper.updateSpecialGroup("General Group", email);
  	                databaseHelper.closeConnection();
  	                Label successLabel = new Label("Student succesfully invited");
  	                successLabel.setTextFill(Color.GREEN);
  	            } catch (SQLException e) {
  	                e.printStackTrace();
  	            }
  	    } else {
  	    	Label successLabel = new Label("Email input was canceled");
	        successLabel.setTextFill(Color.RED);
  	    }
  	}
    
    public static void removeStudent() {
    	TextInputDialog text = new TextInputDialog();
    	text.setContentText("Please enter the student's username to remove them from the help system:");
    	
    	Optional<String> confirm = text.showAndWait();

        confirm.ifPresent(username -> {
        	// Create a confirmation alert dialog
        	Alert confirming = new Alert(AlertType.CONFIRMATION);
        	confirming.setHeaderText("Are you sure you want to remove this student?");
        	
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
        		    successAlert.setHeaderText("Student removed successfully.");
        		    successAlert.showAndWait();
        		} catch (SQLException e1) {
        			e1.printStackTrace();
        		}
        	} else {
        		Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
        		cancelAlert.setTitle("Failure");
        		cancelAlert.setHeaderText("Student remove operation was canceled.");
        		cancelAlert.showAndWait();
        	}
        });
    }
    
}