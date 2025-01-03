package application;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
/*
 * This class checks if a user has access rights to specific groups. Additionally,
 * this class will allow users to add/remove users to have admin/viewing rights. It
 * will also list users under groups as well.
 */
public class AccessRights {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	public Scene getScene(Stage primaryStage, String userRole, String userName) {
		
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);
        
        //This area sets up the buttons and style of the page first
        Label label = new Label("Access Rights Management");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
        
        Button adminRights = new Button("Add User to Admin Rights");
        adminRights.setMaxWidth(500);   
        adminRights.setMinHeight(50);
        adminRights.setOnAction(e -> adminRightsPopup(userName));
        
        Button viewingRights = new Button("Add User to Viewing Rights");
        viewingRights.setMaxWidth(500);   
        viewingRights.setMinHeight(50);
        viewingRights.setOnAction(e -> viewingRightsPopup(userName));
        
        Button listOrDeleteAdminRights = new Button("List/Delete Users With Admins Rights");
        listOrDeleteAdminRights.setMaxWidth(500);   
        listOrDeleteAdminRights.setMinHeight(50);
        listOrDeleteAdminRights.setOnAction(e -> displayUsers(primaryStage, userName, userRole, "admin"));
        
        Button listOrDeleteViewingRights = new Button("List/Delete Users With Viewing Rights");
        listOrDeleteViewingRights.setMaxWidth(500);   
        listOrDeleteViewingRights.setMinHeight(50);
        listOrDeleteViewingRights.setOnAction(e -> displayUsers(primaryStage, userName, userRole, "view"));
        
