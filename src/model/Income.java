package model;

public class Income {

	private int income_id;
	private int user_id2;
	private double weekly_income;
	
	public Income(String username, double weekly_income){
		this.weekly_income = weekly_income;
	}
	public Income(int income_id, int user_id2, double weekly_income){
		this.user_id2 = user_id2;
		this.weekly_income = weekly_income;
		this.income_id = income_id;
	}

	public int getIncome_id() {return income_id;}
	public void setIncome_id(int income_id) {this.income_id = income_id;}
	public int getUser_id2() {return user_id2;}
	public void setUser_id2(int user_id2) {this.user_id2 = user_id2;}
	public double getWeekly_income() {return weekly_income;}
	public void setWeekly_income(double weekly_income) {this.weekly_income = weekly_income;}
}
