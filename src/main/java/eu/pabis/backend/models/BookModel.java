package eu.pabis.backend.models;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConstructorBinding;

public class BookModel {

	@NotNull(message = "Id must not be null!") @NotBlank(message = "Id must not be blank!")
	public String id;
	
	@NotNull(message = "Author must not be null!")
	public String author;
	
	@NotNull(message = "Title must not be null!") @NotBlank(message = "Title must not be blank!")
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

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BookModel && (
				((BookModel)obj).id.equalsIgnoreCase(id))
				|| (
						((BookModel)obj).title.equalsIgnoreCase(title) &&
						((BookModel)obj).author.equalsIgnoreCase(author)
					)
				); 
	}

	@Override
	public int hashCode() {
		return author.hashCode() + title.hashCode() + id.hashCode();
	}
	
	
	
}
