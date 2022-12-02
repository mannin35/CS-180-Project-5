import java.util.ArrayList;
import java.io.*;

public class ResourceManager<T> {
    private String filename;
    private Object fileLock;
    private Object listLock;
    private ArrayList<T> list;

    public ResourceManager(String filename) {
        this.filename = filename;
        this.list = new ArrayList<T>();
        this.fileLock = new Object();
        this.listLock = new Object();
    }
    

    public ResourceManager() {
        this.list = new ArrayList<T>();
        this.listLock = new Object();
    }

    public ArrayList<T> getList() {
        synchronized(listLock) {
            return list;
        }
    }
    public T getAt(int index) {
        synchronized(listLock) {
            return list.get(index);
        }
    }

    public void add(T element) {
        synchronized(listLock) {
            list.add(element);
        }
    }

    public void appendToFile(String toAppend) {
        synchronized(fileLock) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println(toAppend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> readFile() {
        // Create file (does nothing if it already exists)
        synchronized(fileLock) {
            File file = new File(filename);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Read the lines of the given file
            ArrayList<String> lines = new ArrayList<>();
            String line;
            try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
                while ((line = bfr.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return lines;
        }
    }
}

