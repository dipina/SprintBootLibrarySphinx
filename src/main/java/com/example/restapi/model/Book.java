package com.example.restapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    
    @ManyToOne
    //@JoinColumn(name = "user_id")
    @JoinColumn(name = "user_id", nullable = true) // Allows null values
    private User user;

    public Book() {}

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public User getBorrower() {
        return this.user;
    }

    public void setBorrower(User borrowedBy) {
        this.user = borrowedBy;
    }
}