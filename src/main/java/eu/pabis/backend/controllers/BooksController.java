package eu.pabis.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookService;

@Controller
@RequestMapping("/books")
public class BooksController {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private BookService service;

	@RequestMapping("/")
	@ResponseBody //Skip thymeleaf
	List<BookModel> books() {
		return service.getBooks();
	}
	
	@RequestMapping("/{id}")
	@ResponseBody //Skip thymeleaf
	BookModel books(@PathVariable("id") String id) throws HttpClientErrorException {
		BookModel book = service.getBook(id);
		if(book == null)
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book with id "+id+" is not in the database!");
		else
			return book;
		
	}
	
}
