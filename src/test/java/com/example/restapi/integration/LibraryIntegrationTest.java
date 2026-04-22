package com.example.restapi.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.example.restapi.model.Book;
import com.example.restapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testUserBorrowsAndReturnsBook() {
        // Step 1: Create a user
        User user = new User("john", "pass", "John Doe", "john@email.org");
        ResponseEntity<User> userResponse = restTemplate.postForEntity("/api/library/users/register", user, User.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        User createdUser = userResponse.getBody();
        assertNotNull(createdUser);
        Long userId = createdUser.getId();

        // Step 2: Create a book
        Book book = new Book("Spring Boot in Action", "anonymous");
        ResponseEntity<Book> bookResponse = restTemplate.postForEntity("/api/library/books", book, Book.class);
        assertEquals(HttpStatus.OK, bookResponse.getStatusCode());
        Book createdBook = bookResponse.getBody();
        assertNotNull(createdBook);
        Long bookId = createdBook.getId();

        // Step 3: Borrow the book
        ResponseEntity<Void> borrowResponse = restTemplate.exchange(
            "/api/library/books/borrow/" + bookId + "/users/" + userId, HttpMethod.POST, null, Void.class);
        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode());

        // Step 4: Verify book is borrowed
        ResponseEntity<List<Book>> borrowedBookResponse = restTemplate.exchange(
            "/api/library/books", 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Book>>() {}
        );

        // Ensure the response is OK
        assertEquals(HttpStatus.OK, borrowedBookResponse.getStatusCode());

        // Get the list of books
        List<Book> books = borrowedBookResponse.getBody();
        assertNotNull(books);
        assertFalse(books.isEmpty());

        // Find the borrowed book
        // Find the borrowed book using a traditional loop
        Book borrowedBook = null;
        for (Book bookTemp : books) {
            if (bookTemp.getId().equals(bookId)) {
                borrowedBook = bookTemp;
                break; // Exit loop once the book is found
            }
        }


        // Ensure the book is found and is borrowed
        assertNotNull(borrowedBook);
        assertNotNull(borrowedBook.getBorrower());

        // Step 5: Return the book
        ResponseEntity<Void> returnResponse = restTemplate.exchange(
            "/api/library/books/return/"  + bookId, HttpMethod.POST, null, Void.class);
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());

        // Step 6: Verify book is available
        ResponseEntity<List<Book>> returnedBookResponse = restTemplate.exchange(
            "/api/library/books", 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Book>>() {}
        );
        // Ensure the response is OK
        assertEquals(HttpStatus.OK, returnedBookResponse.getStatusCode());

        // Get the list of books
        books = returnedBookResponse.getBody();
        assertNotNull(books);
        assertFalse(books.isEmpty());

        // Find the borrowed book
        // Find the borrowed book using a traditional loop
        Book returnedBook = null;
        for (Book bookTemp : books) {
            if (bookTemp.getId().equals(bookId)) {
                returnedBook = bookTemp;
                break; // Exit loop once the book is found
            }
        }

        assertNotNull(returnedBook);
        assertTrue(returnedBook.getBorrower()==null);


        // Step 7: Remove borrowing, book and user created
        ResponseEntity<Void> borrowingDeleteResponse = restTemplate.exchange(
        "/api/library/books/"+ bookId + "/users/"  + userId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, borrowingDeleteResponse.getStatusCode());
    }
}
