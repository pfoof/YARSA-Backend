package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest(classes = BookService.class)
public class BooksServiceTest {
	
	@Autowired
	BookService service;
	
	@Test @DisplayName("Testing inexistent book (null)")
	void testNull() {
		BookModel book = service.getBook("blahblah");
		assertNull(book);
	}
	
	@Test @DisplayName("Testing adding book")
	void testAdd() {
		BookModel book = new BookModel("Junit", "Test");
		String recordId = book.id;
		
		/* Test if the service returned properly formatted JSON with id */
		assertDoesNotThrow(() -> {
			String response = service.addBook(book);
			assertDoesNotThrow(() -> {
				JSONObject obj = new JSONObject(response);
				assertTrue(obj.has("id"));
				assertDoesNotThrow(() -> {
					obj.getString("id");
				});
				assertNotNull(obj.getString("id"));
				/* Subject to possible change */
				assertTrue(obj.getString("id").equalsIgnoreCase(recordId));
			});
		});
		
		/* Test if the book is inserted into the collection */
		BookModel fetchedBook = service.getBook(recordId);
		assertTrue(book.author.equalsIgnoreCase(fetchedBook.author));
		assertTrue(book.title.equalsIgnoreCase(fetchedBook.title));
	}
	
	@Test @DisplayName("Testing null adds")
	void testAddNull() {
		assertThrows(NullPointerException.class, () -> {
			service.addBook(null);
		});
	}
	
	@Test @DisplayName("Testing all books fetching")
	void testGetAll() {
		List<BookModel> books = service.getBooks();
		
		assertNotNull(books);
		assertTrue(books.size() > 0);
	}
	
	@Test @DisplayName("Testing deletion")
	void testDelete() {
		List<BookModel> books = service.getBooks();
		int size = books.size();
		BookModel top = books.get(0);
		String recordId = top.id;
		
		assertDoesNotThrow(() -> {
			String response = service.deleteBook(recordId);
			
			/* Test if response is JSON */
			assertDoesNotThrow(() -> {
				JSONObject obj = new JSONObject(response);
				assertTrue(obj.has("id"));
				assertDoesNotThrow(() -> {
					obj.getString("id");
				});
				assertNotNull(obj.getString("id"));
				/* Subject to possible change */
				assertTrue(obj.getString("id").equalsIgnoreCase(recordId));
			});
			
		});
		
		/* Book should be unable to be found */
		assertNull(service.getBook(recordId));
		
		/* The list should be shorter by one record */
		assertEquals(size - 1, service.getBooks().size());
	}
	
	@Test @DisplayName("Testing null deletion")
	void testDeleteInexistent() {
		
		assertThrows(NullPointerException.class, () -> {
			service.deleteBook("blahblah");
		});
	}
}
