package eu.pabis.backend.controllers;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import eu.pabis.backend.exceptions.AlreadyExistsException;
import eu.pabis.backend.exceptions.WrongEmailException;
import eu.pabis.backend.exceptions.WrongPasswordException;
import eu.pabis.backend.exceptions.WrongUsernameException;
import eu.pabis.backend.models.UserModel;
import eu.pabis.backend.services.SessionService;
import eu.pabis.backend.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	public static final String SESSION_ID = "sessionId";
	
	@Autowired
	SessionService sessionService;
	
	@Autowired
	UserService userService;
	
	@RequestMapping( value = {"", "/"} )
	@ResponseBody
	public Map<String, String> main(HttpServletRequest request) {
		if(verifySession(request)) {
			String cookie = getSessionCookie(request);
			String uid = sessionService.getUserIdBySession(cookie);
			UserModel user = userService.findUserById(uid);
			if(user == null)
				throw new HttpClientErrorException(HttpStatus.CONFLICT, "Session does not match any exisitng user!");
			
			Map<String, String> result = new HashMap<String, String>();
			result.put("username", user.email);
			result.put("username", user.username);
			return result;
		} else
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Session expired. Please login again!");
	}
	
	@RequestMapping( value = "/login", method = RequestMethod.POST )
	@ResponseBody
	public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws ResponseStatusException {
		try {
			System.out.println("Logging in user: "+username);
			String session = userService.loginUser(username, password);
			setSessionCookie(response, session);
			return session;
		} catch (WrongUsernameException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials!", e);
		} catch (WrongPasswordException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials!", e);
		}
		
	}
	
	@RequestMapping( value = "/logout" )
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String cookie = getSessionCookie(request);
		if(cookie != null)
			sessionService.destroy(cookie);
		Cookie removeCookie = new Cookie(SESSION_ID, null);
		removeCookie.setSecure(true);
		removeCookie.setMaxAge(0);
		removeCookie.setPath("/");
		response.addCookie(removeCookie);
	}
	
	@RequestMapping( value = "/register", method = RequestMethod.POST )
	@ResponseBody
	public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
		try {
			userService.registerUser(email, username, password);
		} catch (WrongPasswordException | WrongUsernameException | WrongEmailException | AlreadyExistsException e) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		return "{\"success\":\"1\"}";
	}
	
	private void setSessionCookie(HttpServletResponse response, String key) {
		Cookie cookie = new Cookie(SESSION_ID, key);
		cookie.setSecure(true);
		cookie.setMaxAge(60 * 24 * 60 * 60); // 60 days
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	private boolean verifySession(HttpServletRequest request) {
		String cookie = getSessionCookie(request);
		if(cookie == null) return false;
		String sessionUser = sessionService.getUserIdBySession(cookie);
		if(sessionUser == null) return false;
		UserModel u = userService.findUserById(sessionUser);
		if(u == null) return false;
		return true;
	}
	
	private String getSessionCookie(HttpServletRequest request) {
		if(request.getCookies() == null) return null;
		Cookie[] filtered = (Cookie[]) Stream.of(request.getCookies()).filter(c -> c.getName().equalsIgnoreCase(SESSION_ID)).toArray();
		if(filtered.length < 1) return null;
		if(!filtered[0].getName().equalsIgnoreCase(SESSION_ID)) return null;
		return filtered[0].getValue();
	}
	
}
