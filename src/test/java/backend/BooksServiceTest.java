package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import eu.pabis.backend.config.DataSourceConfig;
import eu.pabis.backend.exceptions.AlreadyExistsException;
import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookService;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@ContextConfiguration( initializers = {backend.UserServiceTest.Initializer.class} )
@SpringBootTest(classes = { BookService.class, DataSourceConfig.class })
@TestMethodOrder(OrderAnnotation.class)
public class BooksServiceTest {
	
    static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      	String dbUrl = System.getenv("DATABASE_URL");
  		URI uri;
			try {
				uri = new URI(dbUrl);
				TestPropertyValues.of(
			              "spring.datasource.url=" + DataSourceConfig.getURL(uri),
			              "spring.datasource.username=" + DataSourceConfig.getUsername(uri),
			              "spring.datasource.password=" + DataSourceConfig.getPassword(uri)
			            ).applyTo(configurableApplicationContext.getEnvironment());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
      }
  }
	
    @Autowired
    DataSource dataSource;
	
    private static BookService service;
    
    @BeforeAll
    public static void prepare() throws SQLException, URISyntaxException {
    	service = new BookService(new DataSourceConfig().getDataSource());
    }
	
	@Test @DisplayName("Testing inexistent book (null)") @Order(2)
	void testNull() {
		BookModel book = service.getBook("blahblah");
		assertNull(book);
	}
	
	@Test @DisplayName("Testing adding book") @Order(1)
	void testAdd() {
		BookModel book = new BookModel("Junit", "Test");
		
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
				/* Test if the book is inserted into the collection */
				BookModel fetchedBook = service.getBook(obj.getString("id"));
				assertNotNull(fetchedBook);
				assertTrue(book.author.equalsIgnoreCase(fetchedBook.author));
				assertTrue(book.title.equalsIgnoreCase(fetchedBook.title));
			});
		});
		
		assertThrows(AlreadyExistsException.class, () -> {
			service.addBook(book);
		});
	}
	
	@Test @DisplayName("Testing null adds") @Order(3)
	void testAddNull() {
		assertThrows(NullPointerException.class, () -> {
			service.addBook(null);
		});
	}
	
	@Test @DisplayName("Testing all books fetching") @Order(4)
	void testGetAll() {
		List<BookModel> books = service.getBooks();
		
		assertNotNull(books);
		assertTrue(books.size() > 0);
	}
	
	@Test @DisplayName("Testing deletion") @Order(5)
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
	
	@Test @DisplayName("Testing null deletion") @Order(6)
	void testDeleteInexistent() {
		
		assertThrows(NullPointerException.class, () -> {
			service.deleteBook("blahblah");
		});
	}
}
