package eu.pabis.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import eu.pabis.backend.exceptions.AlreadyExistsException;
import eu.pabis.backend.exceptions.BadBookParametersException;
import eu.pabis.backend.exceptions.UnknownException;
import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookService;

@Controller
@RequestMapping("/books")
public class BooksController {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private BookService service;

	@RequestMapping( value = {"/", ""}, method = RequestMethod.GET )
	@ResponseBody //Skip thymeleaf
	List<BookModel> books() {
		return service.getBooks();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/", ""})
	@ResponseBody //Skip thymeleaf
	String addBook(@RequestBody BookModel book) throws ResponseStatusException {
		try {
			return service.addBook(book);
		} catch (NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The content of the record is null");
		} catch (BadBookParametersException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch(AlreadyExistsException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		} catch(UnknownException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody //Skip thymeleaf
	BookModel books(@PathVariable("id") String id) throws ResponseStatusException {
		BookModel book = service.getBook(id);
		if(book == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id "+id+" is not in the database!");
		else
			return book;
		
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody //Skip thymeleaf
	String deleteBook(@PathVariable("id") String id) throws ResponseStatusException {
		try {
			return service.deleteBook(id);
		} catch (NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id "+id+" is not in the database!");
		}
	}
	
}
