
import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {
	
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    private String currentBorrowerId;
    private LocalDate dueDate;

    
    
    public Book(String bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
    }

    
    
    
    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getCurrentBorrowerId() { return currentBorrowerId; }
    public void setCurrentBorrowerId(String id) { this.currentBorrowerId = id; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate date) { this.dueDate = date; }
    
    
    
    @Override
    public String toString() { 
        String status = isAvailable ? "Available" : "Checked Out";
        return title + " by " + author + " [" + isbn + "] - " + status;
    }
}
