package model;

public class Savings {

	private int savings_id;
	private int account_id;
	private double weekly_savings;
	
	public Savings(int account_id, double weekly_savings){
		this.account_id = account_id;
		this.weekly_savings = weekly_savings;
	}
	public Savings(double weekly_savings){
		this.weekly_savings = weekly_savings;
	}

	public int getSavings_id() {
		return savings_id;
	}

	public void setSavings_id(int savings_id) {
		this.savings_id = savings_id;
	}

	public int getAccount_id() {
		return account_id;
	}

	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}

	public double getWeekly_savings() {
		return weekly_savings;
	}

	public void setWeekly_savings(double weekly_savings) {
		this.weekly_savings = weekly_savings;
	}
}
