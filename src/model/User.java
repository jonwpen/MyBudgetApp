package model;

public class User {

	private int user_id;
	private String first_name;
	private String last_name;
	private String username;
	private String email;
	
	public User(int user_id, String first_name,String last_name,String username,String email){
		this.user_id = user_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.email = email;
	}
	
	public User(String first_name,String last_name,String username,String email){
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.email = email;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
