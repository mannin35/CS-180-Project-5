import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
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
    public static ResourceManager<User> accounts;
    public static ResourceManager<String> buyers;
    public static ResourceManager<String> sellers;
    public static ResourceManager<Store> stores;

    static {
        accounts = new ResourceManager<>("accounts.txt");
        buyers = new ResourceManager<>();
        sellers = new ResourceManager<>();
        stores = new ResourceManager<>("stores.txt");

        loadAccounts();
        loadStores();
    }

    // Returns the created user object with given user fields
    // Returns null if user already exists or username contains commas
    // Also adds user to accounts.txt and accounts/buyers/sellers arraylists
    public static User register(String email, String username, String password, boolean isSeller, PrintWriter writer) {
        if (findUser(username) != null) {
            ServerProcessor.sendMessage(writer, "A user with this username already exists!", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (username.contains(",")) {
            ServerProcessor.sendMessage(writer, "Username may not contain commas!", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        User user = new User(email, password, username, isSeller);

        accounts.appendToFile(user.toString());
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
    public static User logIn(String username, String password, PrintWriter writer) {
        User foundUser = findUser(username);

        if (foundUser == null) {
            ServerProcessor.sendMessage(writer, "User doesn't exist!", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (foundUser.getPassword().equals(password)) {
            return foundUser;
        } else {
           ServerProcessor.sendMessage(writer, "Incorrect password for user!", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Returns the user with given username
    // Returns null if the user doesn't exist
    public static User findUser(String username) {
        User foundUser = null;
        for (User user : accounts.getList()) {
            if (user.getUsername().equals(username)) {
                foundUser = user;
                break;
            }
        }

        return foundUser;
    }

    // Loads information into the stores arraylist
    private static void loadStores() {
        stores = new ResourceManager<>("stores.txt");

        // Reads the lines of stores.txt
        ArrayList<String> lines = stores.readFile();

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
    private static void loadAccounts() {

        // Reads the lines of accounts.txt
        ArrayList<String> lines = accounts.readFile();

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
    public static void createStore(String sellerName, String storeName) {
        Store store = new Store(sellerName, storeName);

        stores.add(store);
        stores.appendToFile(store.toString());
    }

    // Returns the user object that has the given username
    // Returns null if user doesn't exist
    public static User getUserFromUsername(String username) {
        User foundUser = null;
        for (User user : accounts.getList()) {
            if (user.getUsername().equals(username)) {
                foundUser = user;
            }
        }

        return foundUser;
    }
}