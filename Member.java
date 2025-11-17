package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member - Model class representing a library member
 * This is a stub implementation for integration purposes.
 * Team member responsible for Member model will implement full functionality.
 * 
 * @author Team Member (Member Model Owner)
 * @version 1.0
 */
public class Member {
    
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private LocalDate membershipDate;
    private List<String> borrowedBooks;
    private boolean isActive;
    
    /**
     * Constructor
     */
    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.membershipDate = LocalDate.now();
        this.borrowedBooks = new ArrayList<>();
        this.isActive = true;
    }
    
    // Getters and Setters
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getMembershipDate() {
        return membershipDate;
    }
    
    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }
    
    public List<String> getBorrowedBooks() {
        return borrowedBooks;
    }
    
    public void setBorrowedBooks(List<String> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public void borrowBook(String bookId) {
        borrowedBooks.add(bookId);
    }
    
    public void returnBook(String bookId) {
        borrowedBooks.remove(bookId);
    }
    
    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", borrowedBooks=" + borrowedBooks.size() +
                '}';
    }
}
