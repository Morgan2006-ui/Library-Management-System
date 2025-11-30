import java.time.LocalDate;

public class CheckoutManager {
    private LibraryManager library;

    public CheckoutManager() {
        this.library = LibraryManager.getInstance();
    }

    
    
    public boolean checkoutBook(String bookId, String memberId) {
        Book book = library.getBookCatalog().get(bookId);
        Member member = library.getMemberRegistry().get(memberId);

        if (book == null || member == null) return false;
        if (!book.isAvailable()) return false;

        book.setAvailable(false);
        book.setCurrentBorrowerId(memberId);
        book.setDueDate(LocalDate.now().plusDays(14));
       
        member.getBorrowedBooks().add(bookId);
        
        library.addToHistory(memberId, "Checked out " + book.getTitle());
        library.saveData();
        return true;
    }

    
    
    
    public void returnBook(String bookId) {
        Book book = library.getBookCatalog().get(bookId);
        if (book != null && !book.isAvailable()) {
            String memberId = book.getCurrentBorrowerId();
            Member member = library.getMemberRegistry().get(memberId);
            book.setAvailable(true);
            book.setCurrentBorrowerId(null);
            book.setDueDate(null);
            
            if (member != null) {
                member.getBorrowedBooks().remove(bookId);
            }
            
            if (memberId != null) {
                library.addToHistory(memberId, "Returned " + book.getTitle());
            }
            library.saveData();
        }
    }
}