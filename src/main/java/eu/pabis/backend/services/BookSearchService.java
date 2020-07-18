package eu.pabis.backend.services;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import eu.pabis.backend.mappers.BookRowMapper;
import eu.pabis.backend.models.BookModel;

@Service
public class BookSearchService {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	public List<BookModel> booksByTitlePrefix(String prefix) {
		
		final String sql = "SELECT * FROM books WHERE ("+BookRowMapper.TITLE+" LIKE :title OR "+BookRowMapper.TITLE+" LIKE '%:title')";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("title", prefix);
		
		return template.query(sql, params, new BookRowMapper());
	}
	
	public List<BookModel> booksByAuthorPrefix(String prefix) {
		
		final String sql = "SELECT * FROM books WHERE ("+BookRowMapper.AUTHOR+" LIKE :author OR "+BookRowMapper.AUTHOR+" LIKE '%:author')";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("author", prefix);
		
		return template.query(sql, params, new BookRowMapper());
	}
	
}
