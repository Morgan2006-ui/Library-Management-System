package models;

import java.time.LocalDate;

/**
 * Book - Model class representing a book in the library
 * This is a stub implementation for integration purposes.
 * Team member responsible for Book model will implement full functionality.
 * 
 * @author Team Member (Book Model Owner)
 * @version 1.0
 */
public class Book {
    
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private boolean isAvailable;
    private String currentBorrowerId;
    private LocalDate dueDate;
    
    /**
     * Constructor
     */
    public Book(String bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
    }
    
    // Getters and Setters
    
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
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
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }
    
    public void setCurrentBorrowerId(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
