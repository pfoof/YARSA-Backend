package eu.pabis.backend.models;

import java.util.UUID;

import org.springframework.boot.context.properties.ConstructorBinding;

public class BookModel {

	public String id;
	
	public String author;
	
	public String title;
	
	public BookModel() {
		this.id = UUID.randomUUID().toString();
	}
	
	@ConstructorBinding
	public BookModel(String author, String title) {
		this();
		this.author = author;
		this.title = title;
	}
	
	public BookModel(String id, String author, String title) {
		this(author, title);
		this.id = id;
	}
	
}
