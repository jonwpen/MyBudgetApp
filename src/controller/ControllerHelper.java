package controller;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import data.BudgetData;
import model.Category;
import model.Debts;
import model.Expense;
import model.Goals;
import model.Income;
import model.User;
import services.BudgetServices;

/*
 * The ControllerHelper class serves as a utility component within the budgeting application, facilitating user input handling, 
 * data presentation, and specific budget-related services. It contains methods to interact with both the user and underlying data models.
 * It contains all methods for setting up the user's initial budget expenses.
 */
public class ControllerHelper {
    private BudgetData budgetData;
    BudgetServices services;

    public ControllerHelper(BudgetData budgetData, BudgetServices services) {
        this.budgetData = budgetData;
        this.services = services;
    }
	
	static int readInt(Scanner scanner, String prompt) {
	    int input = 0;
	    boolean validInput = false;
	    while (!validInput) {
	        try {
	            System.out.print(prompt);
	            input = scanner.nextInt();
	            scanner.nextLine(); // Consume the newline character
	            validInput = true;
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next();
	        }
	    }
	    return input;
	}

	static void printList(List<String> items) {
	    for (String item : items) {
	        System.out.println(item);
	    }
	}

	static double readDouble(Scanner scanner, String prompt) {
	    double input = 0;
	    boolean validInput = false;
	    while (!validInput) {
	        try {
	            System.out.print(prompt);
	            input = scanner.nextDouble();
	            validInput = true;
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next();
	        }
	    }
	    scanner.nextLine();
	    return input;
	}

	static int promptAccountType(Scanner scanner) {
	    int accountTypeChoice = 0;
	    boolean validChoice = false;
	    while (!validChoice) {
	        try {
	            System.out.println("Choose Account Type:");
	            System.out.println("1. Checking");
	            System.out.println("2. Savings");
	            accountTypeChoice = scanner.nextInt();
	            if (accountTypeChoice != 1 && accountTypeChoice != 2) {
	                throw new IllegalArgumentException("Please enter 1 for Checking or 2 for Savings.");
	            }
	            validChoice = true;
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next();
	        } catch (IllegalArgumentException e) {
	            System.out.println(e.getMessage());
	            scanner.nextLine();
	        }
	    }
	    return accountTypeChoice;
	}

