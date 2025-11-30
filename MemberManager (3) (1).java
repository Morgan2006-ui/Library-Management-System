public class MemberManager {
    private LibraryManager library;

    public MemberManager() {
        this.library = LibraryManager.getInstance();
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
}
