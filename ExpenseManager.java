// ExpenseManager.java
// Manages the in-memory list of expenses.
// Demonstrates ARRAYLISTS and CRUD operations.

import java.util.ArrayList;

public class ExpenseManager {

    private ArrayList<Expense> expenses = new ArrayList<>();
    private int nextId = 1;

    // Predefined categories (simple String list)
    public static final String[] CATEGORIES = {
        "Food", "Transport", "Entertainment", "Utilities", "Health", "Other"
    };

    // Add a new expense (auto-assigns an ID)
    public void addExpense(Expense e) {
        e = rebuildWithId(e, nextId++);
        expenses.add(e);
    }

    // Replace the expense with the same ID
    public boolean editExpense(Expense updated) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getExpenseId() == updated.getExpenseId()) {
                expenses.set(i, updated);
                return true;
            }
        }
        return false;
    }

    // Remove by ID
    public boolean deleteExpense(int id) {
        return expenses.removeIf(e -> e.getExpenseId() == id);
    }

    public ArrayList<Expense> getExpenses() { return expenses; }

    // Sum all expense amounts
    public double getTotalAmount() {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        return total;
    }

    // Called by FileHandler after loading from disk
    public void setExpenses(ArrayList<Expense> loaded) {
        expenses = loaded;
        for (Expense e : loaded)
            if (e.getExpenseId() >= nextId) nextId = e.getExpenseId() + 1;
    }

    // Rebuild expense with a specific ID (needed for auto-increment)
    private Expense rebuildWithId(Expense e, int id) {
        if (e instanceof RecurringExpense) {
            RecurringExpense r = (RecurringExpense) e;
            return new RecurringExpense(id, r.getAmount(), r.getDescription(),
                    r.getCategory(), r.getDate(), r.getFrequency());
        }
        return new Expense(id, e.getAmount(), e.getDescription(),
                e.getCategory(), e.getDate());
    }
}
