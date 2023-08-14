package controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner; 
import data.BudgetData;
import model.AccountType;
import model.Accounts;
import model.Category;
import model.Debts;
import model.Expense;
import model.Goals;
import model.Income;
import model.User;
import services.BudgetServices;

public class ControllerUserInterface {
	
	private Scanner scanner;
	private BudgetServices services;
	private BudgetData budgetData;
	private static User currentUser;

	public ControllerUserInterface(BudgetData budgetData) { 
		scanner = new Scanner(System.in);
		services = new BudgetServices(budgetData);
		this.budgetData = budgetData;
	}
	
	public User createUser() throws UserAlreadyExistsException {
	    System.out.println("Creating a new user.");
	    System.out.println("Enter first name:");
	    String firstName = scanner.nextLine();

	    System.out.println("Enter last name:");
	    String lastName = scanner.nextLine();

	    System.out.println("Enter username:");
	    String username = scanner.nextLine();

	    System.out.println("Enter email:");
	    String email = scanner.nextLine();

	    //Check if the user already exists
	    if (budgetData.isUserExists(username, email)) {
	        throw new UserAlreadyExistsException("User with the given username or email already exists");
	    }

	    //Create a new User object with the provided information
	    currentUser = new User(firstName, lastName, username, email);
	    //Insert the user into the database using the BudgetData class method
	    budgetData.addUser(currentUser);

	    //Ask user for income information
	    System.out.println("Enter weekly income:");
	    double weeklyIncome = scanner.nextDouble();
	    scanner.nextLine();
	    //Insert income data into the database
	    Income income = new Income(currentUser.getUsername(), weeklyIncome);
	    budgetData.addIncome(currentUser.getUsername(), income);
	    
	    //Ask user for account information
	    System.out.println("Enter a checking account balance:");
	    double checkingBalance = scanner.nextDouble();
	    scanner.nextLine();
	    
	    AccountType checkingType = new AccountType("Checking");
	    //Set account type id
	    checkingType.setAccount_type_id(1);
	    Accounts checkingAccount = new Accounts(checkingType, budgetData.getUserId(currentUser), checkingBalance);
	    
	    budgetData.addAccount(checkingAccount);
	    
	    System.out.println("New user created successfully.");
	    return currentUser;
	}

	//Setup expense utility methods
	public void setupInitialExpenses() {
		System.out.println("___________________________________");
	    System.out.println("Setting up initial expenses:");
	    setupExpense("Subscriptions","Subscriptions and Memberships", "Monthly");
	    setupExpense("Car Tires","Transportation", "Yearly");
	    setupExpense("Car Tags","Transportation", "Yearly");
	    setupExpense("Doctor Checkup","Health and Wellness", "Yearly");
	    setupExpense("Rent","Living Expenses", "Monthly");
	    setupExpense("Phone","Living Expenses", "Monthly");
	    setupExpense("Utilities","Living Expenses", "Monthly");
	    setupExpense("Electricity","Living Expenses", "Monthly");
	    setupExpense("Car Insurance","Transportation", "Monthly");
	    setupExpense("Gas","Transportation", "Weekly");
	    setupExpense("Groceries","Food and Dining", "Weekly");
	    setupExpense("Work Snacks","Food and Dining", "Weekly");
	    setupExpense("Eating Out Weekly Cost","Food and Dining", "Weekly");
	    setupExpense("Alcohol Weekly Cost","Food and Dining", "Weekly");
	    setupExpense("Car Loan Debt","Transportation", "Monthly");
	    setupExpense("Student Loan Payment","Debts and Loans", "Monthly");
	    setupExpense("Disposable","Leisure", "Monthly");
	}

	private void setupExpense(String name, String category, String frequency) {
	    double amount = promptForExpense("Enter " + name + " (" + frequency + "): ");
	    createAndAddExpense(name, category, amount, frequency, currentUser);
	}

	private double promptForExpense(String prompt) {
	    System.out.print(prompt);
	    double amount = scanner.nextDouble();
	    scanner.nextLine();
	    return amount;
	}
	
	private void createAndAddExpense(String expenseName, String category, double amount, String frequency, User user) {
	    if (amount > 0) {
	        int user_id4 = budgetData.getUserId(user);
	        int categoryId = budgetData.getCategoryIdByName(category);
	        Expense expense = new Expense(user_id4, expenseName, amount, frequency, categoryId);

	        budgetData.addExpense(expense, currentUser); //Call the addExpense method with the Expense and User objects
	    } else {
	        System.out.println(expenseName + " expense skipped as the amount is zero.\n");
	    }
	}
	//End of Setup expense utility methods ^
	
