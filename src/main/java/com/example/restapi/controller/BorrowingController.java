/**
 * @package com.example.restapi.controller
 * @brief This package contains REST controllers for handling user, book, and borrowing operations.
 *
 * This package includes the classes {@link BookController}, {@link UserController}, and {@link BorrowingController}.
 */
package com.example.restapi.controller;

import com.example.restapi.model.Book;
import com.example.restapi.model.User;
import com.example.restapi.service.BorrowingService;
import com.example.restapi.service.UserService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @class BorrowingController
 * @brief Controller class providing RESTful endpoints for borrowing and returning books.
 *
 * This controller interacts with the {@link BorrowingService} and {@link UserService} classes,
 * managing book borrowing actions and retrieval of borrowed book data.
 *
 * The controller is hidden from Swagger documentation via the {@link Hidden} annotation.
 */
@RestController
@RequestMapping("/api/borrows")
//@Hidden  // This will hide the entire BorrowingController from Swagger
public class BorrowingController {
    /** Service handling borrowing logic. */
    @Autowired
    private BorrowingService borrowingService;
    
    /** Service handling user management logic. */
    @Autowired
    private UserService userService;

    /**
     * Endpoint for borrowing a book by a specific user.
     *
     * @param bookId the ID of the book to borrow
     * @param user the {@link User} attempting to borrow the book
     * @return a {@link ResponseEntity} with a success or failure message
     */
    @PostMapping("/{bookId}/borrows")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, @RequestBody User user) {
        return borrowingService.borrowBook(bookId, user) ?
            ResponseEntity.ok("Book borrowed successfully") :
            ResponseEntity.badRequest().body("Book is already borrowed");
    }

    /**
     * Endpoint for returning a borrowed book.
     *
     * @param bookId the ID of the book to return
     * @return a {@link ResponseEntity} with a success or failure message
     */
    @PostMapping("/{bookId}/returns")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId) {
        return borrowingService.returnBook(bookId) ?
            ResponseEntity.ok("Book returned successfully") :
            ResponseEntity.badRequest().body("Book was not borrowed");
    }

    /* 
    @GetMapping("/users/{username}/borrowed-books")
    public List<Book> getBorrowedBooksByUser(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username); // Already returns Optional<User>

        if (userOptional.isEmpty()) {
            return List.of(); // Return empty list if user is not found
        }

        return borrowingService.getBorrowedBooksByUser(username);
    } */


    /**
     * Endpoint for retrieving the list of books borrowed by a specific user.
     *
     * @param username the username of the user
     * @return a {@link ResponseEntity} containing a list of borrowed books represented as maps of book attributes
     */
    @GetMapping("/users/{username}/borrowed-books")
    public ResponseEntity<List<Map<String, Object>>> getBorrowedBooksByUser(@PathVariable String username) {
        List<Map<String, Object>> borrowedBooks = borrowingService.getBorrowedBooksByUser(username);
        return ResponseEntity.ok(borrowedBooks);
    }

    /**
     * Endpoint for deleting a borrowing record based on book and user IDs.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return a {@link ResponseEntity} with no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long bookId, @PathVariable Long userId) {
        borrowingService.deleteByBookUserId(bookId, userId);
        return ResponseEntity.noContent().build();
    }    
}