import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        SearchManager gui = new SearchManager();
        
        Scene scene = new Scene(gui.createMainLayout(), 800, 600);
        primaryStage.setTitle("Library System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}