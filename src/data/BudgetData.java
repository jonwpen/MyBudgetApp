package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.AccountType;
import model.Accounts;
import model.Category;
import model.Debts;
import model.Expense;
import model.Goals;
import model.Income;
import model.User;

public class BudgetData {
    private Connection connection;

    public BudgetData() {
        //Initialize the database connection using JDBC
        String url = "jdbc:mysql://localhost/budget_app_db?user=root&password=password&useSSL=false&allowPublicKeyRetrieval=true";
        
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void testDatabaseConnection() {
        try {
        	String url = "jdbc:mysql://localhost/budget_app_db?user=root&password=password&useSSL=false&allowPublicKeyRetrieval=true";
            Connection connection = DriverManager.getConnection(url);

            System.out.println("Database connected successfully");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    //Create (Insert) methods
    public void addUser(User user) {
        String sqlStatement = "INSERT INTO users (first_name, last_name, username, email) VALUES (?, ?, ?, ?)";
        //PreparedStatement is used to avoid SQL injection
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
        	//After a new POJO instance is created from user input, those fields are added to the database here
            preparedStatement.setString(1, user.getFirst_name());
            preparedStatement.setString(2, user.getLast_name());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getEmail());

            //Executes PreparedStatement. Use INSERT query to add the user to the database
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User added.");
            } else {
                System.out.println("Failed to add user.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding user: " + e.getMessage());
        }
    }

    public void addAccount(Accounts account) {
        String sqlStatement = "INSERT INTO accounts (account_type_id, user_id, balance) VALUES (?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, account.getAccountType().getAccount_type_id());
            preparedStatement.setInt(2, account.getUser_id());
            preparedStatement.setDouble(3, account.getBalance());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account added.");
            } else {
                System.out.println("Failed to add account.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding account: " + e.getMessage());
        }
    }
    
