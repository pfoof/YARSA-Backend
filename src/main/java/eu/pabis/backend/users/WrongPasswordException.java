package eu.pabis.backend.users;

public class WrongPasswordException extends Exception {

	public WrongPasswordException(String string) {
		super(string);
	}

}