	static int readValidAccountId(Scanner scanner, List<String> accounts) {
	    int accountId = 0;
	    boolean validAccountId = false;
	    while (!validAccountId) {
	        try {
	            System.out.print("Enter the account ID: ");
	            accountId = scanner.nextInt();
	            for (String account : accounts) {
	                String[] parts = account.split(" ");
	                int validId = Integer.parseInt(parts[2]);
	                if (accountId == validId) {
	                    validAccountId = true;
	                    break;
	                }
	            }
	            if (!validAccountId) {
	                System.out.println("Invalid account ID. Please enter a valid ID.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next();
	        }
	    }
	    return accountId;
	}
	
    static String readString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
	
    static void editIncome(BudgetData budgetData, User user, Scanner scanner) {
        System.out.println("Available incomes:");
        List<Income> incomes = budgetData.getIncomesByUser(budgetData.getUserId(user));
        for (Income income : incomes) {
            System.out.println("Income ID: " + income.getIncome_id() + ", Weekly Income: " + income.getWeekly_income());
        }
        System.out.print("Enter the Income ID you want to edit: ");
        int incomeId;
        try {
            incomeId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            return; //Exit method and re-prompt
        }
        boolean validIncomeId = false;
        for (Income income : incomes) {
            if (income.getIncome_id() == incomeId) {
                validIncomeId = true;
                break;
            }
        }
        if (!validIncomeId) {
            System.out.println("Invalid Income ID. Please enter a valid Income ID.");
            return;
        }
        System.out.print("Enter the new weekly income: ");
        double newWeeklyIncome = scanner.nextDouble();

        budgetData.editIncome(incomeId, newWeeklyIncome);
    }
    
    static void addCustomExpense(BudgetData budgetData, User user, Scanner scanner, String[] frequencies) {
        List<String> categories = Category.predefinedCategories();
        System.out.println("__________________________________________");
        System.out.println("Select a category for your custom expense:");
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        int selectedCategoryIndex = readInt(scanner, "Choose category (1-" + categories.size() + "): ");

        if (selectedCategoryIndex < 1 || selectedCategoryIndex > categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

         String customExpenseName = readString(scanner, "Enter Custom Expense Name: ");
        double amount = readDouble(scanner, "Enter Amount: ");

        System.out.println("Select Frequency:\n1. Weekly\n2. Monthly\n3. Annually");
        int frequencyChoice = readInt(scanner, "Choose frequency (1-3): ") - 1;

        if (frequencyChoice < 0 || frequencyChoice >= frequencies.length) {
            System.out.println("Invalid frequency selection.");
            return;
        }

        int categoryId = budgetData.getCategoryIdByName(categories.get(selectedCategoryIndex - 1));
        Expense expense = new Expense(user.getUser_id(), customExpenseName, amount, frequencies[frequencyChoice], categoryId);
        budgetData.addExpense(expense, user);
    }

    static void editExpense(BudgetData budgetData, User user, Scanner scanner, String[] frequencies) {
        List<Expense> userExpenses = budgetData.getExpensesByUserId(user.getUser_id());
        if (userExpenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("Your expenses:");
        for (Expense exp : userExpenses) {
            System.out.println("ID: " + exp.getExpense_id() + ", Name: " + exp.getName()
                    + ", Amount: " + exp.getAmount() + ", Frequency: " + exp.getFrequency());
        }

        int expenseId = readInt(scanner, "Enter Expense ID to edit: ");
        String newName = readString(scanner, "Enter New Expense Name: ");
        double newAmount = readDouble(scanner, "Enter New Amount: ");

        System.out.println("Select New Frequency:\n1. Weekly\n2. Monthly\n3. Annually");
        int frequencyChoice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (frequencyChoice < 0 || frequencyChoice >= frequencies.length) {
            System.out.println("Invalid frequency selection.");
            return;
        }
        
        Expense updatedExpense = new Expense();
        updatedExpense.setExpense_id(expenseId);
        updatedExpense.setName(newName);
        updatedExpense.setAmount(newAmount);
        updatedExpense.setFrequency(frequencies[frequencyChoice]);

        budgetData.updateExpense(updatedExpense);
    }
    
    public List<String> viewAndCalculateDebts(BudgetData budgetData, User user) {
        List<String> outputLines = new ArrayList<>();
        List<String> debts = budgetData.getDebtsByUser(budgetData.getUserId(user));

        if (debts.isEmpty()) {
            outputLines.add("You have no debts.");
            return outputLines;
        } else {
            outputLines.add("Here are your debts:");
            outputLines.addAll(debts);
            
            for (int i = 0; i < debts.size(); i++) {
                Debts selectedDebt = budgetData.getDebtByIndex(budgetData.getUserId(user), i);

                double remainingBalance = selectedDebt.getRemaining_balance();
                double interestRate = selectedDebt.getInterest_rate();
                double monthlyPayment = selectedDebt.getMonthlyPayment();

                String result = services.calculateTimeToPayOffDebt(remainingBalance, monthlyPayment, interestRate);
                outputLines.add("For debt " + (i + 1) + ", " + result);
            }
        }

        return outputLines;
    }

	public static String printAllGoals(List<Goals> goals) {
	    StringBuilder output = new StringBuilder();
	    output.append("Your Goals:\n\n");
	    for (Goals goal : goals) {
	        output.append("Goal ID: " + goal.getGoal_id() + "\n");
	        output.append("Name: " + goal.getName() + "\n");
	        output.append("Amount: " + goal.getAmount() + "\n");
	        output.append("Remaining Amount: " + goal.getRemaining_amount() + "\n");
	        output.append("Target Date: " + goal.getTarget_date() + "\n\n");
	    }
	    return output.toString();
	}

	//Setup expense utility methods
	public void setupInitialExpenses(Scanner scanner, User user) throws IllegalArgumentException{
		System.out.println("___________________________________");
	    System.out.println("Setting up initial expenses:");
	    
	    try {
	    setupExpense("Total Subscriptions","Subscriptions and Memberships", "Monthly", scanner, user);
	    
	    setupExpense("Car Tags","Transportation", "Yearly", scanner, user);
	    setupExpense("Doctor Checkup","Health and Wellness", "Yearly", scanner, user);
	    setupExpense("Rent","Living Expenses", "Monthly", scanner, user);
	    setupExpense("Phone","Living Expenses", "Monthly", scanner, user);
	    setupExpense("Utilities","Living Expenses", "Monthly", scanner, user);
	    setupExpense("Electricity","Living Expenses", "Monthly", scanner, user);
	    setupExpense("Car Insurance","Transportation", "Monthly", scanner, user);
	    setupExpense("Gas","Transportation", "Weekly", scanner, user);
	    setupExpense("Groceries","Food and Dining", "Weekly", scanner, user);
	    setupExpense("Work Snacks","Food and Dining", "Weekly", scanner, user);
	    setupExpense("Eating Out Weekly Cost","Food and Dining", "Weekly", scanner, user);
	    setupExpense("Alcohol Weekly Cost","Food and Dining", "Weekly", scanner, user);
	    setupExpense("Disposable","Leisure", "Monthly", scanner, user);
	    setupExpense("Car Tires","Transportation", "Yearly", scanner, user);
	    setupExpense("Car Loan Debt","Transportation", "Monthly", scanner, user);
	    setupExpense("Student Loan Payment","Debts and Loans", "Monthly", scanner, user);
	    
	    } catch (InputMismatchException e) {
	        scanner.nextLine();//Clear error from buffer
	        throw new IllegalArgumentException("Invalid input. Please enter a valid number for the expenses.");
	    }
	}

	private void setupExpense(String name, String category, String frequency, Scanner scanner, User user) {
	    double amount = readDouble(scanner, "Enter " + name + " (" + frequency + ") $: ");
	    if (amount == 0 && isDebtExpense(name)) {
	        System.out.println(name + " expense and associated debt skipped as the amount is zero.\n");
	        return; //Exit the method early, skipping the debt prompts
	    }
	    createAndAddExpense(name, category, amount, frequency, user);

	    if (isDebtExpense(name)) {
	        double interestRate = readDouble(scanner, "Enter interest rate for " + name + " (%): ");
	        double monthlyPayment = amount;

	        //Prompt for total remaining balance if the expense is also a debt
	        double remainingBalance = readDouble(scanner, "Enter total remaining balance for " + name + " $: ");

	        int monthlyPaymentDate;

	        do {
	            monthlyPaymentDate = readInt(scanner, "Enter monthly payment date (day of the month) for " + name + ": ");
	            if (monthlyPaymentDate < 1 || monthlyPaymentDate > 31) {
	                System.out.println("Invalid date. Please enter a value between 1 and 31.");
	            }
	        } while (monthlyPaymentDate < 1 || monthlyPaymentDate > 31);

	        createAndAddDebt(name, remainingBalance, interestRate, monthlyPayment, monthlyPaymentDate, user);
	    }
	}

	private static boolean isDebtExpense(String name) {
	    //Names of the expenses that are also considered debts
	    return name.equals("Car Loan Debt") || name.equals("Student Loan Payment");
	}
	
	private void createAndAddDebt(String name, double remainingBalance, double interestRate, double monthlyPayment, int monthlyPaymentDate, User user) {
	    if (monthlyPayment != 0) {
	        int expenseId = budgetData.getLastInsertedExpenseId(user);
	        Debts debt = new Debts(expenseId, interestRate, remainingBalance, monthlyPayment, monthlyPaymentDate);
	        budgetData.addDebt(debt);
	    }
	}
	
	private void createAndAddExpense(String expenseName, String category, double amount, String frequency, User user) {
	    if (amount > 0) {
	        int user_id4 = budgetData.getUserId(user);
	        int categoryId = budgetData.getCategoryIdByName(category);
	        Expense expense = new Expense(user_id4, expenseName, amount, frequency, categoryId);

	        budgetData.addExpense(expense, user); //Call the addExpense method with the Expense and User objects
	    } else {
	        System.out.println(expenseName + " expense skipped as the amount is zero.\n");
	    }
	}
	//End of Setup expense utility methods ^
}
