public class BookManager {
    private LibraryManager library;
    
    public BookManager() {
        this.library = LibraryManager.getInstance();
    }

    public void addBook(String title, String author, String isbn) {
        String id = "B" + (library.getBookCatalog().size() + 100); 
        Book newBook = new Book(id, title, author, isbn);
        library.getBookCatalog().put(id, newBook);
        library.saveData();
    }

    public void removeBook(String bookId) {
        library.getBookCatalog().remove(bookId);
        library.saveData();
    }
    
    public void reserveBook(String bookId, String memberId) {
        library.addReservation(bookId, memberId);
    }
    
    public String getNextInQueue(String bookId) {
        return library.getNextReservation(bookId);
    }
}
