package com.example.restapi.repository;

import com.example.restapi.model.Book;
import com.example.restapi.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByUserId(Long userId);
    List<Borrowing> findByBookId(Long bookId);
    void deleteByBookId(Long bookId);

    // Find the latest borrowing record of a user for a specific book
    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.user.id = :userId ORDER BY b.borrowDate DESC LIMIT 1")
    Borrowing findLatestBorrowing(Long bookId, Long userId);


    // Add this query to return a list of books borrowed by a user
    @Query("SELECT b FROM Book b JOIN Borrowing br ON br.book.id = b.id WHERE br.user.id = :userId")
    List<Book> findBooksByUserId(Long userId);

    // Add this query to return a list of books borrowed by a user which have not be returned
    @Query("SELECT b FROM Book b JOIN Borrowing br ON br.book.id = b.id WHERE br.user.id = :userId AND br.returnDate IS NULL")
    List<Book> findBooksBorrowedByUserId(Long userId);

    // Fetch Borrowing details for a specific user and book
    @Query("SELECT br FROM Borrowing br WHERE br.book.id = :bookId AND br.user.id = :userId")
    List<Borrowing> findBorrowingsByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.user.id = :userId AND b.returnDate IS NULL")
    List<Borrowing> findActiveBorrowingsByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.user.id = :userId AND b.returnDate IS NULL")
    Optional<List<Borrowing>> findActiveBorrowingByBookIdAndUserId(Long bookId, Long userId);

    @Query(value = "SELECT * FROM borrowings WHERE book_id = :bookId AND user_id = :userId AND return_date IS NULL ORDER BY borrow_date DESC LIMIT 1", nativeQuery = true)
    Optional<Borrowing> findLatestBorrowedBook(Long bookId, Long userId);


    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.returnDate IS NULL")
    List<Borrowing> findActiveBorrowingsByBookId(Long bookId, Long userId);
}


