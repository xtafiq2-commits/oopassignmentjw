// Extends Expense to add a recurrence frequency.

public class RecurringExpense extends Expense {

    private String frequency; // "Daily", "Weekly", "Monthly"

    public RecurringExpense(int expenseId, double amount, String description,
                            String category, String date, String frequency) {
        super(expenseId, amount, description, category, date);
        this.frequency = frequency;
    }

    public String  getFrequency() { return frequency; }
    public void    setFrequency(String frequency) { this.frequency = frequency; }
    public boolean isRecurring()  { return true; }

    // Append frequency to the parent CSV line
    @Override
    public String toString() {
        return super.toString() + "," + frequency + ",recurring";
    }
}
