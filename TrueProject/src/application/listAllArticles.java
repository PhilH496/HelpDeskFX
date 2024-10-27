package application;

import java.sql.SQLException;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class listAllArticles {
    private static articleDatabaseHelper articleDHelper;

    public Scene getScene(Stage primaryStage, String userRole, String userName) {
        articleDHelper = new articleDatabaseHelper();
        //Launches articleDatabase
        try { 
            articleDHelper.connectToDatabase(); 
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
        displayArticles(articlesArea, null);

        //Search for specific article using keyword
        TextField searchField = new TextField();
        searchField.setPromptText("Enter keyword to search");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            displayArticles(articlesArea, keyword);
        });
        
        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        // Back/return button
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
    private void displayArticles(TextArea articlesArea, String keyword) {
        try {
            String articles;
            if (keyword == null || keyword.isEmpty()) {
                articles = articleDHelper.displayArticles(); // Display all if no keyword is provided
            } 
            else {
                articles = articleDHelper.searchByKeyword(keyword); // Search by keyword
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
}