	public void mainMenu() {
		System.out.println("___________________________________");
        System.out.println("Main Menu");
        System.out.println("1. View Accounts");
        System.out.println("2. Add / Edit Income and Expenses");
        System.out.println("3. Budget Analysis");
        System.out.println("4. Create / View Goals");
        System.out.println("5. Exit");
	}
	//Main Menu 1.
	public void accountsSummary() {
		System.out.println("___________________________________");
        System.out.println("Accounts Menu");
        System.out.println("1. Account Balances");
        System.out.println("2. Debt");
        System.out.println("3. Current Savings Rate");
        System.out.println("4. Add Account");
        System.out.println("5. Delete Account");
        System.out.println("6. Go back to Main Menu");
	}
	//Main Menu 1.
	private void handleAccountsSummaryMenu(User user) {
	    boolean viewAccountsMenuRunning = true;

	    while (viewAccountsMenuRunning) {
	        accountsSummary();
	        int viewAccountsMenuChoice = 0;

	        try {
	            viewAccountsMenuChoice = scanner.nextInt();
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next(); //Clear the invalid input
	            continue; //Skip to the next iteration to re-prompt the user
	        }

	        switch (viewAccountsMenuChoice) {
	            case 1:
	                List<String> accountsBalance = budgetData.getAccountsByUser(user.getUser_id());
	                for (String i : accountsBalance) {
	                    System.out.println(i);
	                }
	                break;
	            case 2:
	                List<String> debtsBalance = budgetData.getDebtsByUser(user.getUser_id());
	                for (String i : debtsBalance) {
	                    System.out.println(i);
	                }
	                break;
	            case 3:
	                services.getCurrentSavingsRate(user.getUser_id());
	                break;
	            case 4:
	                System.out.println("Choose Account Type:");
	                System.out.println("1. Checking");
	                System.out.println("2. Savings");
	                int accountTypeChoice = scanner.nextInt();
	                scanner.nextLine(); //Clear newline
	                AccountType accountType = new AccountType(accountTypeChoice, accountTypeChoice == 1 ? "Checking" : "Savings");
	                System.out.print("Enter balance: ");
	                double balance = scanner.nextDouble();
	                scanner.nextLine(); //Clear newline
	                Accounts accountToAdd = new Accounts(accountType, user.getUser_id(), balance);
	                budgetData.addAccount(accountToAdd);
	                break;
	            case 5:
	                System.out.print("Enter the account ID to delete: ");
	                int accountId = scanner.nextInt();
	                scanner.nextLine(); //Clear newline
	                budgetData.deleteAccount(accountId);
	                break;
	            case 6:
	                //Return to the main menu
	                viewAccountsMenuRunning = false;
	                break;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	        }
	    }
	}

