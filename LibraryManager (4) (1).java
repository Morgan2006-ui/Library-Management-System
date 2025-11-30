import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryManager {
   
    private static LibraryManager instance;
    private Map<String, Book> bookCatalog = new ConcurrentHashMap<>();
    private Map<String, Member> memberRegistry = new ConcurrentHashMap<>();
    private Map<String, List<String>> borrowingHistory = new ConcurrentHashMap<>();
    private DataPersistence dataPersistence = new DataPersistence();
    private LibraryManager() {
        loadData();
    }
    
    
    
    
    public static synchronized LibraryManager getInstance() {
        if (instance == null) instance = new LibraryManager();
        return instance;
    }

    
    
    
    public Map<String, Book> getBookCatalog() { return bookCatalog; }
    public Map<String, Member> getMemberRegistry() { return memberRegistry; }
    public Map<String, List<String>> getBorrowingHistory() { return borrowingHistory; }
    
    public void saveData() {
    	
        HashMap<String, Object> data = new HashMap<>();
        data.put("books", new HashMap<>(bookCatalog));
        data.put("members", new HashMap<>(memberRegistry));
        data.put("history", new HashMap<>(borrowingHistory));
        dataPersistence.saveData(data);
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        Map<String, Object> data = dataPersistence.loadData();
        if (data != null) {
            if (data.containsKey("books")) 
                bookCatalog.putAll((Map<String, Book>) data.get("books"));
            if (data.containsKey("members")) 
                memberRegistry.putAll((Map<String, Member>) data.get("members"));
            if (data.containsKey("history")) 
                borrowingHistory.putAll((Map<String, List<String>>) data.get("history"));
        }
    }
    
    
    
    
    public void addToHistory(String memberId, String message) {
        borrowingHistory.computeIfAbsent(memberId, k -> new ArrayList<>()).add(message);
        saveData();
    }
    
    public List<String> getHistory(String memberId) {
        return borrowingHistory.getOrDefault(memberId, new ArrayList<>());
    }
    
    public List<Book> getOutstandingBooks() {
        List<Book> outstanding = new ArrayList<>();
        for (Book book : bookCatalog.values()) {
            if (!book.isAvailable()) {
                outstanding.add(book);
            }
        }
        return outstanding;
    }
}
