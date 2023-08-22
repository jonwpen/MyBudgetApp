

# Budget Tracker

Welcome to Budget Tracker, a CLI-based budgeting tool designed for personal finance management. Created as a capstone project for Coding Nomads' Java Programming course, this application offers features such as account tracking, income and expense management, budget analysis, goal setting, and standout functionalities like a Comprehensive Financial Sanity Report. In addition to these features, I've incorporated user-friendly error handling and custom exceptions to ensure a smooth user experience. I've also implemented file I/O capabilities for data persistence, and integrated appropriate data structures like HashMaps for efficient financial calculations. Additionally, I've focused on creating reusable and decoupled code, promoting modularity and maintainability throughout the application. Get started with Budget Tracker for a straightforward and insightful look at your finances.

## Features

### Main Menu

-   **View Accounts:** Dive into your financial accounts, debts, savings
    rate, and more.
-   **Add / Edit Income and Expenses:** Record your income, manage
    custom expenses, and keep track of where your money is going.
-   **Budget Analysis:** Get comprehensive insights into your spending
    habits, balance trajectory, debt payoff, savings ratio, and generate
    a Financial Sanity Report.
-   **Create / View Goals:** Set, view, update, and delete financial
    goals. Keep track of your progress towards a financially secure
    future.
-   **Exit:** Safely close the application.

### Accounts Menu

Manage your accounts by viewing balances, debts, savings rates, and even
adding or deleting accounts.

### Add / Edit Income and Expenses Menu

Add, edit, or view income and expenses to maintain an accurate record of
your financial activities.

### Budget Analysis Menu

Analyze your budget with detailed insights into spending rates, balance
trajectory, debt payoff time, savings ratio, and a Comprehensive
Financial Sanity Report saved to a text file.

### Goals Menu

Create, update, view, or delete financial goals to align your budgeting
practices with your long-term financial objectives.

## Installation and Setup

1. **Clone the repository or download the ZIP file.**
2. **Open the project in your preferred Java IDE.**
3. **Ensure MySQL is installed and configured properly.**
4. **Configure Database Connection:**
   - The application retrieves the database password from an environment variable named `DB_PASSWORD`. Wherever you will be running the application, set this environment variable in your system to match your MySQL password:
     - **Windows**: In Command Prompt, run `set DB_PASSWORD=YourPasswordHere`.
     - **Linux/Mac**: In the terminal, run `export DB_PASSWORD=YourPasswordHere`.
     - **Inside IntelliJ IDEA**:
       1. Open the 'Run/Debug Configurations' dialog by clicking on 'Edit Configurations...' in the top right corner near the run buttons.
       2. Select the configuration for your project in the left pane.
       3. Go to the 'Environment variables' field and click on the '...' button.
       4. Click the '+' button to add a new environment variable.
       5. Enter 'DB_PASSWORD' for the name and your database password for the value.
       6. Click 'OK' to close the dialogs, and then run the application as usual.
     - **Inside Eclipse**:
       1. Right-click on the project in the 'Project Explorer' or 'Package Explorer'.
       2. Select 'Run As' > 'Run Configurations...'.
       3. In the left pane, select 'Java Application' and the configuration for your project.
       4. Go to the 'Environment' tab.
       5. Click 'New...' to add a new environment variable.
       6. Enter 'DB_PASSWORD' for the name and your database password for the value.
       7. Click 'OK', then 'Apply', and then 'Run' to launch the application.
   - **Note**: The default database username in the code is set to 'root'. If you are using a different username for MySQL, make sure to update the 'USERNAME' constant in the 'BudgetData' class to match your MySQL configuration.
   - Open the `BudgetData` class to review the connection details. Modify the `USERNAME` and `DB_NAME` constants if needed to match your MySQL configuration.
5. **Use the included SQL dump to initialize the database schema.**
6. **Build and run the application:**
   - Find the main method (`public static void main`) inside the `ControllerUserInterface` class.
   - Run this method to launch the application.

Note: The database password must be set in the environment each time you start a new terminal session, or you can add the export/set command to your shell profile (e.g., `.bashrc`, `.bash_profile`) to make it permanent.


## Usage

Navigate through the CLI interface using the numerical options provided.
Input your financial data as prompted, and enjoy the seamless management
and analysis of your personal budget.

### What I Learned

- **Starting Small**: Initially being overwhelmed by the complexity of the project, I realized the importance of starting with a Minimal Viable Product, focusing on one to three simple CRUD methods before expanding.
  
- **Code Organization**: Collapsing methods is very helpful for viewing class structure, especially when a class spans several hundred lines of code.

- **Iterative Testing**: It's essential to test the code continuously rather than waiting until the end. Errors may not be immediately apparent, but they can lead to significant issues later on and even massive design overhauls.

- **Code Refactoring and Decoupling**: Utilizing practices like the DRY (Don't Repeat Yourself) method, decomposing code, and extracting logic into separate methods helped me create a more maintainable codebase. The scale of this project really drove 
this point home for me.

- **Project Complexity and Planning**: I learned that a project can grow exponentially in complexity without a clear outline, emphasizing the need for proper planning. I realize this is still an area for improvement.

## Dependencies

-   Java (JDK 8 or higher) (This project was developed using 17.0.3)
-   MySQL

## Contributing

Feel free to fork the project, create a feature branch, and send me a
pull request.

## Support

For any questions or support, please email jonwpen@gmail.com or open an
issue in the repository.

------------------------------------------------------------------------
