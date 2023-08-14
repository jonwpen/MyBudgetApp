package model;

public class AccountType {

	private int account_type_id;
	private String type_name;
	
	public AccountType(int account_type_id, String type_name){
		this.account_type_id = account_type_id;
		this.type_name = type_name;
	}
	public AccountType(String type_name){
		this.type_name = type_name;
	}

	public int getAccount_type_id() {
		return account_type_id;
	}

	public void setAccount_type_id(int account_type_id) {
		this.account_type_id = account_type_id;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
}
