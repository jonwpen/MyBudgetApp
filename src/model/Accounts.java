package model;

public class Accounts {

	private int accounts_id;
	private int account_type_id;
	private int user_id;
	private double balance;
	private AccountType accountType;
	
	public Accounts(){
		
	}
	public Accounts(int accounts_id, int account_type_id, int user_id, double balance){
		this.accounts_id = accounts_id;
		this.account_type_id = account_type_id;
		this.user_id = user_id;
		this.balance = balance;
	}
	public Accounts(AccountType accountType, int user_id, double balance){
		this.accountType = accountType;
		this.user_id = user_id;
		this.balance = balance;
	}
	
	public int getAccounts_id() {return accounts_id;}
	public void setAccounts_id(int accounts_id) {this.accounts_id = accounts_id;}
	public int getAccount_type_id() {return account_type_id;}
	public void setAccount_type_id(int account_type_id) {this.account_type_id = account_type_id;}
	public int getUser_id() {return user_id;}
	public void setUser_id(int user_id) {this.user_id = user_id;}
	public double getBalance() {return balance;}
	public void setBalance(double balance) {this.balance = balance;}
	public AccountType getAccountType() {return accountType;}
}
