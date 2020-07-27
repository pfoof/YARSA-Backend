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
public class BookSearchService extends AbstractSQLService {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private NamedParameterJdbcTemplate template;

	
	public List<BookModel> booksByTitlePrefix(String prefix) {
		
		String prefix_asterisk = prefix + "%";
		String prefix_space_asterisk = "% "+prefix + "%";
		
		final String sql = "SELECT * FROM books WHERE ("+BookRowMapper.TITLE+" LIKE :title_asterisk OR "+BookRowMapper.TITLE+" LIKE :title_space_asterisk)";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("title_asterisk", prefix_asterisk)
				.addValue("title_space_asterisk", prefix_space_asterisk);
		
		return template.query(sql, params, new BookRowMapper());
	}
	
	public List<BookModel> booksByAuthorPrefix(String prefix) {
		
		String prefix_asterisk = prefix + "%";
		String prefix_space_asterisk = "% "+prefix + "%";
		
		final String sql = "SELECT * FROM books WHERE ("+BookRowMapper.AUTHOR+" LIKE :author_asterisk OR "+BookRowMapper.AUTHOR+" LIKE :author_space_asterisk)";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("author_asterisk", prefix_asterisk)
				.addValue("author_space_asterisk", prefix_space_asterisk);
		
		return template.query(sql, params, new BookRowMapper());
	}
	
}
