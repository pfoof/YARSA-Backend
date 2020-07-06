package eu.pabis.backend.models;

import java.util.UUID;

public class BookModel {

	public String id;
	
	public String author;
	
	public String title;
	
	public BookModel(String author, String title) {
		this.author = author;
		this.title = title;
		this.id = UUID.randomUUID().toString();
	}
	
	public BookModel(String id, String author, String title) {
		this(author, title);
		this.id = id;
	}
	
}
