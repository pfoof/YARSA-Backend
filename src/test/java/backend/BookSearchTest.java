package backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookSearchService;
import eu.pabis.backend.services.BookService;

@SpringBootTest(classes = {BookSearchService.class, BookService.class})
public class BookSearchTest {
	
	@Autowired
	BookSearchService service;
	
	@Autowired
	BookService bookService;
	
	BookModel book1 = new BookModel("Tolkien JRR", "Hobbit");
	BookModel book2 = new BookModel("Tolkien JRR", "Lord of the Rings");
	BookModel book3 = new BookModel("Kiyosaki Robert", "Rich dad, Poor dad");
	
	@Test @DisplayName("Testing search by title")
	public void testSearchTitle() {
		
		bookService.addBook(book1);
		bookService.addBook(book2);
		bookService.addBook(book3);
		
		List<BookModel> books_Lord = service.booksByTitlePrefix("Lord"); // "Lord of the Rings"
		List<BookModel> books_Ri = service.booksByTitlePrefix("Ri"); // "Lord of the Rings", "Rich dad, Poor dad"
		
		assertEquals(1, books_Lord.size());
		assertTrue(books_Lord.contains(book2));
		assertFalse(books_Lord.contains(book1));
		assertFalse(books_Lord.contains(book3));
		
		assertEquals(2, books_Ri.size());
		assertTrue(books_Ri.contains(book2));
		assertTrue(books_Ri.contains(book3));
		assertFalse(books_Ri.contains(book1));
	}
	
	@Test @DisplayName("Testing search by author")
	public void testSearchAuthor() {
		
		List<BookModel> books_Tol = service.booksByAuthorPrefix("Tol"); // "Lord of the Rings", "Hobbit"
		
		assertEquals(2, books_Tol.size());
		assertTrue(books_Tol.contains(book1));
		assertTrue(books_Tol.contains(book2));
		assertFalse(books_Tol.contains(book3));
	}
	

}
