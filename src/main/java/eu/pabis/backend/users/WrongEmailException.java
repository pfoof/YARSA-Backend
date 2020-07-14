package eu.pabis.backend.users;

public class WrongEmailException extends Exception {
	public WrongEmailException(String string) {
		super(string);
	}
}
