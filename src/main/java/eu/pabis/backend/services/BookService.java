package eu.pabis.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import eu.pabis.backend.models.BookModel;

@Service
public class BookService {

	private ArrayList<BookModel> books = new ArrayList<BookModel>();
	
	public BookService() {
		books.add(new BookModel("Adam Mickieiwcz", "Krzyzacy"));
		books.add(new BookModel("Juluiusz Slowacki", "Rota"));
	}
	
	public BookModel getBook(String id) {
		BookModel target = null;
		for(BookModel book : books) {
			if(book.id.equalsIgnoreCase(id)) {
				target = book;
				break;
			}
		}
		return target;
	}
	
	public void addBook(BookModel book) throws NullPointerException {
		if(book != null) {
			books.add(book);
		} else throw new NullPointerException();
	}
	
	public List<BookModel> getBooks() {
		return books;
	}
	
}