        Button deleteAccess = new Button("Remove user from General/Special Access group");
        deleteAccess.setMaxWidth(500);   
        deleteAccess.setMinHeight(50);
        deleteAccess.setOnAction(e -> {
			try {
				deleteAccess();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        if (!userRole.equals("Admin")) {
        	deleteAccess.setVisible(false);
        }
        

        // Go back to article management page
        Button backButton = new Button("Back to Article Management");
        backButton.setMaxWidth(500);
        backButton.setMinHeight(50);
        backButton.setOnAction(e -> {
            articleManagement back = new articleManagement();
            primaryStage.setScene(back.getScene(primaryStage, userRole, userName));
        });
        
        cb.getChildren().addAll(label, adminRights, viewingRights, listOrDeleteAdminRights, listOrDeleteViewingRights, deleteAccess, backButton);

        return new Scene(cb, 600, 600);
	}
	
	/*
	 * Private method that deletes the special access group of a user,
	 * revoking their viewing/admin rights of their owned group while retaining other permissions.
	 */
	private void deleteAccess() throws SQLException {
		// Dialog to prompt the user to enter the ID of the user wish to send to the help system
  	   	TextInputDialog idInputDialog = new TextInputDialog();
  	   	idInputDialog.setHeaderText("Enter the ID of the user you wish to remove access from:");
  	        
  	   	Optional<String> idInputDialogResult = idInputDialog.showAndWait();
  	   	if (idInputDialogResult.isPresent()) {
  	   		int id = Integer.parseInt(idInputDialogResult.get());
  	   		databaseHelper.connectToDatabase();
  	   		databaseHelper.deleteSpecialAccess(id);
  	   		databaseHelper.closeConnection();
  	   	} else {
  	   		Label messageInputCanceled = new Label("Message type not selected");
  	   		messageInputCanceled.setTextFill(Color.RED);
  	   	}
	}

	//Displays all users in a specific group
	private static void displayUsers(Stage primaryStage, String name, String userRole, String viewOrAdmin) {
	    try {
	        DatabaseHelper databaseHelper = new DatabaseHelper();
	        databaseHelper.connectToDatabase();

	        // Initial user list
	        List<String> users = databaseHelper.getUsernamesByViewingRightsGroup("None");
	        databaseHelper.closeConnection();

	        TableView<String> table = new TableView<>();

	        // Set up columns for displaying data
	        TableColumn<String, String> usernameColumn = new TableColumn<>("Username");
	        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

	        table.getColumns().add(usernameColumn);
	        table.getItems().addAll(users);

	        // Search bar for filtering by group
	        TextField searchField = new TextField();
	        searchField.setPromptText("Enter group name to filter");

	        Button searchButton = new Button("Search");
	        searchButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

	        // Search button action to filter users based on group in viewingRights or adminRights
	        searchButton.setOnAction(event -> {
	            String group = searchField.getText().trim();
	            if (!group.isEmpty()) {
	                try {
	                    databaseHelper.connectToDatabase();
	                    List<String> filteredUsers = viewOrAdmin.equals("view")
	                            ? databaseHelper.getUsernamesByViewingRightsGroup(group)
	                            : databaseHelper.getUsernamesByAdminRightsGroup(group);
	                    databaseHelper.closeConnection();

	                    // Refresh the table with the filtered users
	                    table.getItems().clear();
	                    table.getItems().addAll(filteredUsers);
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        });

	        // Button to delete rights based on viewOrAdmin parameter
	        Button deleteButton = new Button("Delete Rights");
	        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
	        deleteButton.setOnAction(event -> {
	            String selectedUser = table.getSelectionModel().getSelectedItem();
	            if (selectedUser != null) {
	                try {
	                	String group = searchField.getText().trim();
	                    databaseHelper.connectToDatabase();
	                    String userSpecialGroup = databaseHelper.getSpecialAccessGroup(name);
	                    if (userSpecialGroup.equals(group) || databaseHelper.isGroupInAdminRights(name, group))
	                    {
		                    if (viewOrAdmin.equals("view")) {
		                        databaseHelper.deleteViewingRights(selectedUser, group);
		                    } else {
		                        databaseHelper.deleteAdminRights(selectedUser, group);
		                    }
		                    databaseHelper.closeConnection();
	
		                    // Remove the user from the table after deletion
		                    table.getItems().remove(selectedUser);
	                    }
	                    else
	                    {
	                        Alert alert = new Alert(AlertType.ERROR);
	                        alert.setTitle("Access Denied");
	                        alert.setHeaderText(null);
	                        alert.setContentText("Error! You do not have the right!");
	                        alert.showAndWait();
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        });

	        // Return button to go back to the admin page
	        Button backButton = new Button("Return");
	        backButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
	        backButton.setPrefWidth(400);
	        backButton.setOnAction(event -> {
	            articleManagement articleBack = new articleManagement();
	            primaryStage.setScene(articleBack.getScene(primaryStage, userRole, name));
	        });

	        // Layout for search bar, table, delete button, and back button
	        HBox searchBox = new HBox(10, searchField, searchButton); // Holds search bar and button
	        VBox vbox = new VBox(10, searchBox, table, deleteButton, backButton);

	        Scene scene = new Scene(vbox);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("User List:");
	        primaryStage.show();
	    } catch (SQLException e1) {
	        e1.printStackTrace();
	    }
	}
	
	//Popup menu in case adding user to have admin rights
	private void adminRightsPopup(String userName) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Admin Rights");

        Label header = new Label("Admin Rights Management");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
 
        TextField groupField = new TextField();
        groupField.setPromptText("Enter Your Special Group");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

        // Confirmation
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String group = groupField.getText();  // Group name
            String username = usernameField.getText();  // username input
            DatabaseHelper databaseHelper = new DatabaseHelper();
         
            try {
				databaseHelper.connectToDatabase();
			} catch (SQLException e) {
				e.printStackTrace();
			}

            try
            {
	            String userSpecialGroup = databaseHelper.getSpecialAccessGroup(userName); // grabs special group
	            if (userSpecialGroup.equals(group) || databaseHelper.isGroupInAdminRights(userName, group)) //compares if user group has the rights
	            {
	            	databaseHelper.updateAdminRights(username, group);
	                // Display confirmation alert with correct group and username
	                Alert confirmationAlert = new Alert(AlertType.INFORMATION, 
	                    "Group: " + group + "\nUsername: " + username);
	                confirmationAlert.setHeaderText("Success! " + username + " now has admin rights over " + group);
	                confirmationAlert.showAndWait();
	                dialog.close();  // Close the dialog after successful submission
	            }
	            else
	            {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Access Denied");
	                alert.setHeaderText(null);
	                alert.setContentText("Error! You do not have the right!");
	                alert.showAndWait();
	            }
            }  catch (SQLException e) {
				e.printStackTrace();
			}
            
        });
        

        VBox dialogVbox = new VBox(20, header, groupField, usernameField, submitButton);
        dialogVbox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVbox, 500, 500);

        dialog.setScene(dialogScene);
        dialog.show();
    }
	
	//Creates popup menu to add people to viewing rights
	private void viewingRightsPopup(String userName) {
	    Stage dialog = new Stage();
	    dialog.initModality(Modality.APPLICATION_MODAL);
	    dialog.setTitle("Viewing Rights");

	    Label header = new Label("Viewing Rights Management");
	    header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

	    TextField groupField = new TextField();
	    groupField.setPromptText("Enter Your Special Group");

	    TextField usernameField = new TextField();
	    usernameField.setPromptText("Enter Username");

	    // Display confirmation after submit
	    Button submitButton = new Button("Submit");
	    submitButton.setOnAction(event -> {
	        String group = groupField.getText().trim(); 
	        String username = usernameField.getText();  

	        if (group.isEmpty() || username.isEmpty()) {
	            Alert alert = new Alert(AlertType.ERROR, "Group and Username must not be empty!");
	            alert.showAndWait();
	            return;
	        }

	        try {
	            databaseHelper.connectToDatabase();  
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        try {
	            String userSpecialGroup = databaseHelper.getSpecialAccessGroup(username);
	            if (userSpecialGroup.equals(group) || databaseHelper.isGroupInAdminRights(userName, group)) //Checks if user has access to add person to viewing rights
	            {
	            	databaseHelper.updateViewingRights(username, group);
	    	        // Display confirmation alert with correct group and username
	    	        Alert confirmationAlert = new Alert(AlertType.INFORMATION,
	    	            "Group: " + group + "\nUsername: " + username);
	    	        confirmationAlert.setHeaderText("Success! " + username + " now has viewing rights over " + group);
	    	        confirmationAlert.showAndWait();
	    	        dialog.close();  // Close the dialog after successful submission
	            }
	            else
	            {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Access Denied");
	                alert.setHeaderText(null);
	                alert.setContentText("Error! You do not have the right!");
	                alert.showAndWait();
	            }
	        }  catch (SQLException e) {
				e.printStackTrace();
			}
	        
	    });

	    VBox dialogVbox = new VBox(20, header, groupField, usernameField, submitButton);
	    dialogVbox.setAlignment(Pos.CENTER);
	    Scene dialogScene = new Scene(dialogVbox, 500, 500);

	    dialog.setScene(dialogScene);
	    dialog.show();
	}
}