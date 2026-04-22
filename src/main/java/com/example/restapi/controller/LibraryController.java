package com.example.restapi.controller;

import com.example.restapi.model.Book;
import com.example.restapi.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/library")
@Tag(name = "Library API", description = "Operations to manage a Library which correlates books and users information")
public class LibraryController {

    private final UserController userController;
    private final BookController bookController;
    private final BorrowingController borrowingController;

    @Autowired
    public LibraryController(UserController userController, 
                             BookController bookController, 
                             BorrowingController borrowingController) {
        this.userController = userController;
        this.bookController = bookController;
        this.borrowingController = borrowingController;
    }

    // User registration
    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return userController.register(user);
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpSession session) {
        return userController.login(user, session);
    }
    


    // Check user session
    @GetMapping("/users/session")
    public ResponseEntity<?> getUserSession(HttpSession session) {
        return userController.checkSession(session);
    }


    @GetMapping("/books")
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = bookController.getAllBooks(); // Get the list of books
        return ResponseEntity.ok(books); // Wrap it in ResponseEntity and return
    }


    // Add a new book
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        System.out.println("Adding book: " + book.getAuthor() + " - " + book.getTitle());
        return bookController.createBook(book);
    }

    // Borrow a book
    @PostMapping("/books/borrow/{bookId}/users/{userId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, @PathVariable Long userId) {
        return borrowingController.borrowBook(bookId, userController.getUserById(userId).getBody());
    }

    // Return a book
    @PostMapping("/books/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId) {
        return borrowingController.returnBook(bookId);
    }

    // View user profile and borrowed books
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
        return userController.getUserById(userId);
    }

    // View user profile and borrowed books
    @GetMapping("/users/byname/{username}")
    public ResponseEntity<User> getUserProfileByUsername(@PathVariable String username) {
        return userController.getUserByUsername(username);
    }

    /* 
    @GetMapping("/users/{username}/borrowed-books")
    public ResponseEntity<List<Book>> getBorrowedBooksByUser(@PathVariable String username) {
        List<Book> borrowedBooks = borrowingController.getBorrowedBooksByUser(username);
        return ResponseEntity.ok(borrowedBooks);
    }
    */

    @GetMapping("/users/{username}/borrowed-books")
    public ResponseEntity<List<Map<String, Object>>> getBorrowedBooksByUser(@PathVariable String username) {
        return borrowingController.getBorrowedBooksByUser(username);
    }

    @DeleteMapping("books/{bookId}/users/{userId}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long bookId, @PathVariable Long userId) {
        borrowingController.deleteBorrowing(bookId, userId);
        bookController.deleteBook(bookId);
        userController.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
