package model;

public class Goals {

	private int goal_id;
	private int user_id5;
	private String name;
	private double amount;
	private double remaining_amount;
	private String target_date;
	
	public Goals(int goal_id, int user_id5, String name, double amount, String target_date, double remaining_amount){
		this.goal_id = goal_id;
		this.user_id5 = user_id5;
		this.name = name;
		this.amount = amount;
		this.target_date = target_date;
		this.remaining_amount = remaining_amount;
	}
	public Goals(int user_id5, String name, double amount, String target_date, double remaining_amount){
		this.user_id5 = user_id5;
		this.name = name;
		this.amount = amount;
		this.target_date = target_date;
		this.remaining_amount = remaining_amount;
	}

	public int getGoal_id() {
		return goal_id;
	}

	public void setGoal_id(int goal_id) {
		this.goal_id = goal_id;
	}

	public int getUser_id5() {
		return user_id5;
	}

	public void setUser_id5(int user_id5) {
		this.user_id5 = user_id5;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTarget_date() {
		return target_date;
	}

	public void setTarget_date(String target_date) {
		this.target_date = target_date;
	}
	public double getRemaining_amount() {
		return remaining_amount;
	}
	public void setRemaining_amount(double remaining_amount) {
		this.remaining_amount = remaining_amount;
	}
	
}
