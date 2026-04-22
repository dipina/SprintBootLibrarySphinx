package com.example.restapi.service;

import com.example.restapi.model.Book;
import com.example.restapi.model.User;
import com.example.restapi.repository.BookRepository;
import com.example.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service class that provides business logic for managing {@link Book} and borrowing operations.
 */
@Service
public class BookService {
    /** Repository for managing {@link Book} entities. */
    @Autowired
    private BookRepository bookRepository;
    
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Retrieves all books from the repository.
     *
     * @return a list of all {@link Book} objects
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Retrieves a book by its unique identifier.
     *
     * @param id the ID of the book to retrieve
     * @return an {@link Optional} containing the found {@link Book}, or empty if not found
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    /**
     * Creates a new book entry in the repository.
     *
     * @param book the {@link Book} object to be created
     * @return the created {@link Book} object
     */
    public Book createBook(Book book) {
        System.out.println("Book Title: " + book.getTitle() + ", Author: " + book.getAuthor());
        return bookRepository.save(book);
    }
    
    /**
     * Updates an existing book's details.
     *
     * @param id the ID of the book to update
     * @param bookDetails the updated {@link Book} details
     * @return the updated {@link Book} object
     * @throws Exception if the book with the specified ID is not found
     */
    public Book updateBook(Long id, Book bookDetails) throws Exception {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            return bookRepository.save(book);
        }).orElseThrow(() -> new Exception("Book not found"));
    }
    
    /**
	 * Deletes the Book identified by id.
	 * deleteBook() needs to be public because it is used
	 * by clients who wish to remove books identified by id
	 */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public boolean borrowBook(Long bookId, Long userId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalBook.isPresent() && optionalUser.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBorrower() == null) {
                book.setBorrower(optionalUser.get());
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a borrowed book, making it available for others.
     *
     * @param bookId the ID of the book to return
     * @return {@code true} if the return was successful, {@code false} otherwise
     */
    public boolean returnBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBorrower() != null) {
                book.setBorrower(null);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }
}