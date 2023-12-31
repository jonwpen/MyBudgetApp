package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {

	private int category_id;
	private String name;
	
	public Category(int category_id, String name){
		this.category_id = category_id;
		this.name = name;
	}
	
	public static List<String> predefinedCategories() {
	        return new ArrayList<>(Arrays.asList(
	            "Savings and Investments",
	            "Living Expenses",
	            "Transportation",
	            "Food and Dining",
	            "Subscriptions and Memberships",
	            "Health and Wellness",
	            "Debts and Loans",
	            "Leisure"
	        ));
	    }

	public int getCategory_id() {return category_id;}
	public void setCategory_id(int category_id) {this.category_id = category_id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
}

