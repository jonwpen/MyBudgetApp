package model;

public class Expense {

	private int expense_id;
	private int user_id4;
	private String name;
	private double amount;
	private String frequency;
	private int category_id;
	
	public Expense() {
		
	}
	public Expense(int expense_id, int user_id4, String name, double amount, String frequency, int category_id){
		
		this.expense_id = expense_id;
		this.user_id4 = user_id4;
		this.name = name;
		this.amount = amount;
		this.frequency = frequency;
		this.category_id = category_id;
	}
	public Expense(int user_id4, String name, double amount, String frequency, int category_id){
		
		this.user_id4 = user_id4;
		this.name = name;
		this.amount = amount;
		this.frequency = frequency;
		this.category_id = category_id;
	}

	public int getExpense_id() {return expense_id;}
	public void setExpense_id(int expense_id) {this.expense_id = expense_id;}
	public int getUser_id4() {return user_id4;}
	public void setUser_id4(int user_id4) {this.user_id4 = user_id4;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public double getAmount() {return amount;}
	public void setAmount(double amount) {this.amount = amount;}
	public String getFrequency() {return frequency;}
	public void setFrequency(String frequency) {this.frequency = frequency;}
	public int getCategory_id() {return category_id;}
	public void setCategory_id(int category_id) {this.category_id = category_id;}
}
