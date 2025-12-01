import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class MemberManager {
    private LibraryManager library;
    private Timer alertTimer;

    public MemberManager() {
        this.library = LibraryManager.getInstance();
        startAlertSystem();
    }

    public void registerMember(String name, String email) {
    	String id = "M" + (library.getMemberRegistry().size() + 100);
        Member newMember = new Member(id, name, email);
        library.getMemberRegistry().put(id, newMember);
        library.saveData();
    }
    
    public void removeMember(String memberId) {
        library.getMemberRegistry().remove(memberId);
        library.saveData();
    }
    
    private void startAlertSystem() {
        alertTimer = new Timer(true);
        alertTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkDueDates();
            }
        }, 0, 3600000);
    }
    
    private void checkDueDates() {
        LocalDate today = LocalDate.now();
        for (Book book : library.getBookCatalog().values()) {
            if (!book.isAvailable() && book.getDueDate() != null) {
                long daysUntilDue = ChronoUnit.DAYS.between(today, book.getDueDate());
                if (daysUntilDue <= 3 && daysUntilDue >= 0) {
                    String memberId = book.getCurrentBorrowerId();
                    Member member = library.getMemberRegistry().get(memberId);
                    if (member != null) {
                        String alert = "ALERT: " + book.getTitle() + " is due in " + daysUntilDue + " days";
                        System.out.println(alert + " for " + member.getName());
                    }
                }
            }
        }
    }
    
    public void stopAlertSystem() {
        if (alertTimer != null) {
            alertTimer.cancel();
        }
    }
}
