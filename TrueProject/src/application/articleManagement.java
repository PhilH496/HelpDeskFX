package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/*
 * This portion is the article management GUI page that sets up the buttons and
 * actions for what each button does and should do
 */

public class articleManagement {
	
	private static articleDatabaseHelper articleDHelper;
	private VBox contentBox;

    public Scene getScene(Stage primaryStage, String userRole, String userName) {
    	articleDHelper = new articleDatabaseHelper();
    	
    	try { 
			articleDHelper.connectToDatabase(); 
			System.out.println("Welcome to article management!"); 
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage()); 
			e.printStackTrace();
		}

    	// This parts creates the UI layout for button placement and label
        contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER);
        Label label = new Label("Article Management");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-font-family: 'Roboto';");
        Button listArticlesButton = new Button("List Articles");
        listArticlesButton.setMaxWidth(500);   
        listArticlesButton.setMinHeight(50);
        Button createArticleButton = new Button("Create Article");
        createArticleButton.setMaxWidth(500);   
        createArticleButton.setMinHeight(50);        
        Button deleteArticleButton = new Button("Delete Article");
        deleteArticleButton.setMaxWidth(500);   
        deleteArticleButton.setMinHeight(50);
        Button backupRestoreButton = new Button("Backup/Restore Articles");
        backupRestoreButton.setMaxWidth(500);   
        backupRestoreButton.setMinHeight(50);
        Button accessRights = new Button("Manage Access Rights");
        accessRights.setMaxWidth(500);   
        accessRights.setMinHeight(50);
        
        Button backButton = new Button("Back");
        backButton.setMaxWidth(500);   
        backButton.setMinHeight(50);

        // Setting up actions for each button
        listArticlesButton.setOnAction(e -> {
            listAllArticles listing = new listAllArticles();
            primaryStage.setScene(listing.getScene(primaryStage, userRole, userName));
        });

        createArticleButton.setOnAction(e -> createArticle(primaryStage, userRole, userName));
        
        deleteArticleButton.setOnAction(e -> deleteArticle(primaryStage, userRole, userName));
        
        backupRestoreButton.setOnAction(e -> backupRestore(primaryStage));
        
        accessRights.setOnAction(e -> {
                AccessRights accessRightPage = new AccessRights();
                primaryStage.setScene(accessRightPage.getScene(primaryStage, userRole, userName));
        });
        
        
        backButton.setOnAction(e -> {
            if (userRole.equals("Instructor")) {
                InstructorPage instructorPage = new InstructorPage();
                try {
					primaryStage.setScene(instructorPage.getScene(primaryStage, userName));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            } else {
                AdminHomePage adminPage = new AdminHomePage();
                primaryStage.setScene(adminPage.getScene(primaryStage, userName));
            }
        });
        
        // Add all buttons here to display on the page
        contentBox.getChildren().addAll(label, listArticlesButton, createArticleButton, 
        		deleteArticleButton, backupRestoreButton, accessRights, backButton);

        return new Scene(contentBox, 600, 600);
    }
    
    // Create article will make a form that creates textfields and save its data
    // to the article database. Additionally, additional buttons are placed to
    // select skill level and groups
    private void createArticle(Stage primaryStage, String userRole, String userName) {
    	VBox createBox = new VBox(10);
        createBox.setAlignment(Pos.CENTER);
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Author(s)");
        
        TextArea abstractArea = new TextArea();
        abstractArea.setPromptText("Abstract");
        
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Set of keywords");
        
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Body");

        TextArea referencesArea = new TextArea();
        referencesArea.setPromptText("Set of references");
        
        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setPromptText("Select Skill Level");      
        
        ComboBox<String> groupsComboBox = new ComboBox<>();
        groupsComboBox.getItems().addAll("H2 Database", "SQL Fiddle",
        		"IntelliJ", "Eclipse");
        groupsComboBox.setPromptText("Select Group");   
        
        ComboBox<String> groupTypeComboBox = new ComboBox<>();
        
        groupTypeComboBox.getItems().addAll("General Group");
        if (userRole.equals("Admin"))
        {
            DatabaseHelper dbHelper = new DatabaseHelper();
            try {
                dbHelper.connectToDatabase();  // Connect once
                // Check if admin has a special group assigned
                if (dbHelper.getSpecialAccessGroup(userName) != null) {
                    groupTypeComboBox.getItems().add("Special Group");
                }
            } catch (SQLException e) {
                e.printStackTrace();  
            } finally {
                dbHelper.closeConnection();  // Ensure the connection is closed after use
            }
        }
        else
        {
        	groupTypeComboBox.getItems().add("Special Group");
        }
        
        groupTypeComboBox.setPromptText("Select Group Type");   
        
        groupTypeComboBox.setOnAction(event -> {
        	String selectedGroup = groupTypeComboBox.getValue();
            if ("Special Group".equals(selectedGroup)) {
                // Show input dialog to enter the name for "Special Group"
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Special Group Name");
                dialog.setHeaderText("Enter a name for the Special Group:");
                dialog.setContentText("Group Name:");

                // Get the entered name
                dialog.showAndWait().ifPresent(name -> {
                    if (!name.isEmpty()) {
                        // Optionally add the new name to the ComboBox or perform other actions
                        groupTypeComboBox.getItems().add(name);
                        groupTypeComboBox.setValue(name); // Select the new group name
                    } else {
                        // Alert if no name was entered
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a valid group name.", ButtonType.OK);
                        alert.showAndWait();
                        groupTypeComboBox.setValue(null); // Reset selection
                    }
                });
            }
           
        });
        
        HBox horizontalLayoutForButtons = new HBox(5, levelComboBox, groupsComboBox, groupTypeComboBox);
        horizontalLayoutForButtons.setAlignment(Pos.CENTER);
        
        Button submitButton = new Button("Submit");
        submitButton.setMaxWidth(250);   
        submitButton.setMinHeight(25);
        submitButton.setOnAction(e -> {
        	if (!groupTypeComboBox.getValue().equals("General Group"))
        	{
            DatabaseHelper dbHelper = new DatabaseHelper();
            try {
				dbHelper.connectToDatabase();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            	try { //if user is already in a group, they can't make a new group. Only articles in same group.
					if (dbHelper.getSpecialAccessGroup(userName) != null &&
				            !dbHelper.getSpecialAccessGroup(userName).equals(groupTypeComboBox.getValue()))
					{
						
	                    Alert alreadyIn = new Alert(Alert.AlertType.INFORMATION);
	                    alreadyIn.setTitle("Failure");
	                    alreadyIn.setHeaderText("Error: User already in special group! Cannot make new article in new special group!");
	                    alreadyIn.showAndWait();
					}
					else
					{
						dbHelper.updateSpecialGroup(groupTypeComboBox.getValue(), userName);
			           	articleDHelper.articleCreation(groupTypeComboBox.getValue(), levelComboBox.getValue(), groupsComboBox.getValue(), titleField.getText(), 
		                		authorField.getText(), abstractArea.getText(), keywordsField.getText(), 
		                		bodyArea.getText(), referencesArea.getText(), false, -1);
			           	
		                // If it's a "Special Group", update adminRights to the special group name
		                dbHelper.updateAdminRights(userName, groupTypeComboBox.getValue());
		                dbHelper.updateViewingRights(userName, groupTypeComboBox.getValue());
		                primaryStage.setScene(getScene(primaryStage, userRole, userName)); // Return to main scene after creating article
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	else
        	{
	           	articleDHelper.articleCreation(groupTypeComboBox.getValue(), levelComboBox.getValue(), groupsComboBox.getValue(), titleField.getText(), 
                		authorField.getText(), abstractArea.getText(), keywordsField.getText(), 
                		bodyArea.getText(), referencesArea.getText(), false, -1);
                primaryStage.setScene(getScene(primaryStage, userRole, userName));        		
        	}
        	
        });
        Button backButton = new Button("Back");
        backButton.setMaxWidth(250);   
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> primaryStage.setScene(getScene(primaryStage, userRole, userName)));
        
        createBox.getChildren().addAll(titleField, authorField, abstractArea, 
        		keywordsField, bodyArea, referencesArea, horizontalLayoutForButtons, submitButton, backButton);
   
        primaryStage.setScene(new Scene(createBox, 600, 600));
    }
    
    // This part deletes the article and sends a confirmation on if the user really
    // wants to delete the article. It will also confirm if the article title exists
    private void deleteArticle(Stage primaryStage, String userRole, String userName) {
    	TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Delete Article");
        titleDialog.setHeaderText("Delete Article");
        titleDialog.setContentText("Please enter the title of the article to delete: ");

        Optional<String> result = titleDialog.showAndWait();

        result.ifPresent(titleText -> {
            try {
                String title = result.get();

                // Confirm deletion
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Are you sure you want to delete this article?");

                Optional<ButtonType> confirmation = confirmAlert.showAndWait();

                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    // Delete article if confirmed
                    articleDHelper.deleteArticle(title);

                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Article deleted successfully.");
                    successAlert.showAndWait();

                    // Return to the main scene
                    primaryStage.setScene(getScene(primaryStage, userRole, userName));
                } else {
                    // Canceled deletion message
                    Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
                    cancelAlert.setTitle("Cancellation");
                    cancelAlert.setHeaderText("Article deletion was canceled.");
                    cancelAlert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                // Handle invalid title format
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid Title");
                errorAlert.setHeaderText("Please enter a valid title.");
                errorAlert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    // Private method that handles the main backup/restore actions based on user input.
    // It prompts the user to specify either 'Backup' or 'Restore'.
    // The method calls private methods, handleBackup() or handleRestore(), as appropriate.
    private void backupRestore(Stage primaryStage) {
        TextInputDialog actionDialog = new TextInputDialog();
        actionDialog.setTitle("Backup or Restore");
        actionDialog.setHeaderText("Select Action");
        actionDialog.setContentText("Enter 'Backup' or 'Restore':");

        Optional<String> actionResult = actionDialog.showAndWait();

        actionResult.ifPresent(action -> {
            if (action.equalsIgnoreCase("Backup")) {
                handleBackup();
            } else if (action.equalsIgnoreCase("Restore")) {
                handleRestore();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Action");
                alert.setHeaderText("Please enter 'Backup' or 'Restore'.");
                alert.showAndWait();
            }
        });
    }
    
	 // Private method performs the backup action.
	 // Prompts the user to select a group (e.g., "H2 Database", "SQL Fiddle") and specify a backup filename.
    private void handleBackup() {
        // Prompt for group (optional) and filename
        List<String> groups = new ArrayList<>();
  	    groups.add("H2 Database");
  	    groups.add("SQL Fiddle");
  	    groups.add("Eclipse");
  	    groups.add("IntelliJ");
  	    groups.add("None");

  	    ChoiceDialog<String> group = new ChoiceDialog<>("None", groups);
  	    group.setContentText("Select a group: ");
  	    Optional<String> outGroup = group.showAndWait();
  	    String userGroup = outGroup.get();
  	    
        TextInputDialog fileDialog = new TextInputDialog();
        fileDialog.setTitle("Backup Filename");
        fileDialog.setHeaderText("Specify Backup Filename: ");
        Optional<String> fileResult = fileDialog.showAndWait();
        
        fileResult.ifPresent(file -> {
        	try {
				if(articleDHelper.backUpFile(file, userGroup)) {
					Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Articles backed up successfully.");
                    successAlert.showAndWait();
				} else {
					Alert failureAlert = new Alert(Alert.AlertType.INFORMATION);
					failureAlert.setTitle("Failure");
					failureAlert.setHeaderText("Articles were not saved to file.");
					failureAlert.showAndWait();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
    }
    
	 // Private method that performs the restore action.
	 // Prompts the user to enter a filename for restoring data, then asks the user to choose 
     // between replacing the entire database or updating existing entries.
    private void handleRestore() {
        TextInputDialog fileDialog = new TextInputDialog();
        fileDialog.setTitle("Restore Filename");
        fileDialog.setHeaderText("Specify Restore Filename:");
        Optional<String> fileResult = fileDialog.showAndWait();

        fileResult.ifPresent(file -> {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Restore Options");
            confirmDialog.setHeaderText("Choose Restore Method");
            confirmDialog.setContentText("Would you like to replace the entire database or update existing entries?");

            ButtonType replaceButton = new ButtonType("Replace All");
            ButtonType updateButton = new ButtonType("Update Existing");
            ButtonType cancelButton = new ButtonType("Cancel");

            confirmDialog.getButtonTypes().setAll(replaceButton, updateButton, cancelButton);
            Optional<ButtonType> result = confirmDialog.showAndWait();
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            Alert failureAlert = new Alert(Alert.AlertType.INFORMATION);
            if (result.isPresent()) {
                if (result.get() == replaceButton) {  // true for complete replacement
                    if(articleDHelper.loadFromFile(file, true)) {
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Articles were succesfully replaced from back up.");
                        successAlert.showAndWait();
    				} else {
    					failureAlert.setTitle("Failure");
    					failureAlert.setHeaderText("Unexepcted error occured when restoring files");
    					failureAlert.showAndWait();
    				}
                } else if (result.get() == updateButton) {
                    if(articleDHelper.loadFromFile(file, false)) { // false for update mode
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Articles updated successfully.");
                        successAlert.showAndWait();
    				} else {
    					failureAlert.setTitle("Failure");
    					failureAlert.setHeaderText("Unexpected error occured when restoring files.");
    					failureAlert.showAndWait();
    				}
                }
            }
        });
    }
}