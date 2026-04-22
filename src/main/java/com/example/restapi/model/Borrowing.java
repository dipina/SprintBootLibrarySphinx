/**
 * @file Borrowing.java
 * @brief Entity class representing a borrowing transaction between a user and a book.
 *
 * @details
 * The {@code Borrowing} class maps to the "borrowings" table in the database.
 * It captures the user, book, borrow date, and optional return date for each borrowing event.
 *
 * @see User
 * @see Book
 *
 * @author 
 * Example Developer
 * @version 1.0
 * @since 2024-04-28
 */
package com.example.restapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @class Borrowing
 * @brief A JPA entity representing a borrowing record.
 *
 * This entity manages information regarding the borrowing and returning
 * of a {@link Book} by a {@link User}.
 */
@Entity
@Table(name = "borrowings")
public class Borrowing {
    /**
     * @brief Unique identifier for the borrowing record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     /**
     * @brief The user who borrows the book.
     *
     * @details
     * This establishes a many-to-one relationship with the {@link User} entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * @brief The book that is borrowed.
     *
     * @details
     * This establishes a many-to-one relationship with the {@link Book} entity.
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * @brief The date and time when the book was borrowed.
     */
    private LocalDateTime borrowDate;
     /**
     * @brief The date and time when the book was returned.
     *
     * @details
     * This is {@code null} until the book is returned.
     */
    private LocalDateTime returnDate;

    /**
     * @brief Default constructor.
     *
     * @details
     * Required by JPA for entity instantiation.
     */
    public Borrowing() {}

     /**
     * @brief Constructor to create a new borrowing record.
     *
     * @param user the user borrowing the book
     * @param book the book being borrowed
     *
     * @details
     * The borrow date is initialized to the current time, and the return date is set to {@code null}.
     */
    public Borrowing(User user, Book book) {
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDateTime.now();
        this.returnDate = null;
    }

    /**
     * @brief Gets the unique identifier of the borrowing record.
     *
     * @return the borrowing record ID
     */
    public Long getId() { return id; }

    /**
     * @brief Gets the user associated with this borrowing.
     *
     * @return the {@link User} who borrowed the book
     */
    public User getUser() { return user; }

    /**
     * @brief Gets the book associated with this borrowing.
     *
     * @return the {@link Book} that was borrowed
     */
    public Book getBook() { return book; }

    /**
     * @brief Gets the date and time when the book was borrowed.
     *
     * @return the borrow date and time
     */
    public LocalDateTime getBorrowDate() { return borrowDate; }

    /**
     * @brief Gets the date and time when the book was returned.
     *
     * @return the return date and time, or {@code null} if not yet returned
     */
    public LocalDateTime getReturnDate() { return returnDate; }

    /**
     * @brief Sets the date and time when the book was returned.
     *
     * @param returnDate the return date and time
     */
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}