    public void addIncome(String username, Income income) {
        if (username == null || username.trim().isEmpty() || income == null) {
            System.out.println("Invalid input. Please provide a valid username and income details.");
            return;
        }

        String sqlStatement = "INSERT INTO Income (user_id2, weekly_income) VALUES (?, ?)";
        //Get user to associate with this income
        User user = getUserByUsername(username);
        int user_id2 = getUserId(user);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, user_id2);
            preparedStatement.setDouble(2, income.getWeekly_income());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Income added.");
            } else {
                System.out.println("Failed to add Income.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding Income: " + e.getMessage());
        }
    }

    public void addExpense(Expense expense, User user) {
        if (expense == null) {
            System.out.println("Invalid input. Please provide valid expense details.");
            return;
        }

        if (expense.getName() == null || expense.getName().trim().isEmpty()) {
            System.out.println("Expense name must not be null or empty.");
            return;
        }

        if (expense.getAmount() <= 0) {
            System.out.println("Expense amount must be a positive number.");
            return;
        }

        String sqlStatement = "INSERT INTO expenses (user_id4, name, amount, frequency, category_id) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, getUserId(user));
            preparedStatement.setString(2, expense.getName());
            preparedStatement.setDouble(3, expense.getAmount());
            preparedStatement.setString(4, expense.getFrequency());
            preparedStatement.setInt(5, expense.getCategory_id()); 

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Expense added.");
            } else {
                System.out.println("Failed to add expense.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding expense: " + e.getMessage());
        }
    }

    public void addGoal(Goals goal) {
        if (goal == null) {
            System.out.println("Invalid input. Please provide valid goal details.");
            return;
        }

        if (goal.getName() == null || goal.getName().trim().isEmpty()) {
            System.out.println("Goal name must not be null or empty.");
            return;
        }

        if (goal.getAmount() <= 0) {
            System.out.println("Goal amount must be a positive number.");
            return;
        }

        String sqlStatement = "INSERT INTO goals (user_id5, name, amount, target_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, goal.getUser_id5());
            preparedStatement.setString(2, goal.getName());
            preparedStatement.setDouble(3, goal.getAmount());
            preparedStatement.setString(4, goal.getTarget_date());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Goal added.");
            } else {
                System.out.println("Failed to add goal.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding goal: " + e.getMessage());
        }
    }

    public boolean isUserExists(String username, String email) {

        //Query to check if a user with the given username or email exists
        String query = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            //If the result set contains a count greater than 0, then the user exists
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("An error occurred while checking for user.");
        }

        //Default to returning false if no user was found or an error occurred
        return false;
    }
    
    //Read methods
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Invalid username provided.");
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            //Set the value for the placeholder in the SQL query
            preparedStatement.setString(1, username);

            //Use SELECT query to retrieve the user by username
            ResultSet resultSet = preparedStatement.executeQuery();

            //Check if the result set contains any rows
            if (resultSet.next()) {
                //If a user is found, create a User object and map the data
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String email = resultSet.getString("email");

                //Create a User object with the retrieved data
                User user = new User(first_name, last_name, username, email);

                //Return the User object
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        //If no user is found, return null
        return null;
    }

    public int getUserId(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("Invalid user provided.");
            return 0;
        }

        String sql = "SELECT user_id FROM users WHERE username = ?";
        int userId = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user ID: " + e.getMessage());
        }

        return userId;
    }

    public List<Income> getIncomesByUser(int userId) {
        List<Income> incomes = new ArrayList<>();
        String sqlStatement = "SELECT income_id, weekly_income FROM income WHERE user_id2 = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int incomeId = resultSet.getInt("income_id");
                double weeklyIncome = resultSet.getDouble("weekly_income");

                Income income = new Income(incomeId, userId, weeklyIncome);
                incomes.add(income);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving incomes for user: " + e.getMessage());
        }

        return incomes;
    }
    
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");

                User user = new User(userId, first_name, last_name, username, email);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return userList;
    }
    
    public Accounts getAccountById(int accountId) { 
        String sql = "SELECT * FROM accounts WHERE accounts_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int accountTypeId = resultSet.getInt("account_type_id");
                int userId = resultSet.getInt("user_id");
                double balance = resultSet.getDouble("balance");

                Accounts account = new Accounts(accountId, accountTypeId, userId, balance);

                return account;
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving account: " + e.getMessage());
        }
        return null;
    }

    public double getCheckingBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM accounts WHERE user_id = ? AND account_type_id = 1"; //id is 1 for checking
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving checking account balance: " + e.getMessage());
        }
        return 0; //Return a default value if no account found
    }
    
    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sqlStatement = "SELECT * FROM expenses WHERE user_id4 = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, userId);

            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("expense_id"),
                    rs.getInt("user_id4"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("frequency"),
                    rs.getInt("category_id")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving expenses: " + e.getMessage());
        }

        return expenses;
    }
    
    public List<String> getCommonExpenses() {
        List<String> commonExpenses = new ArrayList<>();
        String query = "SELECT name FROM category";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                commonExpenses.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching common expenses: " + e.getMessage());
        }

        return commonExpenses;
    }

    public Category getCategoryById(int categoryId) {
        Category category = null;
        String sqlStatement = "SELECT * FROM category WHERE category_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, categoryId);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                category = new Category(
                    rs.getInt("category_id"),
                    rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving category: " + e.getMessage());
        }

        return category;
    }
    
    public int getCategoryIdByName(String categoryName) {
        String query = "SELECT category_id FROM category WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categoryName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("category_id");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving category ID: " + e.getMessage());
        }
        return -1; // Return -1 if the category ID could not be retrieved
    }

    public List<Goals> getGoalsByUserId(int userId) {
        List<Goals> goals = new ArrayList<>();
        String sqlStatement = "SELECT * FROM goals WHERE user_id5 = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, userId);

            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                Goals goal = new Goals(
                    rs.getInt("goal_id"),
                    rs.getInt("user_id5"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("target_date")
                );
                goals.add(goal);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving goals: " + e.getMessage());
        }

        return goals;
    }

    public List<String> getAccountsByUser(int userId) {
        List<String> userAccounts = new ArrayList<>();

        String sql = "SELECT a.accounts_id, a.account_type_id, a.user_id, a.balance, at.type_name "
                   + "FROM accounts a "
                   + "JOIN account_type at ON a.account_type_id = at.account_type_id "
                   + "WHERE a.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int accountId = resultSet.getInt("accounts_id");
                int accountTypeId = resultSet.getInt("account_type_id");
                double balance = resultSet.getDouble("balance");
                String typeName = resultSet.getString("type_name");
                String typeAndBalance = typeName +" balance: "+ balance;

                AccountType accountType = new AccountType(accountTypeId, typeName);
                Accounts account = new Accounts(accountId, accountType.getAccount_type_id(), userId, balance);

                userAccounts.add(typeAndBalance);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving accounts: " + e.getMessage());
        }

        return userAccounts;
    }
    
    public List<String> getDebtsByUser(int userId) {
        List<String> userDebts = new ArrayList<>();

        String sql = "SELECT d.debt_id, d.expense_id, d.interest_rate, d.remaining_balance, e.name "
                   + "FROM debts d "
                   + "JOIN expenses e ON d.expense_id = e.expense_id "
                   + "WHERE e.user_id4 = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int debtId = resultSet.getInt("debt_id");
                int expenseId = resultSet.getInt("expense_id");
                double interestRate = resultSet.getDouble("interest_rate");
                double remainingBalance = resultSet.getDouble("remaining_balance");
                String expenseName = resultSet.getString("expense_name");

                String debtDetails = "Expense: " + expenseName + ", Interest Rate: " + interestRate + "%, Remaining Balance: $" + remainingBalance;
                
                userDebts.add(debtDetails);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving debts: " + e.getMessage());
        }

        return userDebts;
    }
    
    public Debts getDebtByIndex(int userId, int index) {
        List<Debts> debts = new ArrayList<>();

        String sql = "SELECT d.debt_id, d.expense_id, d.interest_rate, d.remaining_balance, e.expense_name "
                   + "FROM debts d "
                   + "JOIN expenses e ON d.expense_id = e.expense_id "
                   + "WHERE e.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int debtId = resultSet.getInt("debt_id");
                int expenseId = resultSet.getInt("expense_id");
                double interestRate = resultSet.getDouble("interest_rate");
                double remainingBalance = resultSet.getDouble("remaining_balance");

                Debts debt = new Debts(debtId, expenseId, interestRate, remainingBalance);
                debts.add(debt);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving debts: " + e.getMessage());
        }

        if (index < 0 || index >= debts.size()) {
            return null; // index out of bounds
        }
        return debts.get(index);
    }

    
    public double getTotalWeeklyIncome(int userId) {
        double totalWeeklyIncome = 0;
        String sqlIncome = "SELECT SUM(i.weekly_income) AS total_weekly_income "
                   + "FROM Income i "
                   + "WHERE i.user_id2 = ?";

        try (PreparedStatement preparedStatementIncome = connection.prepareStatement(sqlIncome)) {
            preparedStatementIncome.setInt(1, userId);
            ResultSet resultSetIncome = preparedStatementIncome.executeQuery();
            if (resultSetIncome.next()) {
                totalWeeklyIncome = resultSetIncome.getDouble("total_weekly_income");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving weekly income: " + e.getMessage());
        }

        return totalWeeklyIncome;
    }
    
    public double getTotalWeeklySavings(int userId) {

        double totalWeeklySavings = 0;
        String sqlSavings = "SELECT SUM(s.weekly_savings) AS total_weekly_savings "
                   + "FROM savings s "
                   + "JOIN accounts a ON s.account_id = a.accounts_id "
                   + "WHERE a.user_id = ?";
        
        try (PreparedStatement preparedStatementSavings = connection.prepareStatement(sqlSavings)) {
            preparedStatementSavings.setInt(1, userId);
            ResultSet resultSetSavings = preparedStatementSavings.executeQuery();
            if (resultSetSavings.next()) {
                totalWeeklySavings = resultSetSavings.getDouble("total_weekly_savings");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving weekly savings: " + e.getMessage());
        }

        return totalWeeklySavings;
    }
    
    //Update methods
    public void updateAccount(Accounts account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            //Set the values for the placeholders in the SQL query
            preparedStatement.setDouble(1, account.getBalance());
            preparedStatement.setInt(2, account.getAccounts_id());

            //Use UPDATE query to update the account in the database
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account updated successfully.");
            } else {
                System.out.println("Failed to update account.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating account: " + e.getMessage());
        }
    }

    public void updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET name = ?, amount = ?, frequency = ? WHERE expense_id = ?"; 
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, expense.getName());
            preparedStatement.setDouble(2, expense.getAmount());
            preparedStatement.setString(3, expense.getFrequency()); 
            preparedStatement.setInt(4, expense.getExpense_id()); 

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Expense updated successfully.");
            } else {
                System.out.println("Failed to update expense.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating expense: " + e.getMessage());
        }
    }

    public void updateGoal(Goals goal) {
        String sql = "UPDATE goals SET name = ?, amount = ?, target_date = ? WHERE goal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, goal.getName());
            preparedStatement.setDouble(2, goal.getAmount());
            preparedStatement.setString(3, goal.getTarget_date());
            preparedStatement.setInt(4, goal.getGoal_id()); // Set the goal_id in the WHERE clause

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Goal updated successfully.");
            } else {
                System.out.println("Failed to update goal.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating goal: " + e.getMessage());
        }
    }

    public void editIncome(int incomeId, double newWeeklyIncome) {
        String sqlStatement = "UPDATE Income SET weekly_income = ? WHERE income_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setDouble(1, newWeeklyIncome);
            preparedStatement.setInt(2, incomeId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Income updated successfully.");
            } else {
                System.out.println("Failed to update Income.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating Income: " + e.getMessage());
        }
    }
    
    //Delete methods. Foreign key constraints will cascade and delete associated data when a user is deleted.
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            //Set the value for the placeholder in the SQL query
            preparedStatement.setInt(1, userId);

            //Use DELETE query to remove the user from the database
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User deleted.");
            } else {
                System.out.println("Failed to delete user.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public void deleteAccount(int accountId) {
        String sql = "DELETE FROM accounts WHERE accounts_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account deleted.");
            } else {
                System.out.println("Failed to delete account.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    public void deleteExpense(int expenseId) {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, expenseId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Expense deleted.");
            } else {
                System.out.println("Failed to delete expense.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting expense: " + e.getMessage());
        }
    }

    public void deleteGoal(int goalId) {
        String sql = "DELETE FROM goals WHERE goal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, goalId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Goal deleted.");
            } else {
                System.out.println("Failed to delete goal.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting goal: " + e.getMessage());
        }
    }
    
    //Close database connection when controller is finished with database interactions.
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error while closing the database connection: " + e.getMessage());
        }
    }
}
