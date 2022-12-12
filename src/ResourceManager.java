import java.util.ArrayList;
import java.io.*;

/**
 * Project 5 - Resource Manager
 * <p>
 * This is the class that handles concurrency for all shared resources
 *
 * @author Nick Andry, Rei Manning, Lab Sec L15
 * @version December 12, 2022
 */

public class ResourceManager<T> {
    private String filename;
    private Object fileLock;
    private final Object listLock;
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
        synchronized (listLock) {
            return list;
        }
    }

    public T get(int index) {
        synchronized (listLock) {
            return list.get(index);
        }
    }

    public void add(T element) {
        synchronized (listLock) {
            list.add(element);
        }
    }

    public int size() {
        synchronized (listLock) {
            return list.size();
        }
    }

    public boolean remove(T element) {
        synchronized (listLock) {
            return list.remove(element);
        }
    }

    public boolean contains(T element) {
        synchronized (listLock) {
            return list.contains(element);
        }
    }

    public void appendToFile(String toAppend) {
        synchronized (fileLock) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println(toAppend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeListToFile() {
        synchronized (fileLock) {
            synchronized (listLock) {
                try {
                    PrintWriter pw = new PrintWriter(new FileOutputStream(filename, false));
                    for (T element : list) {
                        pw.println(element.toString());
                    }
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<String> readFile() {
        // Create file (does nothing if it already exists)
        synchronized (fileLock) {
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

