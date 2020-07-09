package eu.pabis.backend.services;

import org.springframework.stereotype.Service;

import eu.pabis.backend.users.WrongEmailException;
import eu.pabis.backend.users.WrongPasswordException;
import eu.pabis.backend.users.WrongUsernameException;

@Service
public class UserService {
	
	public void registerUser(String email, String username, String password)
			throws WrongPasswordException, WrongUsernameException, WrongEmailException {
		
	}
	
}
