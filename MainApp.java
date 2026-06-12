// MainApp.java
// JavaFX entry point — loads users from file then shows the login screen.

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        FileHandler     fileHandler = new FileHandler();
        ArrayList<User> users       = new ArrayList<>();

        try {
            users = fileHandler.loadUsers();
        } catch (IOException e) {
            System.out.println("Starting fresh — no users file found.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            primaryStage.setScene(new Scene(loader.load(), 400, 340));
            primaryStage.setTitle("MyExpense Tracker");
            primaryStage.setResizable(false);
            ((LoginController) loader.getController()).init(users, fileHandler);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
