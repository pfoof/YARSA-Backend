package eu.pabis.backend.services;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

import eu.pabis.backend.users.Session;

@Service
public class SessionService {

	private HashMap<String, Session> sessions = new HashMap<String, Session>();
	
	private boolean validateSession(String key) {
		return sessions.containsKey(key) && sessions.get(key) != null && sessions.get(key).refresh();
	}
	
	public String createSessionForUser(String userId) {
		String key = UUID.randomUUID().toString();
		sessions.put(key, new Session(userId));
		return key;
	}
	
	public String getUserIdBySession(String key) {
		if(validateSession(key)) {
			return sessions.get(key).getUserId();
		}
		if(sessions.containsKey(key) && (sessions.get(key) == null || sessions.get(key).expired()))
			sessions.remove(key);
		return null;
	}
	
	public void destroy(String key) {
		sessions.remove(key);
	}
	
}
