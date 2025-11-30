import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.stream.Collectors;

public class SearchManager {
	
    private LibraryManager library;
    private BookManager bookManager;
    private MemberManager memberManager;
    private CheckoutManager checkoutManager;
    private ListView<Book> bookListView;
    private ListView<Member> memberListView;
    private TextField searchField;
   
    
    public SearchManager() {
    	
        this.library = LibraryManager.getInstance();
        this.bookManager = new BookManager();
        this.memberManager = new MemberManager();
        this.checkoutManager = new CheckoutManager();
    }

    
    
    
    public BorderPane createMainLayout() {
        TabPane tabPane = new TabPane();
        
        tabPane.getTabs().add(new Tab("Books", createBookTab()));
        tabPane.getTabs().add(new Tab("Members", createMemberTab()));
        tabPane.getTabs().add(new Tab("Circulation", createCirculationTab()));
        
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        return root;
    }

    
    
    
    private VBox createBookTab() {
        VBox layout = new VBox(10);
        
        
        layout.setPadding(new Insets(10));

        searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.setOnKeyReleased(e -> updateBookList(searchField.getText()));

        bookListView = new ListView<>();
        updateBookList("");

        
        TextField titleInput = new TextField(); titleInput.setPromptText("Title");
        TextField authorInput = new TextField(); authorInput.setPromptText("Author");
        Button addButton = new Button("Add Book");
 
        
        
        addButton.setOnAction(e -> {
            if (!titleInput.getText().isEmpty() && !authorInput.getText().isEmpty()) {
                bookManager.addBook(titleInput.getText(), authorInput.getText(), "ISBN-STUB");
                updateBookList("");
                titleInput.clear(); authorInput.clear();
            }
        });
        
        

        layout.getChildren().addAll(new Label("Catalog"), searchField, bookListView, 
        new Separator(), new Label("Add Book"), titleInput, authorInput, addButton);
        return layout;
    }
    
    
    
    
    private VBox createMemberTab() {
    	
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        memberListView = new ListView<>();
        updateMemberList();

        
        
        TextField nameInput = new TextField(); nameInput.setPromptText("Member Name");
        TextField emailInput = new TextField(); emailInput.setPromptText("Email");
        Button registerButton = new Button("Register Member");

        
        
        registerButton.setOnAction(e -> {
            if (!nameInput.getText().isEmpty()) {
                memberManager.registerMember(nameInput.getText(), emailInput.getText());
                updateMemberList();
                nameInput.clear(); emailInput.clear();
            }
        });

        
        
        layout.getChildren().addAll(new Label("Members"), memberListView, 
                                  new Separator(), new Label("New Member"), 
                                  nameInput, emailInput, registerButton);
        return layout;
    }

    
    
    private VBox createCirculationTab() {
    	
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        
        Button checkoutButton = new Button("Checkout Book to Member");
        Button returnButton = new Button("Return Selected Book");

        
        
        checkoutButton.setOnAction(e -> {
            Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
            Member selectedMember = memberListView.getSelectionModel().getSelectedItem();
            
            
            
            if (selectedBook != null && selectedMember != null) {
                if (checkoutManager.checkoutBook(selectedBook.getBookId(), selectedMember.getMemberId())) {
                    showAlert("Success", "Book checked out to " + selectedMember.getName());
                    refreshAll();
                } else {
                    showAlert("Error", "Book is not available.");
                }
            } else {
                showAlert("Selection Error", "Please select a book AND a member.");
            }
        });

        
        
        returnButton.setOnAction(e -> {
            Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                checkoutManager.returnBook(selectedBook.getBookId());
                showAlert("Success", "Book returned.");
                refreshAll();
            }
        });

        
        
        
        layout.getChildren().addAll(new Label("Circulation Actions"), 
                                  new Label("Select a Book from the 'Books' tab and a Member from 'Members' tab."),
                                  checkoutButton, returnButton);
        return layout;
    }

    
    
    
    private void updateBookList(String query) {
        
    	
    	if (query == null || query.isEmpty()) {
            bookListView.setItems(FXCollections.observableArrayList(library.getBookCatalog().values()));
        } else {
            var filtered = library.getBookCatalog().values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
            bookListView.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    
    
    private void updateMemberList() {
        memberListView.setItems(FXCollections.observableArrayList(library.getMemberRegistry().values()));
    }
    
    
    
    
    private void refreshAll() {
        updateBookList("");
        updateMemberList();
    }

    
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}