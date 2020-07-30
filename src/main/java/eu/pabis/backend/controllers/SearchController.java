package eu.pabis.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.pabis.backend.models.BookModel;
import eu.pabis.backend.services.BookSearchService;

import java.util.*;

@Controller
@RequestMapping("/search")
public class SearchController {

	@Autowired
	BookSearchService bookSearchService;
	
	@RequestMapping("/author/{author}")
	public List<BookModel> searchByAuthor(@PathVariable("author") String author) {
		return bookSearchService.booksByAuthorPrefix(author);
	}
	
	@RequestMapping("/title/{title}")
	public List<BookModel> searchByTitle(@PathVariable("title") String title) {
		return bookSearchService.booksByTitlePrefix(title);
	}
	
}
