// LoginController.java
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private ArrayList<User> users;
    private FileHandler     fileHandler;

    public void init(ArrayList<User> users, FileHandler fileHandler) {
        this.users       = users;
        this.fileHandler = fileHandler;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                openDashboard(u);
                return;
            }
        }
        errorLabel.setText("Invalid username or password.");
    }

    @FXML
    private void handleGoRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 460));
            ((RegisterController) loader.getController()).init(users, fileHandler);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 560));
            ExpenseManager manager = new ExpenseManager();
            try { manager.setExpenses(fileHandler.loadExpenses()); } catch (IOException ex) { ex.printStackTrace(); }
            ((DashboardController) loader.getController()).init(user, manager, fileHandler, users);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
