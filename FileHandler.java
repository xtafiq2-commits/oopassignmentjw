// FileHandler.java
// Saves and loads users and expenses from plain text files.
// Demonstrates FILE HANDLING with BufferedReader and BufferedWriter.

import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    private static final String USERS_FILE    = "users.txt";
    private static final String EXPENSES_FILE = "expenses.txt";

    // ── Users ─────────────────────────────────────────────────────────────

    public void saveUsers(ArrayList<User> users) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE));
        for (User u : users) {
            writer.write(u.toString());
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<User> loadUsers() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] p = line.split(",", 3);
            if (p.length == 3)
                users.add(new User(p[0], p[1], p[2]));
        }
        reader.close();
        return users;
    }

    // ── Expenses ──────────────────────────────────────────────────────────
    // Normal format:    id,amount,description,category,date
    // Recurring format: id,amount,description,category,date,frequency,recurring

    public void saveExpenses(ArrayList<Expense> expenses) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSES_FILE));
        for (Expense e : expenses) {
            writer.write(e.toString());
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<Expense> loadExpenses() throws IOException {
        ArrayList<Expense> expenses = new ArrayList<>();
        File file = new File(EXPENSES_FILE);
        if (!file.exists()) return expenses;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] p = line.split(",");
            try {
                int    id     = Integer.parseInt(p[0]);
                double amount = Double.parseDouble(p[1]);
                String desc   = p[2];
                String cat    = p[3];
                String date   = p[4];

                if (p.length == 7 && p[6].equals("recurring")) {
                    expenses.add(new RecurringExpense(id, amount, desc, cat, date, p[5]));
                } else {
                    expenses.add(new Expense(id, amount, desc, cat, date));
                }
            } catch (Exception ex) {
                System.out.println("Skipping bad line: " + line);
            }
        }
        reader.close();
        return expenses;
    }
}
