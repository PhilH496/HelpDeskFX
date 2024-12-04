package application;

import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Profile {

	public Scene getScene(Stage primaryStage, String roleKind, String username) {
		/*
		 * layout set up to begin where pieces should go. Personal information included
		 * as well such as first, last, middle, email, and preferred name. Buttons are
		 * also created to goto the next page
		 */
		VBox cb = new VBox(40);
		cb.setPadding(new Insets(40));
		cb.setAlignment(Pos.TOP_LEFT);
		Label profileFinish = new Label("Complete Your Profile");
		profileFinish.setStyle("-fx-font-weight: bold; -fx-font-size: 25px; -fx-font-family: 'Roboto';");

		Label FirstName = new Label("First Name");
		TextField firstArea = new TextField();
		firstArea.setMinHeight(20);
		firstArea.setMaxWidth(300);

		Label MiddleName = new Label("Middle Name");
		TextField middleArea = new TextField();
		middleArea.setMinHeight(20);
		middleArea.setMaxWidth(300);

		Label LastName = new Label("Last Name");
		TextField lastArea = new TextField();
		lastArea.setMinHeight(20);
		lastArea.setMaxWidth(300);

		Label emailAddress = new Label("Email Address");
		TextField emailArea = new TextField();
		emailArea.setMinHeight(20);
		emailArea.setMaxWidth(300);

		Label preferredName = new Label("Preferred Name (Optional)");
		TextField preferArea = new TextField();
		preferArea.setMinHeight(20);
		preferArea.setMaxWidth(300);

		// On-action commands that set users to go to a specific page

		Button studentPage = new Button("Finish Setting Up Profile (Student)");
		studentPage.setMaxWidth(300);

		// User profile set up
		studentPage.setOnAction(e -> {
			UserHomePage userHome = new UserHomePage();
			String name = firstArea.getText() + " " + middleArea.getText() + " " + lastArea.getText();
			String email = emailArea.getText();
			String preferName = preferArea.getText();
			DatabaseHelper db = new DatabaseHelper();
			try {
				db.connectToDatabase();
				db.updateProfile(username, email, name, preferName);
				System.out.println("Profile created for " + username);
				primaryStage.setScene(userHome.getScene(primaryStage, username));
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				db.closeConnection();
			}
		});

		Button adminPage = new Button("Finish Setting Up Profile (Admin)");
		adminPage.setMaxWidth(300);

		adminPage.setOnAction(e -> {
			AdminHomePage adminHome = new AdminHomePage();
			primaryStage.setScene(adminHome.getScene(primaryStage, username));
		});

		Button teacherPage = new Button("Finish Setting Up Profile (Instructor)");
		teacherPage.setMaxWidth(300);

		// Instructor profile set up
		teacherPage.setOnAction(e -> {
			InstructorPage instru = new InstructorPage();
			String name = firstArea.getText() + " " + middleArea.getText() + " " + lastArea.getText();
			String email = emailArea.getText();
			String preferName = preferArea.getText();
			DatabaseHelper db = new DatabaseHelper();
			try {
				db.connectToDatabase();
				db.updateProfile(username, email, name, preferName);
				System.out.println("Profile created for " + username);
				primaryStage.setScene(instru.getScene(primaryStage, username));
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				db.closeConnection();
			}
		});

		ScrollPane scrolling = new ScrollPane(cb);
		scrolling.setFitToWidth(true); // Ensure scroll pane resizes with the window

		/*
		 * Adds personal information first to setup the layout correctly. Depending on
		 * one's choice, they can log in as either admin, student or teacher. Specific
		 * role buttons display corresponding to what the user set up as their choice in
		 * the create account page.
		 */
		cb.getChildren().addAll(profileFinish, FirstName, firstArea, MiddleName, middleArea, LastName, lastArea,
				emailAddress, emailArea, preferredName, preferArea);
		System.out.println(roleKind);
		if ("Admin".equals(roleKind)) {
			cb.getChildren().addAll(adminPage, studentPage, teacherPage);
		} else if ("All".equals(roleKind)) {
			cb.getChildren().addAll(studentPage, teacherPage);
		} else if ("Student".equals(roleKind)) {
			cb.getChildren().addAll(studentPage);
		} else {
			cb.getChildren().addAll(teacherPage);
		}
		return new Scene(scrolling, 800, 800);
	}
}