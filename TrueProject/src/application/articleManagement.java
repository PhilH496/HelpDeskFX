package application;

import java.sql.SQLException;
import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class articleManagement {
	
	private static articleDatabaseHelper articleDHelper;
	private VBox contentBox;

    public Scene getScene(Stage primaryStage) {
    	articleDHelper = new articleDatabaseHelper();
    	
    	try { 
			articleDHelper.connectToDatabase(); 
			System.out.println("Welcome to article management!"); 
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage()); 
			e.printStackTrace();
		}
    	
    	// Creating the UI layout
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
        Button backButton = new Button("Back");
        backButton.setMaxWidth(500);   
        backButton.setMinHeight(50);

        // Setting up actions for each button
        listArticlesButton.setOnAction(e -> {
            listAllArticles listing = new listAllArticles();
            primaryStage.setScene(listing.getScene(primaryStage));
        });

        createArticleButton.setOnAction(e -> createArticle(primaryStage));
        
        deleteArticleButton.setOnAction(e -> deleteArticle(primaryStage));
        
        backupRestoreButton.setOnAction(e -> backupRestore(primaryStage));
        
        backButton.setOnAction(e -> {
            AdminHomePage adminPage = new AdminHomePage();
            primaryStage.setScene(adminPage.getScene(primaryStage));
        });
        
        // Add all buttons to the content box
        contentBox.getChildren().addAll(label, listArticlesButton, createArticleButton, 
        		deleteArticleButton, backupRestoreButton, backButton);

        return new Scene(contentBox, 600, 600);
    }

    
    // A simple form to create an article
    private void createArticle(Stage primaryStage) {
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

        Button submitButton = new Button("Submit");
        submitButton.setMaxWidth(250);   
        submitButton.setMinHeight(25);
        submitButton.setOnAction(e -> {
            try {
                articleDHelper.articleCreation(titleField.getText(), authorField.getText(), 
                		abstractArea.getText(), keywordsField.getText(), 
                		bodyArea.getText(), referencesArea.getText());
                primaryStage.setScene(getScene(primaryStage)); // Return to main scene after creating article
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setMaxWidth(250);   
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> primaryStage.setScene(getScene(primaryStage)));
        
        createBox.getChildren().addAll(titleField, authorField, abstractArea, 
        		keywordsField, bodyArea, referencesArea, submitButton, backButton);

        primaryStage.setScene(new Scene(createBox, 600, 600));
    }
    
    // For deleting an article
    private void deleteArticle(Stage primaryStage) {
        // Prompt for article ID
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Delete Article");
        idDialog.setHeaderText("Delete Article");
        idDialog.setContentText("Please enter the ID of the article to delete:");

        Optional<String> result = idDialog.showAndWait();

        result.ifPresent(idText -> {
            try {
                int idNumber = Integer.parseInt(idText);

                // Confirm deletion
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Are you sure you want to delete this article?");

                Optional<ButtonType> confirmation = confirmAlert.showAndWait();

                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    // Delete article if confirmed
                    articleDHelper.deleteArticle(idNumber);

                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Article deleted successfully.");
                    successAlert.showAndWait();

                    // Return to the main scene
                    primaryStage.setScene(getScene(primaryStage));
                } else {
                    // Canceled deletion message
                    Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
                    cancelAlert.setTitle("Cancellation");
                    cancelAlert.setHeaderText("Article deletion was canceled.");
                    cancelAlert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                // Handle invalid ID format
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid ID");
                errorAlert.setHeaderText("Please enter a valid ID.");
                errorAlert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    
    // For backup/restore actions
    private void backupRestore(Stage primaryStage) {
        // Prompt user for action (Backup or Restore)
        TextInputDialog actionDialog = new TextInputDialog();
        actionDialog.setTitle("Backup or Restore");
        actionDialog.setHeaderText("Select your Action");
        actionDialog.setContentText("Please enter 'Backup (filename)' or 'Restore (filename)':");

        Optional<String> result = actionDialog.showAndWait();

        result.ifPresent(action -> {
            if (action.contains("Backup") || action.contains("Restore")) {
                // Confirmation alert
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Are you sure you want to proceed with " + action + "?");

                Optional<ButtonType> confirmation = confirmAlert.showAndWait();

                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    try {
                        // Perform the backup or restore action
                        if (articleDHelper.backedUp(action) == true)
                        {

                        	// Success message
                        	Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        	successAlert.setTitle("Success");
                        	successAlert.setHeaderText(action + " has been completed successfully.");
                        	successAlert.showAndWait();
                        }
                        else
                        {
                        	// Fail message
                        	Alert failAlert = new Alert(Alert.AlertType.INFORMATION);
                        	failAlert.setTitle("Failed");
                        	failAlert.setHeaderText(action + " has failed. Unable to find specified filename.");
                        	failAlert.showAndWait();
                        }
                    

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Cancel
                    Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
                    cancelAlert.setTitle("Action Cancelled");
                    cancelAlert.setHeaderText(action + " action was canceled.");
                    cancelAlert.showAndWait();
                }
            } else {
                // Invalid action
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid Action");
                errorAlert.setHeaderText("Please enter 'Backup (filename)' or 'Restore (filename)'.");
                errorAlert.showAndWait();
            }
        });
    }

}
