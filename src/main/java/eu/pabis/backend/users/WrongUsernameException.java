package eu.pabis.backend.users;

public class WrongUsernameException extends Exception {
	public WrongUsernameException(String string) {
		super(string);
	}
}
