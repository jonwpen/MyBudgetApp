package services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.ControllerHelper;
import data.BudgetData;
import model.Category;
import model.Expense;
import model.Goals;
import model.User;

/*
 * The BudgetServices class encapsulates various financial calculations, analysis, and reports, functioning as a central hub for the application's business logic.
 * It leverages data models and utilities provided by other classes, such as BudgetData and ControllerHelper, to perform its tasks. 
 */
public class BudgetServices {
    private BudgetData budgetData;
    ControllerHelper controllerHelper;

    public BudgetServices(BudgetData budgetData, ControllerHelper controllerHelper) {
        this.budgetData = budgetData;
        this.controllerHelper = controllerHelper;
    }
    
    public void setControllerHelper(ControllerHelper controllerHelper) {
        this.controllerHelper = controllerHelper;
    }
    
    //Return percentage of weekly income that is being saved
    public double getCurrentSavingsRate(int userId) {
        double totalWeeklySavings = budgetData.getTotalWeeklySavings(userId);
        double totalWeeklyIncome = budgetData.getTotalWeeklyIncome(userId);

        //Return savings rate as a percentage
        if (totalWeeklyIncome > 0) {
            double savingsRate = (totalWeeklySavings / totalWeeklyIncome) * 100;

            //Format the savingsRate to two decimal places without rounding
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            String result = df.format(savingsRate);

            return Double.parseDouble(result);
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

    //Return percentage of weekly income spent on each expense
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
                percentage = Math.floor(percentage * 10) / 10; //Truncating to one decimal place without rounding
            }

            //Add the expense name and percentage to the result map
            expensePercentages.put(expense.getName(), percentage);
        }

        return expensePercentages;
    }

    //Project checking balance for 1 month, 6 months, and 1 year into the future
    public double[] getProjectedBalances(int userId, double currentBalance) {
        
        double monthlyIncome = budgetData.getTotalWeeklyIncome(userId) * 4.4; //Get the user's monthly income

        List<Expense> expenses = budgetData.getExpensesByUserId(userId); //Get the user's expenses

        //Calculate the total monthly expenses
        double totalMonthlyExpenses = 0;
        for (Expense expense : expenses) {
            double amount = expense.getAmount();
            switch (expense.getFrequency()) {
                case "weekly":
                    amount *= 4.4; //4.4 weeks in a month
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

        oneMonthBalance = Math.floor(oneMonthBalance * 100) / 100; //Truncating to two decimal places without rounding
        sixMonthsBalance = Math.floor(sixMonthsBalance * 100) / 100; 
        oneYearBalance = Math.floor(oneYearBalance * 100) / 100; 

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
        double ratio = weeklySavings / weeklyIncome * 100; //calculating the percentage
        return (int) (ratio * 100) / 100.0; //truncate to two decimal places without rounding
    }
    
    //Gather all financial data for a user and write it to a .txt file
    public void generateFinancialSanityReport(User user, Scanner scanner) throws Exception {
        String fileName = "FinancialSanityReport_" + user.getUsername() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
        	writer.println("_____________________________________________________________________");
        	writer.println("                Comprehensive Financial Sanity Report                ");
        	writer.println("_____________________________________________________________________");
            
            //Account ID and Balances
            writer.println("\nAccount ID and Balances:");
            List<String> accountsBalance = budgetData.getAccountsByUser(user.getUser_id());
            double totalBalance = 0; // Initialize the total balance
            for (String i : accountsBalance) {
                writer.println(i);

                // Extracting the balance from the account info
                String balanceStr = i.substring(i.lastIndexOf(' '));
                double balance = Double.parseDouble(balanceStr.trim());
                totalBalance += balance;
            }
            writer.println("\nTotal Accounts Balance: " + totalBalance);
        	
            //Current Savings Rate
            writer.println("_____________");
            writer.println("\nCurrent Savings Rate:\n");
            writer.println("You are currently saving " + getCurrentSavingsRate(user.getUser_id()) + "% of your income.");
        	
            //Expense Categories
            writer.println("_____________");
            writer.println("\nExpense Categories:\n");
            double weeklyIncome = budgetData.getTotalWeeklyIncome(user.getUser_id());
            HashMap<String, Double> categoryExpensePercentages = getCategoryExpensePercentage(user.getUser_id(), weeklyIncome * 4.5);
            for (HashMap.Entry<String, Double> entry : categoryExpensePercentages.entrySet()) {
            	writer.println(entry.getKey() + ": " + entry.getValue() + "%");
            }
        	
            //Projected Balances
            writer.println("_____________");
            writer.println("\nProjected Balances:\n");
            double currentCheckingBalance = budgetData.getCheckingBalanceByUserId(user.getUser_id()); 
            double[] projectedBalances = getProjectedBalances(user.getUser_id(), currentCheckingBalance);
            writer.println("Projected balance in 1 month: " + projectedBalances[0]);
            writer.println("Projected balance in 6 months: " + projectedBalances[1]);
            writer.println("Projected balance in 1 year: " + projectedBalances[2]);
            
            //View Debts and Calculate Time to Pay Off
            writer.println("_____________");
            writer.println("\nDebts and Time to Pay Off:\n");
            List<String> debtSummary = controllerHelper.viewAndCalculateDebts(budgetData, user);
            for (String line : debtSummary) {
                writer.println(line);
            }

            //Ratio of Income Saved
            writer.println("_____________");
            double weeklySavings = budgetData.getTotalWeeklySavings(user.getUser_id());
            double percentage = getRatioOfIncomeSaved(weeklyIncome, weeklySavings);
            writer.println("\nRatio of Income Saved Weekly: " + percentage + "%");
            
            //Goals Section
            writer.println("_____________");
            List<Goals> userGoals = budgetData.getGoalsByUserId(user.getUser_id());
            String goalsOutput = ControllerHelper.printAllGoals(userGoals);
            writer.print(goalsOutput);

            writer.println("_____________________________________________________________________");
            
            System.out.println("Financial sanity report generated successfully!");
            System.out.println("Please open the text file named '" + fileName + "' to view your data.");
        	
        } catch (IOException e) {
            //Re-throw as a new exception with a user-friendly message
            throw new Exception("An error occurred while generating the financial report.");
        }
    }
}