	//Main Menu 2.
	public void addEditIncomeExpenses() {
		System.out.println("___________________________________");
        System.out.println("Add / Edit Income and Expenses Menu");
        System.out.println("1. Add Income");
        System.out.println("2. Edit Income");
        System.out.println("3. Add Custom Expense");
        System.out.println("4. Edit Expense");
        System.out.println("5. Go back to Main Menu");
	}
	//Main Menu 2.
	private void handleAddEditIncomeExpenses(User user) { 
		
		int frequencyChoice;
		String[] frequencies = {"Weekly", "Monthly", "Annually"};
		user = currentUser;
	    boolean addEditMenuRunning = true;
	    int addEditMenuChoice = 0;

	        while (addEditMenuRunning) {
	            addEditIncomeExpenses();
	            try {
	                addEditMenuChoice = scanner.nextInt();
	            } catch (InputMismatchException e) {
	                System.out.println("Invalid input. Please enter a number.");
	                scanner.next(); //Clear the invalid input
	                continue; //Skip to the next iteration to re-prompt the user
	            }
	            try {
	            switch (addEditMenuChoice) {
	            case 1:
	                String username = user.getUsername();
	                System.out.print("Enter the weekly income: ");
	                double weeklyIncome = scanner.nextDouble();

	                Income newIncome = new Income(username, weeklyIncome);
	                budgetData.addIncome(username, newIncome);

	                System.out.println("Income added successfully.");
	                break;
	            case 2:
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
	                    scanner.next(); //Clear the invalid input
	                    break; //Exit case 2 and re-prompt the user
	                }
	                System.out.print("Enter the new weekly income: ");
	                double newWeeklyIncome = scanner.nextDouble();

	                budgetData.editIncome(incomeId, newWeeklyIncome);

	                break;
	            case 3:
	                List<String> categories = Category.predefinedCategories();
	                System.out.println("__________________________________________");
	                System.out.println("Select a category for your custom expense:");
	                for (int i = 0; i < categories.size(); i++) {
	                    System.out.println((i + 1) + ". " + categories.get(i));
	                }
	                
	                int selectedCategoryIndex = scanner.nextInt();
	                scanner.nextLine();

	                if (selectedCategoryIndex < 1 || selectedCategoryIndex > categories.size()) {
	                    System.out.println("Invalid category selection.");
	                    break;
	                }
	                
	                System.out.print("Enter Custom Expense Name: ");
	                String customExpenseName = scanner.nextLine();
	                
	                System.out.print("Enter Amount: ");
	                double amount = scanner.nextDouble();
	                scanner.nextLine();
	                
	                System.out.println("Select Frequency:\n1. Weekly\n2. Monthly\n3. Annually");
	                frequencyChoice = scanner.nextInt() - 1;
	                scanner.nextLine();

	                if (frequencyChoice < 0 || frequencyChoice >= frequencies.length) {
	                    System.out.println("Invalid frequency selection.");
	                    break;
	                }

	                int categoryId = budgetData.getCategoryIdByName(categories.get(selectedCategoryIndex - 1));
	                Expense expense = new Expense(user.getUser_id(), customExpenseName, amount, frequencies[frequencyChoice], categoryId);
	                budgetData.addExpense(expense, user);
	                break;
	            case 4:
	                List<Expense> userExpenses = budgetData.getExpensesByUserId(user.getUser_id());
	                if (userExpenses.isEmpty()) {
	                    System.out.println("No expenses found.");
	                    break;
	                }
	                
	                System.out.println("Your expenses:");
	                for (Expense exp : userExpenses) {
	                    System.out.println("ID: " + exp.getExpense_id() + ", Name: " + exp.getName() 
	                    + ", Amount: " + exp.getAmount() + ", Frequency: " + exp.getFrequency());
	                }
	                System.out.print("Enter Expense ID to edit: ");
	                int expenseId = scanner.nextInt();
	                scanner.nextLine(); //consume the newline

	                System.out.print("Enter New Expense Name: ");
	                String newName = scanner.nextLine();

	                System.out.print("Enter New Amount: ");
	                double newAmount = scanner.nextDouble();
	                scanner.nextLine(); //consume the newline

	                System.out.println("Select New Frequency:\n1. Weekly\n2. Monthly\n3. Annually");
	                frequencyChoice = scanner.nextInt() - 1;
	                scanner.nextLine();
	                
	                if (frequencyChoice < 0 || frequencyChoice >= frequencies.length) {
	                    System.out.println("Invalid frequency selection.");
	                    break;
	                }
	                Expense updatedExpense = new Expense();
	                updatedExpense.setExpense_id(expenseId);
	                updatedExpense.setName(newName);
	                updatedExpense.setAmount(newAmount);
	                updatedExpense.setFrequency(frequencies[frequencyChoice]);

	                budgetData.updateExpense(updatedExpense);
	                break;
	                case 5:
	                    //Return to the main menu
	                    addEditMenuRunning = false;
	                    break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input format. Please enter the correct data types.");
	            scanner.next(); //Clear the invalid input
	        	}
	        }
	    }
	//Main Menu 3.
	public void budgetAnalysisMenu() {
		System.out.println("___________________________________");
        System.out.println("Budget Analysis Menu");
        System.out.println("1. Spending rates per category");
        System.out.println("2. Spending rates per expense");
        System.out.println("3. Balance trajectory");
        System.out.println("4. Time until debt is paid");
        System.out.println("5. Ratio of Income Saved");
        System.out.println("6. Go back to Main Menu");
	}
	//Main Menu 3.
	private void handlebudgetAnalysisMenu(User user) { 
        boolean viewBudgetAnalysisRunning = true;
        int userId = user.getUser_id();
        double currentCheckingBalance = budgetData.getCheckingBalanceByUserId(userId);
        double weeklyIncome = budgetData.getTotalWeeklyIncome(userId);

        while (viewBudgetAnalysisRunning) {
        	budgetAnalysisMenu();
            int budgetAnalysisMenuChoice;
            
			try {
                budgetAnalysisMenuChoice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); //Clear the invalid input
                continue; //Skip to the next iteration to re-prompt the user
            }

			try {
            switch (budgetAnalysisMenuChoice) {
            case 1:
                //Get percentage of income spent on each expense category
                HashMap<String, Double> categoryExpensePercentages = services.getCategoryExpensePercentage(userId, weeklyIncome*4.5);
                for (HashMap.Entry<String, Double> entry : categoryExpensePercentages.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "%");
                }
                break;
            case 2:
                //Get percentage of income spent on each expense
                HashMap<String, Double> expensePercentages = services.getExpensePercentageOfWeeklyIncome(userId, weeklyIncome);
                System.out.println("Expense Percentage of Weekly Income:");
                for (HashMap.Entry<String, Double> entry : expensePercentages.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "%");
                }
                break;
            case 3:
                    //Balance trajectory
                	double[] projectedBalances = services.getProjectedBalances(userId, currentCheckingBalance);
                    System.out.println("Projected balance in 1 month: " + projectedBalances[0]);
                    System.out.println("Projected balance in 6 months: " + projectedBalances[1]);
                    System.out.println("Projected balance in 1 year: " + projectedBalances[2]);
                    break;
            case 4:
                    //View Debts and Calculate Time to Pay Off
                    List<String> debts = budgetData.getDebtsByUser(user.getUser_id());

                    if (debts.isEmpty()) {
                        System.out.println("You have no debts.");
                    } else {
                        System.out.println("Here are your debts:");
                        for (int i = 0; i < debts.size(); i++) {
                            System.out.println((i + 1) + ". " + debts.get(i));
                        }
                        
                        System.out.println("Select a debt to calculate the time to pay it off (enter the number):");
                        int selectedDebtIndex = scanner.nextInt() - 1;

                        Debts selectedDebt = budgetData.getDebtByIndex(user.getUser_id(), selectedDebtIndex);

                        if (selectedDebtIndex < 0 || selectedDebtIndex >= debts.size()) {
                            System.out.println("Invalid selection. Returning to menu.");
                            break;
                        }

                        double remainingBalance = selectedDebt.getRemaining_balance();
                        double interestRate = selectedDebt.getInterest_rate();

                        System.out.println("Enter your monthly payment towards this debt:");
                        double monthlyPayment = scanner.nextDouble();

                        String result = services.calculateTimeToPayOffDebt(remainingBalance, monthlyPayment, interestRate);
                        System.out.println(result);
                    }
                    break;
            case 5:
                    //Ratio of Income Saved
                    double weeklySavings = budgetData.getTotalWeeklySavings(user.getUser_id());
                    double percentage = services.getRatioOfIncomeSaved(weeklyIncome, weeklySavings);
                    System.out.println("Ratio of Income Saved: " + percentage + "%");
                    break;
            case 6:
                    //Return to the main menu
                	viewBudgetAnalysisRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input format. Please enter the correct data types.");
            scanner.next(); //Clear the invalid input
        	}
        }
    }
	//Main Menu 4.
	public void goalsMenu() {
		System.out.println("___________________________________");
        System.out.println("Goals Menu:");
        System.out.println("1. Add Goal");
        System.out.println("2. Update Goal");
        System.out.println("3. View Goals");
        System.out.println("4. Delete Goal");
        System.out.println("5. Return to Main Menu");
		}
	//Main Menu 4.
	private void handleGoalsMenu() { 
	        boolean viewGoalsMenuRunning = true;

	        while (viewGoalsMenuRunning) {
	        	goalsMenu();
	            int viewGoalsMenuChoice;
				try {
	                viewGoalsMenuChoice = scanner.nextInt();
	            } catch (InputMismatchException e) {
	                System.out.println("Invalid choice. Please enter a number.");
	                scanner.next(); //Clear the invalid input
	                continue; //Skip to the next iteration to re-prompt the user
	            }

				try {
	            switch (viewGoalsMenuChoice) {
                case 1:
                    //Add Goal
                    System.out.print("Enter goal name: ");
                    String name = scanner.next();
                    System.out.print("Enter goal amount: ");
                    double amount = scanner.nextDouble();
                    if (amount < 0) {
                        System.out.println("Amount must be non-negative.");
                        break;
                    }
                    System.out.print("Enter target date (yyyy-mm-dd): ");
                    String target_date = scanner.next();
                    LocalDate.parse(target_date); //Can throw DateTimeParseException if input is not a date
                    Goals newGoal = new Goals(currentUser.getUser_id(), name, amount, target_date);
                    budgetData.addGoal(newGoal);
                    break;
	            case 2:
	                //Update Goal
	                System.out.print("Enter goal ID to update: ");
	                int goal_id = scanner.nextInt();
	                System.out.print("Enter new name: ");
	                name = scanner.next();
	                System.out.print("Enter new amount: ");
	                amount = scanner.nextDouble();
	                System.out.print("Enter new target date (yyyy-mm-dd): ");
	                target_date = scanner.next();
	                Goals updatedGoal = new Goals(goal_id, currentUser.getUser_id(), name, amount, target_date);
	                budgetData.updateGoal(updatedGoal);
	                break;
	            case 3:
	                //View Goals
	                List<Goals> goals = budgetData.getGoalsByUserId(currentUser.getUser_id());
	                printAllGoals(goals);
	                break;
	            case 4:
	                //Delete Goal
	                System.out.print("Enter goal ID to delete: ");
	                goal_id = scanner.nextInt();
	                budgetData.deleteGoal(goal_id);
	                break;

	            case 5:
	                //Return to Main Menu
	                viewGoalsMenuRunning = false;
	                break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input format. Please enter the correct data types.");
	            scanner.next(); // Clear the invalid input
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid date format. Please enter a date in the format yyyy-mm-dd.");
	        	}
	        }
	    }
	
	private void printAllGoals(List<Goals> goals) {
		System.out.println("___________________________________");
	    System.out.println("Your Goals:");
	    for (Goals goal : goals) {
	        System.out.println("Goal ID: " + goal.getGoal_id());
	        System.out.println("Name: " + goal.getName());
	        System.out.println("Amount: " + goal.getAmount());
	        System.out.println("Target Date: " + goal.getTarget_date());
	    }
	}

	public User selectUser() {
	    while (true) { //Start an infinite loop
	        System.out.println("________________________________________");
	        System.out.println("Welcome! Are you a new or existing user?\n1. new\n2. existing");
	        try {
	            int choice = scanner.nextInt();
	            scanner.nextLine(); //Consume the newline

	            if (choice == 1) {
	                //Create a new user
	                try {
	                    createUser();
	                    //Setup initial expenses
	                    setupInitialExpenses();
	                    break; //Exit the loop if successful
	                } catch (UserAlreadyExistsException e) {
	                    System.out.println(e.getMessage());
	                }
	            } else if (choice == 2) {
	                //Show a list of existing users
	                List<User> userList = budgetData.getAllUsers();
	                System.out.println("Select a user:");

	                //Print the list of existing users
	                for (int i = 0; i < userList.size(); i++) {
	                    System.out.println((i + 1) + ". " + userList.get(i).getUsername());
	                }

	                //Select an existing user
	                int selectedUserIndex = scanner.nextInt();
	                scanner.nextLine();

	                if (selectedUserIndex >= 1 && selectedUserIndex <= userList.size()) {
	                    //Return the selected user
	                    currentUser = userList.get(selectedUserIndex - 1);
	                    return currentUser; //Exit the method if successful
	                } else {
	                    System.out.println("Invalid selection.");
	                }
	            } else {
	                System.out.println("Invalid choice.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input."); //Handle non-integer input
	            scanner.nextLine(); //Clear the invalid input from the scanner
	        }
	    }

	    //Return null if no user is selected or created
	    return null;
	}

	
	//Launch the user interface. Cycle through all menus until user exits
	public void run(User user) {  
		
		user = selectUser();
        boolean exitApp = false;
        
        while (!exitApp) {
            mainMenu();
            int mainMenuChoice = scanner.nextInt();
            scanner.nextLine(); //Consume the newline character

            switch (mainMenuChoice) {
                case 1:
                	//accountsSummary();
                    handleAccountsSummaryMenu(user); //Process the choice for the accounts menu
                    break;
                case 2:
                	//addEditIncomeExpenses();
                	handleAddEditIncomeExpenses(user); //Process the choice for the edit income / expenses menu    
                    break;
                case 3:
                	//budgetAnalysisMenu();
                    handlebudgetAnalysisMenu(user); //Process the choice for the budget analysis menu             
                    break;
                case 4:
                    //goalsMenu();
                    handleGoalsMenu(); //Process the choice for the goals menu    
                    break;
                case 5:
                    exitApp = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        System.out.println("Exiting Budget App. Goodbye!");
    }
	
	public static void main(String[] args) {
		BudgetData jdbc = new BudgetData();
		jdbc.testDatabaseConnection();
		
		ControllerUserInterface controllerInterface = new ControllerUserInterface(new BudgetData());
		
		controllerInterface.run(currentUser);
		
		jdbc.closeConnection();
	}
}
