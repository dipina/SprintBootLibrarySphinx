package com.example.restapi.service;

import com.example.restapi.model.Book;
import com.example.restapi.model.Borrowing;
import com.example.restapi.model.User;
import com.example.restapi.repository.BookRepository;
import com.example.restapi.repository.BorrowingRepository;
import com.example.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    public boolean borrowBook(Long bookId, User user) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBorrower() == null) {
                book.setBorrower(user);
                bookRepository.save(book);
                Borrowing borrowing = new Borrowing(user, book);
                borrowingRepository.save(borrowing);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBorrower() != null) {
                Long borrowerId= book.getBorrower().getId();
                book.setBorrower(null);
                bookRepository.save(book);
                System.out.println("Going to call to this.getBorrowingDetails");
                Optional<Borrowing> optionalBorrowing = this.getBorrowingDetails(book.getId(), borrowerId);
                if (optionalBorrowing.isPresent()) {
                    Borrowing borrowing = optionalBorrowing.get();
                    borrowing.setReturnDate(LocalDateTime.now());
                    borrowingRepository.save(borrowing);
                    //borrowingRepository.deleteById(borrowing.getId());
                }
                return true;
            }
        }
        return false;
    }

    /* 
    public List<Book> getBorrowedBooksByUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username); // Already returns Optional<User>
        
        if (userOptional.isEmpty()) {
            return List.of(); // Return empty list if user is not found
        }
    
        return borrowingRepository.findBooksByUserId(userOptional.get().getId());
    }*/

    public List<Map<String, Object>> getBorrowedBooksByUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if user is not found
        }

        User user = userOptional.get();
        List<Borrowing> borrowings = new ArrayList<>();


        //borrowingRepository.findActiveBorrowingsByBookIdAndUserId(null, null)
        for (Book book : borrowingRepository.findBooksBorrowedByUserId(user.getId())) {
            System.out.println(book);
            borrowings.addAll(borrowingRepository.findActiveBorrowingsByBookId(book.getId(), user.getId()));
        }

        // Convert borrowings to a list of maps
        List<Map<String, Object>> borrowedBooks = new ArrayList<>();
        for (Borrowing borrowing : borrowings) {
            Map<String, Object> bookData = new HashMap<>();
            bookData.put("title", borrowing.getBook().getTitle());
            bookData.put("author", borrowing.getBook().getAuthor());
            bookData.put("borrowDate", borrowing.getBorrowDate());

            borrowedBooks.add(bookData);
        }

        return borrowedBooks;
    }

    public Optional<Borrowing> getBorrowingDetails(Long bookId, Long userId) {
        //return borrowingRepository.findBorrowingsByBookIdAndUserId(bookId, userId);
        return borrowingRepository.findLatestBorrowedBook(bookId, userId);
    }    

    public List<Borrowing> getBorrowingsDetails(Long bookId, Long userId) {
        //return borrowingRepository.findBorrowingsByBookIdAndUserId(bookId, userId);
        return borrowingRepository.findActiveBorrowingsByBookIdAndUserId(bookId, userId);
    }    


    @Transactional
    public void deleteByBookUserId(Long bookId, Long userId) {
        Borrowing latestBorrowing = borrowingRepository.findLatestBorrowing(bookId, userId);
        if (latestBorrowing != null) {
            borrowingRepository.delete(latestBorrowing);
        }
    }
    
}