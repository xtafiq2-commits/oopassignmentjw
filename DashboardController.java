// DashboardController.java
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalLabel;
    @FXML private Label countLabel;

    private User           currentUser;
    private ExpenseManager manager;
    private FileHandler    fileHandler;
    private ArrayList<User> users;

    public void init(User user, ExpenseManager manager,
                     FileHandler fileHandler, ArrayList<User> users) {
        this.currentUser = user;
        this.manager     = manager;
        this.fileHandler = fileHandler;
        this.users       = users;

        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        totalLabel.setText(String.format("RM %.2f", manager.getTotalAmount()));
        countLabel.setText(manager.getExpenses().size() + " records");
    }

    @FXML
    private void handleViewExpenses() { openExpenseList(); }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 340));
            ((LoginController) loader.getController()).init(users, fileHandler);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openExpenseList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("expenseList.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 860, 560));
            ((ExpenseListController) loader.getController())
                    .init(currentUser, manager, fileHandler, users);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
