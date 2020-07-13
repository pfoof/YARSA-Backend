package eu.pabis.backend;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.jdbc.*;

@Controller
@SpringBootApplication
public class Main {
	
	DataSource dataSource;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}
	
	public Main() {
		
	}
	
	@RequestMapping("/")
	String index() {
		return "index";
	}
}
