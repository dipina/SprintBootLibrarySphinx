package com.example.restapi.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.restapi.model.Book;
import com.example.restapi.model.User;
import com.example.restapi.repository.BookRepository;
import com.example.restapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() { 
        Book book = new Book("Spring Boot in Action", "anonymous");
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(book);

        assertNotNull(createdBook);
        assertEquals("Spring Boot in Action", createdBook.getTitle());
        assertTrue(createdBook.getBorrower()==null);
    }

    @Test
    void testBorrowBook() {
        Book book = new Book("Spring Boot in Action", "anonymous");
        User user = new User("john", "pass", "John Doe", "john@email.org");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        boolean success = bookService.borrowBook(1L, 1L);

        assertTrue(success);
        assertFalse(book.getBorrower()==null);
    }

    @Test
    void testReturnBook() {
        Book book = new Book("Spring Boot in Action", "anonymous");
        User user = new User("john", "pass", "John Doe", "john@email.org");
        book.setBorrower(user);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        boolean success = bookService.returnBook(1L);

        assertTrue(success);
        assertTrue(book.getBorrower()==null);
    }
}
