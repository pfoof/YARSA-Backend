package eu.pabis.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import eu.pabis.backend.exceptions.BadBookParametersException;
import eu.pabis.backend.mappers.BookRowMapper;
import eu.pabis.backend.models.BookModel;

@Service
public class BookService {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	@Autowired
	private DataSource dataSource;
	
	public BookService() {
		
	}
	
	public BookModel getBook(String id) {
		BookModel target = null;
		
		final String sql = "SELECT * FROM books WHERE "+BookRowMapper.ID+" = :id";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", id);
		List<BookModel> books = template.query(sql, params, new BookRowMapper());
		
		for(BookModel book : books) {
			if(book.id.equalsIgnoreCase(id)) {
				target = book;
				break;
			}
		}
		return target;
	}
	
	public String addBook(BookModel book) throws NullPointerException, BadBookParametersException {
		if(book != null) {
			validateBook(book);
			
			final String sql = "INSERT INTO books ("+BookRowMapper.ID+","+BookRowMapper.TITLE+","+BookRowMapper.AUTHOR+") VALUES (:id,:title,:author)";
			SqlParameterSource params = new MapSqlParameterSource()
					.addValue("id", book.id)
					.addValue("title", book.title)
					.addValue("author", book.author);
			template.update(sql, params);
			
			return idToJson(book.id);
		} else throw new NullPointerException();
	}
	
	public String deleteBook(String id) {
		BookModel book = getBook(id);
		if(book == null) throw new NullPointerException();
		
		final String sql = "DELETE FROM books WHERE "+BookRowMapper.ID+" = :id";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", id);
		template.update(sql, params);
		
		return idToJson(book.id);
	}
	
	public List<BookModel> getBooks() {
		final String sql = "SELECT * FROM books";
		return template.query(sql, new BookRowMapper());
	}
	
	private void validateBook(BookModel book) throws BadBookParametersException {
		Set<ConstraintViolation<BookModel>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(book);
		if(!violations.isEmpty())
			for(ConstraintViolation<BookModel> violation : violations)
				throw new BadBookParametersException(violation.getMessage());
	}
	
	public static String idToJson(String id) { return "{\"id\":\""+id+"\"}"; }
	
}
