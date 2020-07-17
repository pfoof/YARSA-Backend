package eu.pabis.backend.services;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.pabis.backend.models.BookModel;

@Service
public class BookSearchService {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private DataSource dataSource;
	
	public List<BookModel> booksByTitlePrefix(String prefix) {
		ArrayList<BookModel> results = new ArrayList<BookModel>();
		for(BookModel b : bookService.getBooks())
			for(String t : b.title.split(" "))
				if(!t.isEmpty() && t.toLowerCase().startsWith(prefix.toLowerCase())) {
					results.add(b);
					break;
				}
		return results;
	}
	
	public List<BookModel> booksByAuthorPrefix(String prefix) {
		ArrayList<BookModel> results = new ArrayList<BookModel>();
		
		for(BookModel b : bookService.getBooks())
			for(String a : b.author.split(" "))
				if(!a.isEmpty() && a.toLowerCase().startsWith(prefix.toLowerCase())) {
					results.add(b);
					break;
				}
		
		return results;
	}
	
}
