// ExpenseListController.java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class ExpenseListController {

    @FXML private TableView<Expense>           expenseTable;
    @FXML private TableColumn<Expense,Integer> colId;
    @FXML private TableColumn<Expense,String>  colDate;
    @FXML private TableColumn<Expense,String>  colDescription;
    @FXML private TableColumn<Expense,String>  colCategory;
    @FXML private TableColumn<Expense,Double>  colAmount;
    @FXML private Label                        totalLabel;

    private User            currentUser;
    private ExpenseManager  manager;
    private FileHandler     fileHandler;
    private ArrayList<User> users;
    private ObservableList<Expense> displayList = FXCollections.observableArrayList();

    public void init(User user, ExpenseManager manager,
                     FileHandler fileHandler, ArrayList<User> users) {
        this.currentUser = user;
        this.manager     = manager;
        this.fileHandler = fileHandler;
        this.users       = users;

        colId.setCellValueFactory(new PropertyValueFactory<>("expenseId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        expenseTable.setItems(displayList);
        refresh();
    }

    private void refresh() {
        displayList.setAll(manager.getExpenses());
        totalLabel.setText(String.format("Total: RM %.2f", manager.getTotalAmount()));
    }

    @FXML private void handleAdd()    { showDialog(null); }

    @FXML
    private void handleEdit() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Select an expense to edit."); return; }
        showDialog(selected);
    }

    @FXML
    private void handleDelete() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Select an expense to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getDescription() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) { manager.deleteExpense(selected.getExpenseId()); saveAndRefresh(); }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Stage stage = (Stage) expenseTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 560));
            ((DashboardController) loader.getController())
                    .init(currentUser, manager, fileHandler, users);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showDialog(Expense existing) {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Expense" : "Edit Expense");

        TextField   amountField = new TextField(existing == null ? "" : String.valueOf(existing.getAmount()));
        TextField   descField   = new TextField(existing == null ? "" : existing.getDescription());
        TextField   dateField   = new TextField(existing == null ? "" : existing.getDate());
        ComboBox<String> catBox = new ComboBox<>(
                FXCollections.observableArrayList(ExpenseManager.CATEGORIES));
        if (existing != null) catBox.setValue(existing.getCategory());

        
        CheckBox recurringCheck = new CheckBox("Is Recurring?");
        ComboBox<String> freqBox = new ComboBox<>(
                FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        freqBox.setDisable(true); // Disabled by default

        // Populate existing recurring data if we are editing
        if (existing instanceof RecurringExpense) {
            recurringCheck.setSelected(true);
            freqBox.setDisable(false);
            freqBox.setValue(((RecurringExpense) existing).getFrequency());
        }

        // Enable/Disable frequency dropdown when checkbox is clicked
        recurringCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            freqBox.setDisable(!newVal);
            if (!newVal) freqBox.setValue(null); // Clear selection if unchecked
        });

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Amount (RM):"), 0, 0); grid.add(amountField, 1, 0);
        grid.add(new Label("Description:"), 0, 1); grid.add(descField,   1, 1);
        grid.add(new Label("Category:"),    0, 2); grid.add(catBox,      1, 2);
        grid.add(new Label("Date (YYYY-MM-DD):"), 0, 3); grid.add(dateField, 1, 3);
        grid.add(recurringCheck, 0, 4);            // Add Checkbox to grid
        grid.add(new Label("Frequency:"), 0, 5);   grid.add(freqBox, 1, 5); // Add ComboBox
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String desc   = descField.getText().trim();
                String cat    = catBox.getValue();
                String date   = dateField.getText().trim();
                
                if (cat == null || desc.isEmpty() || date.isEmpty()) { alert("Fill in all fields."); return null; }
                
                int id = existing == null ? 0 : existing.getExpenseId();

                // --- NEW RETURN LOGIC FOR RECURRING ---
                if (recurringCheck.isSelected()) {
                    String freq = freqBox.getValue();
                    if (freq == null) { alert("Please select a frequency."); return null; }
                    return new RecurringExpense(id, amount, desc, cat, date, freq);
                } else {
                    return new Expense(id, amount, desc, cat, date);
                }
                
            } catch (NumberFormatException ex) { alert("Amount must be a number."); return null; }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (existing == null) manager.addExpense(result);
            else                  manager.editExpense(result);
            saveAndRefresh();
        });
    }

    private void saveAndRefresh() {
        try { fileHandler.saveExpenses(manager.getExpenses()); }
        catch (IOException e) { alert("Error saving: " + e.getMessage()); }
        refresh();
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}