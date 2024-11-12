package application;

import java.sql.SQLException;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    public Scene getScene(Stage primaryStage, String userRole, String userName) {
        articleDBelper = new articleDatabaseHelper();
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
       
        Button viewButton = new Button ("View Article");
        
        HBox searchBox = new HBox(10, searchField, levelComboBox, searchButton, viewButton);
        searchBox.setAlignment(Pos.CENTER);
        
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            String skillLevel = levelComboBox.getValue();
            if (skillLevel == null || skillLevel.equals("Display Skill Level") || skillLevel.equals("All")) {
                skillLevel = null;
            }
            
            displayArticles(articlesArea, keyword, skillLevel, userRole);
            
        });
        
        viewButton.setOnAction(e -> {
        	viewArticle(articlesArea);
        });
        // Back/return button to main adminpage
        Button backButton = new Button("Return");
        backButton.setMaxWidth(250);
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> {
            articleManagement back = new articleManagement();
            primaryStage.setScene(back.getScene(primaryStage, userRole, userName));
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
    private void viewArticle(TextArea articlesArea) {
    	TextInputDialog sequenceNumberInput = new TextInputDialog();
    	sequenceNumberInput.setHeaderText("Enter the sequence number of the article you want to view: ");
        Optional<String> sequenceOut = sequenceNumberInput.showAndWait();
        sequenceOut.ifPresent(input -> {
        	int sequenceNumber = Integer.parseInt(input);
        	try {
        		String article = articleDBelper.viewArticle(sequenceNumber);
        		articlesArea.setText(article);
			} catch (SQLException e) {
				e.printStackTrace();
				articlesArea.setText("Error");
			}
    	});
    }
}