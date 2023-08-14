package model;

public class Debts {

	
	private int debt_id;
	private int expense_id;
	private double interest_rate;
	private double remaining_balance;
	
	public Debts(int debt_id, int expense_id, double interest_rate, double remaining_balance){
		
		this.debt_id = debt_id;
		this.expense_id = expense_id;
		this.interest_rate = interest_rate;
		this.remaining_balance = remaining_balance;
	}
	public Debts(int expense_id, double interest_rate, double remaining_balance){
		
		this.expense_id = expense_id;
		this.interest_rate = interest_rate;
		this.remaining_balance = remaining_balance;
	}
	public int getDebt_id() {
		return debt_id;
	}

	public void setDebt_id(int debt_id) {
		this.debt_id = debt_id;
	}

	public int getExpense_id() {
		return expense_id;
	}

	public void setExpense_id(int expense_id) {
		this.expense_id = expense_id;
	}

	public double getInterest_rate() {
		return interest_rate;
	}

	public void setInterest_rate(double interest_rate) {
		this.interest_rate = interest_rate;
	}

	public double getRemaining_balance() {
		return remaining_balance;
	}

	public void setRemaining_balance(double remaining_balance) {
		this.remaining_balance = remaining_balance;
	}
}
