import java.util.*;

/**
 * Project 4 - src.Store
 * <p>
 * Class with all the information a user has (email, password, etc.)
 * and methods that are used for sending/editing/deleting messages
 * during the program.
 *
 * @author Rei Manning, Lab Sec L15
 * @version November 13th, 2022
 */

public class User {
    private String email;
    private String password;
    private String username;
    private boolean isSeller;
    private ArrayList<String> blocked;
    private ArrayList<String> invisibleTo;

    // Constructor for new Users (no previous conversation history or
    // blocked/invisble users)
    public User(String email, String password, String username, boolean isSeller) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isSeller = isSeller;
        this.blocked = new ArrayList<String>();
        this.invisibleTo = new ArrayList<String>();
    }

    //Constructor for users with previous history (from file)
    public User(String email, String password, String username, boolean isSeller,
                ArrayList<String> blocked, ArrayList<String> invisibleTo) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isSeller = isSeller;
        this.blocked = blocked;
        this.invisibleTo = invisibleTo;
    }

    //Takes new message String and arraylist of messages from each src.User. Adds new Message to both arraylists
    public void sendMessage(String message, ArrayList<Message> user, ArrayList<Message> recipient) {
        Message newMessage;
        if (user.isEmpty()) {
            newMessage = new Message(this.username, message, 1);
        } else {
            newMessage = new Message(this.username, message, user.get(user.size() - 1).getMessageID() + 1);
        }
        user.add(newMessage);
        recipient.add(newMessage);
    }

    //Takes messageID and removes message from arraylist with matching ID
    public boolean deleteMessage(int messageID, ArrayList<Message> user) {
        boolean idExists = false;
        for (int i = 0; i < user.size(); i++) {
            if (user.get(i).getMessageID() == messageID) {
                user.remove(i);
                idExists = true;
            }
        }
        return idExists;
    }

    //Takes messageID, newMessage String, and message arraylists of user and recipient
    //Loops through each arraylist until finding message with correct ID,
    //Then sets that element to be a Message with updated message String
    public boolean editMessage(int messageID, String newMessage, ArrayList<Message> user,
                               ArrayList<Message> recipient, User editor) {
        boolean idExists = false;
        boolean edited = false;
        for (int i = 0; i < user.size(); i++) {
            if (user.get(i).getMessageID() == messageID) {
                if (user.get(i).getUsername().equals(editor.getUsername())) {
                    user.set(i, new Message(username, newMessage, messageID));
                    edited = true;
                } else {
                    System.out.println("You may only edit messages that you have sent!");
                }
                idExists = true;
            }
        }

        if (edited) {
            for (int i = 0; i < recipient.size(); i++) {
                if (recipient.get(i).getMessageID() == messageID) {
                    recipient.set(i, new Message(username, newMessage, messageID));
                }
            }
        }
        if (!idExists) {
            System.out.println("Please enter a valid message ID!");
        }
        return edited;
    }

    //Returns String representation of user
    public String toString() {
        String toString;
        String buyerSeller;
        String blockedString = "";
        String invisibleString = "";
        if (isSeller)
            buyerSeller = "seller";
        else
            buyerSeller = "buyer";


        blockedString = String.join(",", blocked);
        invisibleString = String.join(",", invisibleTo);

        toString = String.format("%s%n%s%n%s%n%s%n%s%n%s", username, email, password,
                buyerSeller, blockedString, invisibleString);
        return toString;
    }

    //Adds username of blocked user to this user's blocked list
    //Replaces setter method for blocked 
    public void blockUser(User otherUser) {
        blocked.add(otherUser.getUsername());
    }

    //Adds username of other user to this user's invisbleTo list
    public void addInvisible(User otherUser) {
        invisibleTo.add(otherUser.getUsername());
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSeller() {
        return this.isSeller;
    }

    public ArrayList<String> getBlocked() {
        return this.blocked;
    }

    public ArrayList<String> getinvisibleTo() {
        return this.invisibleTo;
    }

}