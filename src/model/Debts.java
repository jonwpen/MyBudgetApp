package model;

public class Debts {

	private int debt_id;
	private int expense_id;
	private double interest_rate;
	private double remaining_balance;
	private double monthlyPayment;
	private int monthly_due_date;
	
	public Debts(int debt_id, int expense_id, double interest_rate, double remaining_balance, double monthlyPayment, int monthly_due_date){
		
		this.debt_id = debt_id;
		this.expense_id = expense_id;
		this.interest_rate = interest_rate;
		this.remaining_balance = remaining_balance;
		this.monthlyPayment = monthlyPayment;
		this.monthly_due_date = monthly_due_date;
	}
	public Debts(int expense_id, double interest_rate, double remaining_balance, double monthlyPayment, int monthly_due_date){
		
		this.expense_id = expense_id;
		this.interest_rate = interest_rate;
		this.remaining_balance = remaining_balance;
		this.monthlyPayment = monthlyPayment;
		this.monthly_due_date = monthly_due_date;
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
	public double getMonthlyPayment() {
		return monthlyPayment;
	}
	public void setMonthlyPayment(int monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}
	public int getPayment_date() {
		return monthly_due_date;
	}
	public void setPayment_date(int monthly_due_date) {
		this.monthly_due_date = monthly_due_date;
	}
	
}
