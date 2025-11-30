import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataPersistence {
    private static final String DATA_FILE = "library_data.ser";

    
    
    
    public void saveData(Map<String, Object> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(data);
            System.out.println("Data saved to disk.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Object>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Starting with empty database.");
            return new HashMap<>();
        }
    }
}