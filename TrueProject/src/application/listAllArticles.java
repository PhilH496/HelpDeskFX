package application;

import java.sql.SQLException;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/*
 * listAllArticles will list every article via a new page
 * This class will also create the 'search' method to find specific articles
 * and decide to either display the article or not
 */

public class listAllArticles {
    private static articleDatabaseHelper articleDBelper;
    private static int sequenceNumber = -1;
    
    public Scene getScene(Stage primaryStage, String userRole, String userName) {
        try {
			articleDBelper = new articleDatabaseHelper();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try { 
            articleDBelper.connectToDatabase(); 
            System.out.println("Welcome to article management!"); 
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage()); 
            e.printStackTrace();
        }
        Label titleLabel = new Label("Articles");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
        
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);

        TextArea articlesArea = new TextArea();
        articlesArea.setPrefWidth(500);
        articlesArea.setPrefHeight(500);
        articlesArea.setEditable(false); 

        // Display all articles when loading up
        displayArticles(articlesArea, null, null, userRole);

        //Here you can search for specific article using keyword
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, author, abstract, or keywords");
        
        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert", "All");
        levelComboBox.setPromptText("Display Skill Level");   

        Button searchButton = new Button("Search");
        Button viewButton = new Button("View Article");
        Button editButton = new Button("Edit");
        editButton.setVisible(false);
        
        HBox searchBox = new HBox(10, searchField, levelComboBox, searchButton, viewButton, editButton);
        searchBox.setAlignment(Pos.CENTER);
        
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            String skillLevel = levelComboBox.getValue();
            if (skillLevel == null || skillLevel.equals("Display Skill Level") || skillLevel.equals("All")) {
                skillLevel = null;
            }
            displayArticles(articlesArea, keyword, skillLevel, userRole);
            editButton.setVisible(false);
        });
        
        viewButton.setOnAction(e -> {
        	try {
				sequenceNumber = viewArticle(articlesArea, userName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	System.out.print(sequenceNumber);
        	DatabaseHelper dbHelper = new DatabaseHelper();
        	try {
				dbHelper.connectToDatabase();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	try {
	        	String userSpecialGroup = dbHelper.getSpecialAccessGroup(userName);
	        	if ((!userRole.equals("Student")) && ((userSpecialGroup.equals(articleDBelper.getGroupType(sequenceNumber)) ||
	        			dbHelper.isGroupInAdminRights(userName, articleDBelper.getGroupType(sequenceNumber)))))
	        	{
	        		editButton.setVisible(true);
	        	}
	        	else if (articleDBelper.getGroupType(sequenceNumber).equals("General Group") && (!userRole.equals("Student")))
	        	{
        			editButton.setVisible(true);
	        	}
	        	else
	        	{
	        		editButton.setVisible(false);
	        	}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        });
        
        editButton.setOnAction(e -> {
        	try {
				editArticle(primaryStage, userRole, userName, sequenceNumber);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        
        // Back/return button to main adminpage
        Button backButton = new Button("Return");
        backButton.setMaxWidth(250);
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> {
        	if (!userRole.equals("Student"))
        	{
        		articleManagement back = new articleManagement();
        		primaryStage.setScene(back.getScene(primaryStage, userRole, userName));
        	}
        	else {
            	UserHomePage backHome = new UserHomePage();
            	try {
					primaryStage.setScene(backHome.getScene(primaryStage, userName));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        cb.getChildren().addAll(titleLabel, searchBox, articlesArea, backButton);

        return new Scene(cb, 600, 600);
    }

	/* Method to display articles based on a keyword search. This part will call upon 
     * articleDatabaseHelper and query via keyword 
     */
    private void displayArticles(TextArea articlesArea, String keyword, String skillLevel, String userRole) {
        try {
            String articles;
            if (keyword == null || keyword.isEmpty() && (skillLevel == null || skillLevel.isEmpty())) {
                articles = articleDBelper.displayArticles(userRole); // Display all if no keyword is provided
            } 
            else {
                articles = articleDBelper.searchByKeywordAndLevel(keyword, skillLevel); // Search by keyword/level
                if (articles.isEmpty())
                {
                	articles = "No articles Found!";
                }
            }
            articlesArea.setText(articles);
        } catch (Exception e) {
            e.printStackTrace();
            articlesArea.setText("Error");
        }
    }
    
    private int viewArticle(TextArea articlesArea, String userName) throws Exception {
        TextInputDialog sequenceNumberInput = new TextInputDialog();
        sequenceNumberInput.setHeaderText("Enter the sequence number of the article you want to view: ");
        Optional<String> sequenceOut = sequenceNumberInput.showAndWait();
        
        if (sequenceOut.isPresent()) {
            int sequenceNumber = Integer.parseInt(sequenceOut.get());
            try {
                String article = articleDBelper.viewArticle(sequenceNumber, userName);
                articlesArea.setText(article);
                return sequenceNumber;
            } catch (SQLException e) {
                e.printStackTrace();
                articlesArea.setText("Error");
                return -1;
            }
        }
        return -1; // Return -1 if no input was provided
    }
    
    private void editArticle(Stage primaryStage, String userRole, String userName, int sequenceNumber) throws Exception {
    	String article[] = articleDBelper.updateArticle(sequenceNumber); 
    	
    	VBox createBox = new VBox(10);
        createBox.setAlignment(Pos.CENTER);
        
        TextField titleField = new TextField();
        titleField.setText(article[3]);
        titleField.setPromptText("Title");
        
        TextField authorField = new TextField();
        authorField.setText(article[4]);
        authorField.setPromptText("Author(s)");
        
        TextArea abstractArea = new TextArea();
        abstractArea.setText(article[5]);
        abstractArea.setPromptText("Abstract");
        
        TextField keywordsField = new TextField();
        keywordsField.setText(article[6]);
        keywordsField.setPromptText("Set of keywords");
        
        TextArea bodyArea = new TextArea();
        bodyArea.setText(article[7]);
        bodyArea.setPromptText("Body");

        TextArea referencesArea = new TextArea();
        referencesArea.setText(article[8]);
        referencesArea.setPromptText("Set of references");
        
        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setValue(article[1]);
        
        
        ComboBox<String> groupsComboBox = new ComboBox<>();
        groupsComboBox.getItems().addAll("H2 Database", "SQL Fiddle",
        		"IntelliJ", "Eclipse");
        groupsComboBox.setPromptText("Select Group");
        groupsComboBox.setValue(article[2]);
        
        ComboBox<String> groupTypeComboBox = new ComboBox<>();
        groupTypeComboBox.getItems().addAll("General Group", "Special Group");
        groupTypeComboBox.setPromptText("Select Group Type");   
        groupTypeComboBox.setValue(article[0]);
        
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
            try {
            	articleDBelper.articleCreation(groupTypeComboBox.getValue(), levelComboBox.getValue(), groupsComboBox.getValue(), titleField.getText(), 
                		authorField.getText(), abstractArea.getText(), keywordsField.getText(), 
                		bodyArea.getText(), referencesArea.getText(), true, sequenceNumber);
                primaryStage.setScene(getScene(primaryStage, userRole, userName)); // Return to main scene after creating article
            } catch (Exception ex) {
                ex.printStackTrace();
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
}