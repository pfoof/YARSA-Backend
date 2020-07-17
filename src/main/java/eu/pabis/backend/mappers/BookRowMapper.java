package eu.pabis.backend.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.pabis.backend.models.BookModel;

public class BookRowMapper implements RowMapper<BookModel> {

	public BookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		String id = rs.getString(ID);
		String title = rs.getString(TITLE);
		String author = rs.getString(AUTHOR);
		return new BookModel(id, author, title);
	}
	
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";

}
