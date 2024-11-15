package application;

import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AccessRights {
	public Scene getScene(Stage primaryStage, String userRole, String userName) {
		
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);
        
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
        
        

        // Go back to article management
        Button backButton = new Button("Back to Article Management");
        backButton.setMaxWidth(500);
        backButton.setMinHeight(50);
        backButton.setOnAction(e -> {
            articleManagement back = new articleManagement();
            primaryStage.setScene(back.getScene(primaryStage, userRole, userName));
        });
        
        
        cb.getChildren().addAll(label, adminRights, viewingRights, listOrDeleteAdminRights, listOrDeleteViewingRights, backButton);

        return new Scene(cb, 600, 600);
	}
	
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

        // Display confiration after submit
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String group = groupField.getText();  // Group name
            String username = usernameField.getText();  // Correct username input from text field
            DatabaseHelper dbHelper = new DatabaseHelper();
         
            try {
				dbHelper.connectToDatabase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            try
            {
	            String userSpecialGroup = dbHelper.getSpecialAccessGroup(userName);
	            if (userSpecialGroup.equals(group) || dbHelper.isGroupInAdminRights(userName, group))
	            {
	            	dbHelper.updateAdminRights(username, group);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        });
        

        VBox dialogVbox = new VBox(20, header, groupField, usernameField, submitButton);
        dialogVbox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVbox, 500, 500);

        dialog.setScene(dialogScene);
        dialog.show();
    }
	
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
	        String group = groupField.getText().trim();  // Group name
	        String username = usernameField.getText();  // Correct username input from text field
	        DatabaseHelper dbHelper = new DatabaseHelper();

	        if (group.isEmpty() || username.isEmpty()) {
	            Alert alert = new Alert(AlertType.ERROR, "Group and Username must not be empty!");
	            alert.showAndWait();
	            return;
	        }

	        try {
	            dbHelper.connectToDatabase();  // Ensure the DB connection is successful
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        try {
	            String userSpecialGroup = dbHelper.getSpecialAccessGroup(username);
	            if (userSpecialGroup.equals(group) || dbHelper.isGroupInAdminRights(userName, group))
	            {
	            	dbHelper.updateViewingRights(username, group);
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
				// TODO Auto-generated catch block
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
