package application;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class listAllArticles {
	private static articleDatabaseHelper articleDHelper;
    public Scene getScene(Stage primaryStage) {
    	articleDHelper = new articleDatabaseHelper();

    	
    	try { 
			articleDHelper.connectToDatabase(); 
			System.out.println("Welcome to article management!"); 
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage()); 
			e.printStackTrace();
		}
    	
        VBox cb = new VBox(20);
        cb.setAlignment(Pos.TOP_CENTER);

        TextArea articlesArea = new TextArea();
        articlesArea.setPrefWidth(500);  
        articlesArea.setPrefHeight(500);  
        articlesArea.setEditable(false);  // Make the text area read-only

        // Fetching articles from the database
        String articles = null;
		try {
			articles = articleDHelper.displayArticles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        articlesArea.setText(articles);

        // Back button
        Button backButton = new Button("Return");
        backButton.setMaxWidth(250);   
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> {
            articleManagement back = new articleManagement();
            primaryStage.setScene(back.getScene(primaryStage));
        });


        cb.getChildren().addAll(articlesArea, backButton);

        return new Scene(cb, 600, 600);
    }
}
