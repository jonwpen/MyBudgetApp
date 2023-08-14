package services;

import java.util.HashMap; 
import java.util.List;
import java.util.Map;
import data.BudgetData;
import model.Category;
import model.Expense;

public class BudgetServices {

    private BudgetData budgetData;

    public BudgetServices(BudgetData budgetData) {
        this.budgetData = budgetData;
    }
    //Used in Main Menu 1. sub menu 3.
    public double getCurrentSavingsRate(int userId) {
        double totalWeeklySavings = budgetData.getTotalWeeklySavings(userId);
        double totalWeeklyIncome = budgetData.getTotalWeeklyIncome(userId);

        //Return savings rate as a percentage
        if (totalWeeklyIncome > 0) {
            return (totalWeeklySavings / totalWeeklyIncome) * 100;
        } else {
            return 0;
        }
    }
    //Return the percentage of monthly income that is spent on each expense category
    public HashMap<String, Double> getCategoryExpensePercentage(int userId, double monthlyIncome) { 
        List<Expense> expenses = budgetData.getExpensesByUserId(userId);
        HashMap<Integer, Double> categoryExpenseTotals = new HashMap<>();

        for (Expense expense : expenses) {
            double amount = expense.getAmount();
            //Convert the amount based on the frequency
            switch (expense.getFrequency()) {
                case "weekly":
                    amount *= 4; //4 weeks in a month
                    break;
                case "annually":
                    amount /= 12; //Divide by 12 for a monthly amount
                    break;
                //"monthly" is already in the right format
            }
            //Add or update the category total
            int categoryId = expense.getCategory_id();
            if (categoryExpenseTotals.containsKey(categoryId)) {
                double existingAmount = categoryExpenseTotals.get(categoryId);
                categoryExpenseTotals.put(categoryId, existingAmount + amount);
            } else {
                categoryExpenseTotals.put(categoryId, amount);
            }
        }

        //Convert the category totals into a map with category name and percentage of income
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : categoryExpenseTotals.entrySet()) {
            Category category = budgetData.getCategoryById(entry.getKey());
            double percentage = (entry.getValue() / monthlyIncome) * 100;
            percentage = Math.floor(percentage * 10) / 10; //Truncating to one decimal place without rounding
            result.put(category.getName(), percentage);
        }

        return result;
    }

    public HashMap<String, Double> getExpensePercentageOfWeeklyIncome(int userId, double weeklyIncome) {
        List<Expense> expenses = budgetData.getExpensesByUserId(userId);
        HashMap<String, Double> expensePercentages = new HashMap<>();

        for (Expense expense : expenses) {
            double amount = expense.getAmount();

            //Convert the amount based on the frequency
            switch (expense.getFrequency()) {
                case "weekly":
                    //Amount is already in the right format
                    break;
                case "monthly":
                    amount /= 4.5; //Divide by the average number of weeks in a month
                    break;
                case "annually":
                    amount /= 52; //Divide by the number of weeks in a year
                    break;
            }

            //Calculate the percentage of weekly income
            double percentage = 0;
            if (weeklyIncome > 0) {
                percentage = (amount / weeklyIncome) * 100;
            }

            //Add the expense name and percentage to the result map
            expensePercentages.put(expense.getName(), percentage);
        }

        return expensePercentages;
    }

    public double[] getProjectedBalances(int userId, double currentBalance) {
        //Get the user's monthly income
        double monthlyIncome = budgetData.getTotalWeeklyIncome(userId)*4.5;

        //Get the user's expenses
        List<Expense> expenses = budgetData.getExpensesByUserId(userId);

        //Calculate the total monthly expenses
        double totalMonthlyExpenses = 0;
        for (Expense expense : expenses) {
            double amount = expense.getAmount();
            switch (expense.getFrequency()) {
                case "weekly":
                    amount *= 4.5; //4.5 weeks in a month
                    break;
                case "annually":
                    amount /= 12; //Divide by 12 for a monthly amount
                    break;
                //"monthly" is already in the right format
            }
            totalMonthlyExpenses += amount;
        }

        //Calculate the monthly net income (income minus expenses)
        double monthlyNetIncome = monthlyIncome - totalMonthlyExpenses;

        //Project the balances
        double oneMonthBalance = currentBalance + monthlyNetIncome;
        double sixMonthsBalance = currentBalance + (6 * monthlyNetIncome);
        double oneYearBalance = currentBalance + (12 * monthlyNetIncome);

        return new double[]{oneMonthBalance, sixMonthsBalance, oneYearBalance};
    }
    
    public String calculateTimeToPayOffDebt(double debtAmount, double monthlyPayment, double annualInterestRate) {
        if (monthlyPayment <= 0 || debtAmount < 0 || annualInterestRate < 0) {
            return "Debt amount, monthly payment, and interest rate must be greater than zero.";
        }
        double monthlyInterestRate = annualInterestRate / 12;
        int numberOfMonths = 0;

        if (monthlyPayment < (debtAmount * monthlyInterestRate)) {
            return "Monthly payment must be greater than the interest accrued per month.";
        }
        try {
            while (debtAmount > 0) {
                double interestForThisMonth = debtAmount * monthlyInterestRate;
                double principalForThisMonth = monthlyPayment - interestForThisMonth;

                debtAmount -= principalForThisMonth;
                numberOfMonths++;

                if (numberOfMonths > 1000) { //avoid an infinite loop
                    return "Debt not being paid off within a reasonable time frame. Please review the entered values.";
                }
            }
        } catch (Exception e) {
            return "An error occurred while calculating the time to pay off the debt. Please try again.";
        }
        return "Time to pay off the debt: " + numberOfMonths + " months.";
    }

    public double getRatioOfIncomeSaved(double weeklyIncome, double weeklySavings) {
        //Avoid division by zero
        if (weeklyIncome == 0) {
            return 0;
        }
        double ratio = weeklySavings / weeklyIncome;
        return ratio * 100; //returning the percentage
    }
    
}





