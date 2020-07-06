package eu.pabis.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
	
	public List<BookModel> getBooks() {
		return books;
	}
	
}
