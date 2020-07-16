package eu.pabis.backend.users;

import java.util.Date;
import java.util.UUID;

public class Session {
	
	private Date expiration = new Date();
	private String owner = UUID.randomUUID().toString();
	
	public Session(String userId) {
		refresh();
		owner = userId;
	}
	
	public String getUserId() {
		return owner;
	}
	
	public boolean expired() {
		return new Date().after(expiration);
	}
	
	public boolean refresh() {
		if(expired()) return false;
		expiration.setTime(expiration.getTime() + MAX_AGE);
		return true;
	}
	
	public static final long MAX_AGE = 1000L * 60L * 60L * 48L; // 48 Hours
	
}
