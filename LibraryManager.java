package managers;

import models.Book;
import models.Member;
import persistence.DataPersistence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LibraryManager - Singleton class that coordinates all library operations
 * This class serves as the central coordinator for the library management system,
 * managing all core components and ensuring data consistency across the application.
 * 
 * Responsibilities:
 * - Coordinate between different system components
 * - Manage system-wide state and configuration
 * - Provide centralized access to all managers and services
 * - Handle data persistence operations
 * 
 * @author Group Leader
 * @version 1.0
 */
public class LibraryManager {
    
    // Singleton instance
    private static LibraryManager instance;
    private static final Object lock = new Object();
    
    // Core data structures
    private Map<String, Book> bookCatalog;
    private Map<String, Member> memberRegistry;
    private Map<String, List<String>> borrowingHistory;
    private Map<String, Queue<String>> reservationQueues;
    
    // Component references (to be implemented by team members)
    private Object bookManager;
    private Object memberManager;
    private Object checkoutManager;
    private Object searchManager;
    
    // Data persistence handler
    private DataPersistence dataPersistence;
    
    // System configuration
    private int maxBorrowDays = 14;
    private int renewalDays = 7;
    private int maxBooksPerMember = 5;
    private int alertDaysBefore = 3;
    
    /**
     * Private constructor to prevent instantiation
     * Initializes all data structures and loads persisted data
     */
    private LibraryManager() {
        initializeDataStructures();
        dataPersistence = new DataPersistence();
        loadPersistedData();
    }
    
    /**
     * Get the singleton instance of LibraryManager
     * Thread-safe implementation using double-checked locking
     * 
     * @return The single instance of LibraryManager
     */
    public static LibraryManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new LibraryManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize all data structures with thread-safe collections
     */
    private void initializeDataStructures() {
        bookCatalog = new ConcurrentHashMap<>();
        memberRegistry = new ConcurrentHashMap<>();
        borrowingHistory = new ConcurrentHashMap<>();
        reservationQueues = new ConcurrentHashMap<>();
    }
    
    /**
     * Load persisted data from storage
     * Handles exceptions gracefully and initializes with empty data if loading fails
     */
    private void loadPersistedData() {
        try {
            Map<String, Object> data = dataPersistence.loadData();
            if (data != null && !data.isEmpty()) {
                bookCatalog = (Map<String, Book>) data.getOrDefault("books", new ConcurrentHashMap<>());
                memberRegistry = (Map<String, Member>) data.getOrDefault("members", new ConcurrentHashMap<>());
                borrowingHistory = (Map<String, List<String>>) data.getOrDefault("history", new ConcurrentHashMap<>());
                reservationQueues = (Map<String, Queue<String>>) data.getOrDefault("reservations", new ConcurrentHashMap<>());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load persisted data. Starting with empty library.");
            System.err.println("Error details: " + e.getMessage());
        }
    }
    
    /**
     * Save all data to persistent storage
     * 
     * @return true if save was successful, false otherwise
     */
    public boolean saveData() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("books", bookCatalog);
            data.put("members", memberRegistry);
            data.put("history", borrowingHistory);
            data.put("reservations", reservationQueues);
            
            dataPersistence.saveData(data);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Register a component manager with the system
     * This allows team members' components to be integrated
     * 
     * @param componentName The name of the component
     * @param manager The manager instance
     */
    public void registerComponent(String componentName, Object manager) {
        switch (componentName.toLowerCase()) {
            case "book":
                this.bookManager = manager;
                break;
            case "member":
                this.memberManager = manager;
                break;
            case "checkout":
                this.checkoutManager = manager;
                break;
            case "search":
                this.searchManager = manager;
                break;
            default:
                System.err.println("Unknown component: " + componentName);
        }
    }
    
    // ==================== Data Access Methods ====================
    
    /**
     * Get the book catalog
     * @return Map of book IDs to Book objects
     */
    public Map<String, Book> getBookCatalog() {
        return bookCatalog;
    }
    
    /**
     * Get the member registry
     * @return Map of member IDs to Member objects
     */
    public Map<String, Member> getMemberRegistry() {
        return memberRegistry;
    }
    
    /**
     * Get borrowing history
     * @return Map of member IDs to lists of transaction records
     */
    public Map<String, List<String>> getBorrowingHistory() {
        return borrowingHistory;
    }
    
    /**
     * Get reservation queues
     * @return Map of book IDs to queues of member IDs
     */
    public Map<String, Queue<String>> getReservationQueues() {
        return reservationQueues;
    }
    
    /**
     * Add a book to the catalog
     * @param bookId The book ID
     * @param book The Book object
     */
    public void addBook(String bookId, Book book) {
        bookCatalog.put(bookId, book);
        saveData();
    }
    
    /**
     * Remove a book from the catalog
     * @param bookId The book ID
     */
    public void removeBook(String bookId) {
        bookCatalog.remove(bookId);
        reservationQueues.remove(bookId);
        saveData();
    }
    
    /**
     * Add a member to the registry
     * @param memberId The member ID
     * @param member The Member object
     */
    public void addMember(String memberId, Member member) {
        memberRegistry.put(memberId, member);
        borrowingHistory.putIfAbsent(memberId, new ArrayList<>());
        saveData();
    }
    
    /**
     * Remove a member from the registry
     * @param memberId The member ID
     */
    public void removeMember(String memberId) {
        memberRegistry.remove(memberId);
        saveData();
    }
    
    /**
     * Add a reservation to the queue
     * @param bookId The book ID
     * @param memberId The member ID
     */
    public void addReservation(String bookId, String memberId) {
        reservationQueues.putIfAbsent(bookId, new LinkedList<>());
        reservationQueues.get(bookId).offer(memberId);
        saveData();
    }
    
    /**
     * Get the next member in the reservation queue
     * @param bookId The book ID
     * @return The member ID, or null if queue is empty
     */
    public String getNextReservation(String bookId) {
        Queue<String> queue = reservationQueues.get(bookId);
        return (queue != null && !queue.isEmpty()) ? queue.poll() : null;
    }
    
    /**
     * Add a transaction to borrowing history
     * @param memberId The member ID
     * @param transaction The transaction record
     */
    public void addToBorrowingHistory(String memberId, String transaction) {
        borrowingHistory.putIfAbsent(memberId, new ArrayList<>());
        borrowingHistory.get(memberId).add(transaction);
        saveData();
    }
    
    // ==================== Configuration Methods ====================
    
    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }
    
    public void setMaxBorrowDays(int days) {
        this.maxBorrowDays = days;
    }
    
    public int getRenewalDays() {
        return renewalDays;
    }
    
    public void setRenewalDays(int days) {
        this.renewalDays = days;
    }
    
    public int getMaxBooksPerMember() {
        return maxBooksPerMember;
    }
    
    public void setMaxBooksPerMember(int max) {
        this.maxBooksPerMember = max;
    }
    
    public int getAlertDaysBefore() {
        return alertDaysBefore;
    }
    
    public void setAlertDaysBefore(int days) {
        this.alertDaysBefore = days;
    }
    
    // ==================== Component Access Methods ====================
    
    public Object getBookManager() {
        return bookManager;
    }
    
    public Object getMemberManager() {
        return memberManager;
    }
    
    public Object getCheckoutManager() {
        return checkoutManager;
    }
    
    public Object getSearchManager() {
        return searchManager;
    }
    
    /**
     * Shutdown the library system gracefully
     * Saves all data and performs cleanup
     */
    public void shutdown() {
        System.out.println("Shutting down library system...");
        saveData();
        System.out.println("All data saved successfully.");
    }
}
