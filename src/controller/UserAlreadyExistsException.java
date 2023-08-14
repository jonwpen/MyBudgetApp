package controller;

public class UserAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {
        super("User already exists");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}