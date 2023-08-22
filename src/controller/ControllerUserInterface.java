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
    ControllerHelper controllerHelper;

    public ControllerUserInterface(BudgetData budgetData) {
        this.budgetData = budgetData; 
        scanner = new Scanner(System.in);
        services = new BudgetServices(budgetData, null); //Initialize services first, temporarily pass null for controllerHelper
        controllerHelper = new ControllerHelper(budgetData, services); //Then create controllerHelper
        services.setControllerHelper(controllerHelper); //Lastly, set the controllerHelper in services
    }
	
	public User createUser() throws UserAlreadyExistsException, IllegalArgumentException {
	    System.out.println("Creating a new user.");

	    String firstName = ControllerHelper.readString(scanner, "Enter first name:");
	    if (firstName.trim().isEmpty()) {
	        throw new IllegalArgumentException("First name cannot be empty.");
	    }
	    String lastName = ControllerHelper.readString(scanner, "Enter last name:");
	    if (lastName.trim().isEmpty()) {
	        throw new IllegalArgumentException("Last name cannot be empty.");
	    }
	    String username = ControllerHelper.readString(scanner, "Enter username:");
	    if (username.trim().isEmpty()) {
	        throw new IllegalArgumentException("Username cannot be empty.");
	    }
	    String email = ControllerHelper.readString(scanner, "Enter email:");
	    if (email.trim().isEmpty()) {
	        throw new IllegalArgumentException("Email cannot be empty.");
	    }
	    //Check if the user already exists
	    if (budgetData.isUserExists(username, email)) {
	        throw new UserAlreadyExistsException("User with the given username or email already exists");
	    }
	    
	    currentUser = new User(firstName, lastName, username, email); //Create a new User object with the provided information
	    budgetData.addUser(currentUser); //Insert the user into the database using the BudgetData class method

	    try {
	    	double weeklyIncome = ControllerHelper.readDouble(scanner, "Enter weekly income:");
	        Income income = new Income(currentUser.getUsername(), weeklyIncome);
	        budgetData.addIncome(currentUser.getUsername(), income);

	        double checkingBalance = ControllerHelper.readDouble(scanner, "Enter a checking account balance:");

	        AccountType checkingType = new AccountType("Checking");
	        int checkingTypeId = budgetData.getAccountTypeId(checkingType.getType_name()); // Get the account_type_id
	        checkingType.setAccount_type_id(checkingTypeId);
	        Accounts checkingAccount = new Accounts(checkingType, budgetData.getUserId(currentUser), checkingBalance);

	        budgetData.addAccount(checkingAccount);
	        
	        controllerHelper.setupInitialExpenses(scanner, currentUser);

	    } catch (InputMismatchException e) {
	        scanner.nextLine();
	        throw new IllegalArgumentException("Invalid input. Please enter a valid number where required.");
	    }
	    System.out.println("New user created successfully.");
	    return currentUser;
	}

	public void mainMenu() {
		System.out.println("___________________________________");
        System.out.println("Main Menu");
        System.out.println("1. View Accounts");
        System.out.println("2. Add / Edit Income and Expenses");
        System.out.println("3. Budget Analysis");
        System.out.println("4. Create / View Goals");
        System.out.println("5. Exit");
	}
	
	//Main Menu option 1.
	public void accountsSummary() {
	    System.out.println("___________________________________");
	    System.out.println("Accounts Menu");
	    System.out.println("1. Account ID and Balances");
	    System.out.println("2. Debt");
	    System.out.println("3. Current Savings Rate");
	    System.out.println("4. Add Account");
	    System.out.println("5. Delete Account");
	    System.out.println("6. Update Account Balance");
	    System.out.println("7. Update Debt Monthly Payment");
	    System.out.println("8. Go back to Main Menu");
	}
	
	//Main Menu option 1.
	private void handleAccountsSummaryMenu(User user) {
	    boolean viewAccountsMenuRunning = true;

	    while (viewAccountsMenuRunning) {
	        accountsSummary();
	        int viewAccountsMenuChoice = 0;

	        try {
	            viewAccountsMenuChoice = scanner.nextInt();
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next();
	            continue; //Skip to the next iteration to re-prompt the user
	        }

	        switch (viewAccountsMenuChoice) {
	            case 1: //Account ID and Balances
	                List<String> accountsBalance = budgetData.getAccountsByUser(user.getUser_id());
	                for (String i : accountsBalance) {
	                    System.out.println(i);
	                }
	                break;
	                
	            case 2: //view debts
	                List<String> debtsBalance = budgetData.getDebtsByUser(user.getUser_id());
	                for (String i : debtsBalance) {
	                    System.out.println(i);
	                }
	                break;
	                
	            case 3: //current savings rate
	            	System.out.println("You are currently saving "+services.getCurrentSavingsRate(user.getUser_id())+"% of your income.");
	                break;
	                
	            case 4: //add account
	                int accountTypeChoice = ControllerHelper.promptAccountType(scanner);
	                scanner.nextLine();
	                String accountTypeName = accountTypeChoice == 1 ? "Checking" : "Savings";
	                int accountTypeId = budgetData.getAccountTypeId(accountTypeName);
	                AccountType accountType = new AccountType(accountTypeId, accountTypeName);
	                double balance = ControllerHelper.readDouble(scanner, "Enter balance: ");
	                Accounts accountToAdd = new Accounts(accountType, user.getUser_id(), balance);
	                budgetData.addAccount(accountToAdd);

	                if (accountTypeChoice == 2) {
	                    double weeklySavings = ControllerHelper.readDouble(scanner, "Enter weekly savings amount: ");
	                    int accountId = budgetData.getAccountIdByUserIdAndType(user.getUser_id(), accountTypeId);
	                    if (accountId != -1) {
	                        budgetData.createSavings(accountId, weeklySavings);
	                    } else {
	                        System.out.println("Failed to find the account ID for the newly created savings account.");
	                    }
	                }
	                break;

	            case 5: //delete account
	                System.out.println("Available accounts:");
	                List<String> userAccounts = budgetData.getAccountsByUser(user.getUser_id());
	                ControllerHelper.printList(userAccounts);
	                int accountId = ControllerHelper.readInt(scanner, "Enter the account ID to delete: ");
	                boolean accountIdFound = false;

	                for (String account : userAccounts) {
	                    if (account.contains("Account ID: " + accountId + " ")) {
	                        accountIdFound = true;
	                        break;
	                    }
	                }

	                if (accountIdFound) {
	                    budgetData.deleteAccount(accountId);
	                } else {
	                    System.out.println("The account ID does not belong to this user.");
	                }
	                break;
	                
	            case 6: //update account balance
	            	ControllerHelper.printList(budgetData.getAccountsByUser(user.getUser_id()));
	                userAccounts = budgetData.getAccountsByUser(user.getUser_id());
	                int updateAccountId = ControllerHelper.readValidAccountId(scanner, userAccounts);

	                if (updateAccountId == 0) {
	                    System.out.println("Returning to accounts menu.");
	                    break;
	                }

	                double newBalance = ControllerHelper.readDouble(scanner, "Enter the new balance: ");

	                Accounts accountToUpdate = new Accounts(); //Use default constructor
	                accountToUpdate.setAccounts_id(updateAccountId);
	                accountToUpdate.setBalance(newBalance);
	                budgetData.updateAccount(accountToUpdate);
	                break;
	                
	            case 7: //update debt monthly payment
	                List<String> userDebts = budgetData.getDebtsByUser(user.getUser_id());
	                for (int i = 0; i < userDebts.size(); i++) {
	                    System.out.println((i + 1) + ". " + userDebts.get(i));
	                }
	                
	                int debtNumber = ControllerHelper.readInt(scanner, "Enter the number of the debt to update: ") - 1;

	                if (debtNumber < 0 || debtNumber >= userDebts.size()) {
	                    System.out.println("Invalid selection. Returning to accounts menu.");
	                    break;
	                }

	                int monthlyPayment = ControllerHelper.readInt(scanner, "Enter the new monthly payment: ");

	                Debts debtToUpdate = budgetData.getDebtByIndex(user.getUser_id(), debtNumber);
	                if (debtToUpdate != null && budgetData.updateDebt(debtToUpdate.getDebt_id(), monthlyPayment)) {
	                    System.out.println("Monthly payment updated successfully.");
	                } else {
	                    System.out.println("Failed to update monthly payment.");
	                }
	                break;
	                
	            case 8:  //Return to the main menu
	                viewAccountsMenuRunning = false;
	                break;
	                
	            default:
	                System.out.println("Invalid choice. Please try again.");
	        }
	    }
	}
	
	//Main Menu option 2.
	public void addEditIncomeExpenses() {
	    System.out.println("___________________________________");
	    System.out.println("Add / Edit Income and Expenses Menu");
	    System.out.println("1. Add Income");
	    System.out.println("2. Edit Income");
	    System.out.println("3. Add Custom Expense");
	    System.out.println("4. Edit Expense");
	    System.out.println("5. View All Expenses");
	    System.out.println("6. Go back to Main Menu");
	}
	
	//Main Menu option 2.
	private void handleAddEditIncomeExpenses(User user) { 
		
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
	                scanner.next(); 
	                continue; //Skip to the next iteration to re-prompt the user
	            }
	            try {
	            switch (addEditMenuChoice) {
	            case 1: //add income
	                String username = user.getUsername();
	                System.out.print("Enter the weekly income: ");
	                double weeklyIncome = scanner.nextDouble();

	                Income newIncome = new Income(username, weeklyIncome);
	                budgetData.addIncome(username, newIncome);
	                break;
	                
	            case 2: //Edit income
	                ControllerHelper.editIncome(budgetData, user, scanner);
	                break;
	                
	            case 3: //Add custom expense
	                ControllerHelper.addCustomExpense(budgetData, user, scanner, frequencies);
	                break;
	                
	            case 4: //Edit expense
	                ControllerHelper.editExpense(budgetData, user, scanner, frequencies);
	                break;
	                
	            case 5: //View all expenses
	                List<Expense> userExpenses = budgetData.getExpensesByUserId(user.getUser_id());
	                if (userExpenses.isEmpty()) {
	                    System.out.println("No expenses found.");
	                } else {
	                    System.out.println("Your expenses:");
	                    for (Expense exp : userExpenses) {
	                        System.out.println("ID: " + exp.getExpense_id() + ", Name: " + exp.getName()
	                                + ", Amount: " + exp.getAmount() + ", Frequency: " + exp.getFrequency());
	                    }
	                }
	                break;
	                
                case 6: //Return to the main menu
                    addEditMenuRunning = false;
                    break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input format. Please enter the correct data types.");
	            scanner.next(); 
	        	}
	        }
	    }
	
	//Main Menu option 3.
	public void budgetAnalysisMenu() {
		System.out.println("___________________________________");
        System.out.println("Budget Analysis Menu");
        System.out.println("1. Spending rates per category (monthly)");
        System.out.println("2. Spending rates per expense (weekly)");
        System.out.println("3. Balance trajectory");
        System.out.println("4. Time until debt is paid");
        System.out.println("5. Ratio of Income Saved");
        System.out.println("6. Comprehensive Financial Sanity Report.");
        System.out.println("7. Go back to Main Menu");
	}
	
	//Main Menu option 3.
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
                scanner.next(); 
                continue; //Skip to the next iteration to re-prompt the user
            }

			try {
            switch (budgetAnalysisMenuChoice) {
            case 1: //Get percentage of income spent on each expense category
                HashMap<String, Double> categoryExpensePercentages = services.getCategoryExpensePercentage(userId, weeklyIncome*4.5);
                for (HashMap.Entry<String, Double> entry : categoryExpensePercentages.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "%");
                }
                break;
                
            case 2:  //Get percentage of income spent on each expense
                HashMap<String, Double> expensePercentages = services.getExpensePercentageOfWeeklyIncome(userId, weeklyIncome);
                System.out.println("Expense Percentage of Weekly Income:");
                for (HashMap.Entry<String, Double> entry : expensePercentages.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "%");
                }
                break;
                
            case 3: //Balance trajectory
                double[] projectedBalances = services.getProjectedBalances(userId, currentCheckingBalance);
                System.out.println("Projected balance in 1 month: " + projectedBalances[0]);
                System.out.println("Projected balance in 6 months: " + projectedBalances[1]);
                System.out.println("Projected balance in 1 year: " + projectedBalances[2]);
                break;
            case 4: // View Debts and Calculate Time to Pay Off
            	List<String> debtSummary = controllerHelper.viewAndCalculateDebts(budgetData, user);
            	for (String line : debtSummary) {
            	    System.out.println(line);
            	}
                break;

            case 5:  //Ratio of Income Saved
                double weeklySavings = budgetData.getTotalWeeklySavings(user.getUser_id());
                double percentage = services.getRatioOfIncomeSaved(weeklyIncome, weeklySavings);
                System.out.println("Ratio of Income Saved: " + percentage + "%");
                break;
                
            case 6: //Generate a comprehensive report and write it to a text file
                try {
                    services.generateFinancialSanityReport(user, scanner);
                } catch (Exception e) {
                    //user-friendly message
                    System.out.println(e.getMessage());
                }
                break;
                
            case 7: //Return to the main menu
                viewBudgetAnalysisRunning = false;
                break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input format. Please enter the correct data types.");
            scanner.next(); 
        	}
        }
    }
	
	//Main Menu option 4.
	public void goalsMenu() {
		System.out.println("___________________________________");
        System.out.println("Goals Menu:");
        System.out.println("1. Add Goal");
        System.out.println("2. Update Goal");
        System.out.println("3. View Goals");
        System.out.println("4. Delete Goal");
        System.out.println("5. Return to Main Menu");
		}
	
	//Main Menu option 4.
	private void handleGoalsMenu() { 
	        boolean viewGoalsMenuRunning = true;

	        while (viewGoalsMenuRunning) {
	        	goalsMenu();
	            int viewGoalsMenuChoice;
				try {
	                viewGoalsMenuChoice = scanner.nextInt();
	            } catch (InputMismatchException e) {
	                System.out.println("Invalid choice. Please enter a number.");
	                scanner.next(); 
	                continue; //Skip to the next iteration to re-prompt the user
	            }

				try {
	            switch (viewGoalsMenuChoice) {
	            
	            case 1: //Add Goal
	                scanner.nextLine();
	                String name = ControllerHelper.readString(scanner, "Enter goal name: ");
	                double amount = ControllerHelper.readDouble(scanner, "Enter goal amount: ");
	                if (amount < 0) {
	                    System.out.println("Amount must be non-negative.");
	                    break;
	                }
	                String target_date = ControllerHelper.readString(scanner, "Enter target date (yyyy-mm-dd): ");
	                LocalDate.parse(target_date); //Can throw DateTimeParseException if input is not a date
	                double remaining_amount = amount; //Default remaining amount to the total goal amount
	                Goals newGoal = new Goals(currentUser.getUser_id(), name, amount, target_date, remaining_amount);
	                budgetData.addGoal(newGoal);
	                break;
	                
	            case 2: //Update Goal
	                int goal_id;
	                while (true) {
	                    goal_id = ControllerHelper.readInt(scanner, "Enter goal ID to update (or 0 to go back): ");
	                    if (goal_id == 0) {
	                        System.out.println("Returning to goals menu.");
	                        break; //exit and re-prompt the user
	                    }

	                    Goals existingGoal = budgetData.getGoalById(goal_id);
	                    if (existingGoal == null) {
	                        System.out.println("Goal not found with the provided ID.");
	                        continue; //re-prompt the user for a new ID
	                    }

	                    double remainingAmount = ControllerHelper.readDouble(scanner, "Enter new remaining amount: ");
	                    target_date = ControllerHelper.readString(scanner, "Enter new target date (yyyy-mm-dd): ");
	                    // Use existing goal amount instead of prompting the user
	                    Goals updatedGoal = new Goals(goal_id, currentUser.getUser_id(), existingGoal.getName(), existingGoal.getAmount(), target_date, remainingAmount);
	                    budgetData.updateGoal(updatedGoal);
	                    break;
	                }
	                break;

	            case 3: //View Goals
	                List<Goals> goals = budgetData.getGoalsByUserId(currentUser.getUser_id());
	                System.out.println("___________________________________");
	                if (goals.isEmpty()) {
	                	String goalsOutput = ControllerHelper.printAllGoals(goals);
	                    System.out.print(goalsOutput);
	                    System.out.println("No goals? No problem! Now you have time to ponder the meaning of life. Or binge-watch a new series.");
	                } else {
	                    String goalsOutput = ControllerHelper.printAllGoals(goals);
	                    System.out.print(goalsOutput);
	                }
	                break;
	                
	            case 4: //Delete Goal
	                while (true) {
	                    goal_id = ControllerHelper.readInt(scanner, "Enter goal ID to delete (or 0 to go back): ");
	                    if (goal_id == 0) {
	                        System.out.println("Returning to goals menu.");
	                        break; //exit and re-prompt the user
	                    }
	                    budgetData.deleteGoal(goal_id);
	                    break;
	                }
	                break;
	                
	            case 5: //Return to Main Menu
	                viewGoalsMenuRunning = false;
	                break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input format. Please enter the correct data types.");
	            scanner.next(); 
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid date format. Please enter a date in the format yyyy-mm-dd.");
	        	}
	        }
	    }
	
	public User selectUser() {
	    while (true) { //Start an infinite loop until boolean flips
	        System.out.println("________________________________________");
	        System.out.println("Welcome! Are you a new or existing user?\n1. new\n2. existing");
	        try {
	            int choice = scanner.nextInt();
	            scanner.nextLine(); //Consume the newline

	            if (choice == 1) {
	            	try {
	            	    createUser(); //Create a new user
	            	    
	            	} catch (UserAlreadyExistsException e) {
	            	    System.out.println(e.getMessage());
	            	} catch (IllegalArgumentException e) {
	            	    System.out.println(e.getMessage());
	            	} catch (Exception e) {
	            	    System.out.println("An unexpected error occurred while creating the user. Please try again.");
	            	}

	            } else if (choice == 2) {
	                //Show a list of existing users
	                List<User> userList = budgetData.getAllUsers();
	                System.out.println("Select a user:");

	                if (userList.isEmpty()) {
	                    System.out.println("Looks like a ghost town in here. Time to populate our virtual world with a new user!");
	                    continue; //Go back to the start of the loop
	                }
	                
	                //Print the list of existing users
	                for (int i = 0; i < userList.size(); i++) {
	                    System.out.println((i + 1) + ". " + userList.get(i).getUsername());
	                }

	                int selectedUserIndex = scanner.nextInt();  //Select an existing user
	                scanner.nextLine();

	                if (selectedUserIndex >= 1 && selectedUserIndex <= userList.size()) {
	                    currentUser = userList.get(selectedUserIndex - 1); //Return the selected user
	                    return currentUser; //Exit the method if successful
	                } else {
	                    System.out.println("Invalid selection.");
	                }
	            } else {
	                System.out.println("Invalid choice.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input."); //Handle non-integer input
	            scanner.nextLine(); 
	        }
	    }
	}
	
	public void run(User user) {  
		
		user = selectUser();
        boolean exitApp = false;
        
        while (!exitApp) {
            mainMenu();
            int mainMenuChoice = scanner.nextInt();
            scanner.nextLine(); //Consume the newline character

            switch (mainMenuChoice) {
                case 1:
                    handleAccountsSummaryMenu(user); //Process the choice for the accounts menu
                    break;
                case 2:
                	handleAddEditIncomeExpenses(user); //Process the choice for the edit income / expenses menu    
                    break;
                case 3:
                    handlebudgetAnalysisMenu(user); //Process the choice for the budget analysis menu             
                    break;
                case 4:
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
	    jdbc.testDatabaseConnection(); //connect to the database
	    ControllerUserInterface controllerInterface = new ControllerUserInterface(jdbc); // Pass the same BudgetData instance
	    jdbc.ensureCategoriesArePopulated(); //If the table is empty, populate the 'category' database table with predefined expense categories
	    jdbc.ensureAccountTypesArePopulated(); //If the table is empty, populate the 'account_type' database table with predefined account types
	    
	    controllerInterface.run(currentUser); //Launch the user interface. Cycle through all menus until the user exits
	    
	    jdbc.closeConnection();
	}

}
