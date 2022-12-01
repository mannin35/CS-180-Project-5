import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Project 4 - Account Manager
 * <p>
 * Class that implements methods for logging in/registering a user,
 * as well as some helper methods for finding users. Some fields are also
 * here that are used in other parts of the program, like accounts, buyers,
 * seller, and stores.
 *
 * @author Nick Andry, Lab Sec L15
 * @version November 13, 2022
 */
public class AccountManager {
    private ArrayList<User> accounts;
    private ArrayList<String> buyers;
    private ArrayList<String> sellers;
    private ArrayList<Store> stores;

    public AccountManager() {
        loadAccounts();
        loadStores();
    }

    // Returns the created user object with given user fields
    // Returns null if user already exists or username contains commas
    // Also adds user to accounts.txt and accounts/buyers/sellers arraylists
    public User register(String email, String username, String password, boolean isSeller) {
        if (findUser(username) != null) {
            System.out.println("A user with this username already exists!");
            return null;
        }

        if (username.contains(",")) {
            System.out.println("Username may not contain commas!");
            return null;
        }

        User user = new User(email, password, username, isSeller);

        appendToFile("accounts.txt", user.toString());
        accounts.add(user);
        if (user.isSeller()) {
            sellers.add(user.getUsername());
        } else {
            buyers.add(user.getUsername());
        }

        return user;

    }

    // Returns the user with given username and password
    // Returns null if credentials are wrong
    public User logIn(String username, String password) {
        User foundUser = findUser(username);

        if (foundUser == null) {
            System.out.println("User doesn't exist!");
            return null;
        }

        if (foundUser.getPassword().equals(password)) {
            return foundUser;
        } else {
            System.out.println("Incorrect password for user!");
            return null;
        }
    }

    // Returns the user with given username
    // Returns null if the user doesn't exist
    public User findUser(String username) {
        User foundUser = null;
        for (User user : accounts) {
            if (user.getUsername().equals(username)) {
                foundUser = user;
                break;
            }
        }

        return foundUser;
    }

    // Appends a string to the end of the given file
    private void appendToFile(String filename, String toAppend) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(toAppend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns the lines of the given file
    private ArrayList<String> readFile(String filename) {
        // Create file (does nothing if it already exists)
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

    // Loads information into the stores arraylist
    private void loadStores() {
        stores = new ArrayList<>();

        // Reads the lines of stores.txt
        ArrayList<String> lines = readFile("stores.txt");

        // Parses the file in sections of 2 lines
        for (int i = 0; i < lines.size(); i += 2) {
            String storeName = lines.get(i);
            String sellerName = lines.get(i + 1);

            Store store = new Store(storeName, sellerName);

            // Add parsed store to store arraylist
            stores.add(store);
        }
    }

    // Loads information into the accounts, buyers, and sellers arraylists
    private void loadAccounts() {
        accounts = new ArrayList<>();
        buyers = new ArrayList<>();
        sellers = new ArrayList<>();

        // Reads the lines of accounts.txt
        ArrayList<String> lines = readFile("accounts.txt");

        // Parses file in sections of 6 lines
        for (int i = 0; i < lines.size(); i += 6) {
            String username = lines.get(i);
            String email = lines.get(i + 1);
            String password = lines.get(i + 2);
            boolean isSeller = lines.get(i + 3).equals("seller");
            ArrayList<String> blocked = new ArrayList<>(Arrays.asList(lines.get(i + 4).split(",")));
            ArrayList<String> invisible = new ArrayList<>(Arrays.asList(lines.get(i + 5).split(",")));
            User user = new User(email, password, username, isSeller, blocked, invisible);

            // Add parsed user to each of three arraylists
            accounts.add(user);
            if (isSeller) {
                sellers.add(username);
            } else {
                buyers.add(username);
            }
        }
    }

    // Create a store from seller name and store name
    // Add it to arraylist and stores.txt
    public void createStore(String sellerName, String storeName) {
        Store store = new Store(sellerName, storeName);

        stores.add(store);
        appendToFile("stores.txt", store.toString());
    }

    // Returns the user object that has the given username
    // Returns null if user doesn't exist
    public User getUserFromUsername(String username) {
        User foundUser = null;
        for (User user : accounts) {
            if (user.getUsername().equals(username)) {
                foundUser = user;
            }
        }

        return foundUser;
    }

    public ArrayList<User> getAccounts() {
        return accounts;
    }

    public ArrayList<String> getBuyers() {
        return buyers;
    }

    public ArrayList<String> getSellers() {
        return sellers;
    }

    public ArrayList<Store> getStores() {
        return stores;
    }
}