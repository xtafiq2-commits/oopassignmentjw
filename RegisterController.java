// RegisterController.java
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class RegisterController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label         errorLabel;

    private ArrayList<User> users;
    private FileHandler     fileHandler;

    public void init(ArrayList<User> users, FileHandler fileHandler) {
        this.users       = users;
        this.fileHandler = fileHandler;
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                errorLabel.setText("Username already taken.");
                return;
            }
        }

        users.add(new User(username, password, email));
        try {
            fileHandler.saveUsers(users);
            goToLogin();
        } catch (IOException e) {
            errorLabel.setText("Could not save user.");
        }
    }

    @FXML
    private void handleGoLogin() { goToLogin(); }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 340));
            ((LoginController) loader.getController()).init(users, fileHandler);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
