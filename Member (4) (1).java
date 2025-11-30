import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private String memberId;
    private String name;
    private String email;
    private List<String> borrowedBooks;

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public List<String> getBorrowedBooks() { return borrowedBooks; }
    
    @Override
    public String toString() { return name + " (ID: " + memberId + ")"; }
}