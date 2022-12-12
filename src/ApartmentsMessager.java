import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

/*
To do:
1. When the User presses the 'X' button it needs to be handled [returns zero]; handled on 'Client.java'
2. When the User wants to delete a message, BUT they click out and it gets returned as message with messageID 0; handled on 'Client.java'
3. Discrepancy in showMessageDialog [ERROR_MESSAGE VS INFORMATION_MESSAGE]; fixed
 */

public class ApartmentsMessager {

    private User currentUser;
    private User recipient;

    public ApartmentsMessager() {
        currentUser = null;
        recipient = null;
    }

    // Server class
    public static void main(String[] args) {
        // Adapted from geeksforgeeks.org
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(4242);

            while (true) {
                Socket client = serverSocket.accept();
                
                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client);


                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            PrintWriter writer = null;
            BufferedReader reader = null;

            ApartmentsMessager main = new ApartmentsMessager();

            try {

                // get the outputstream of client
                writer = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                // get the inputstream of client
                reader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                boolean loggedIn = false;

                ServerProcessor.sendMessage(writer, "Welcome to L15 Apartments Messager!", JOptionPane.PLAIN_MESSAGE);
                while (!loggedIn) {
                    String login = ServerProcessor.sendOptions(writer, reader, "1. Login\n2. Register",
                            new String[] {"1", "2"});
                    //If they choose to login, check that username already exists and password matches to move on
                    if (login.equals("1")) {
                        String username = ServerProcessor.sendInput(writer, reader, "Enter username: ");
                        String password = ServerProcessor.sendInput(writer, reader, "Enter password: ");
                        main.currentUser = AccountManager.logIn(username, password, writer);
                        if (main.currentUser != null) {
                            loggedIn = true;
                        }
                        //Check if a username with name already exists. If not, register new user
                    } else if (login.equals("2")) {
                        String email = ServerProcessor.sendInput(writer, reader, "Enter email: ");
                        String username = ServerProcessor.sendInput(writer, reader, "Enter username: ");
                        String password = ServerProcessor.sendInput(writer, reader, "Enter password: ");
                        String buyerOrSeller;
                        boolean isSeller = false;
                        do {
                            buyerOrSeller = ServerProcessor.sendInput(writer, reader, "Would you like to register as a buyer or a seller? (B/S)");
                            if (buyerOrSeller.equalsIgnoreCase("S")) {
                                isSeller = true;
                            } else if (buyerOrSeller.equalsIgnoreCase("B")) {
                                isSeller = false;
                            } else {
                                ServerProcessor.sendMessage(writer, "Please type B for buyer or S for seller!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!(buyerOrSeller.equalsIgnoreCase("S")) && !buyerOrSeller.equalsIgnoreCase("B"));
                        main.currentUser = AccountManager.register(email, username, password, isSeller, writer);
                        if (main.currentUser != null) {
                            loggedIn = true;
                        }

                    } else if (login.equals("0")) {
                        break;
                    } else {
                        ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (loggedIn) {
                    ServerProcessor.sendMessage(writer, "You are now logged in as "
                            + main.currentUser.getUsername(), JOptionPane.PLAIN_MESSAGE);
                    //logged in as seller
                    if (main.currentUser.isSeller()) {
                        //removes buyers who chose to be invisible to currently logged in seller from list of buyers
                        for (int i = 0; i < AccountManager.accounts.size(); i++) {
                            User c = AccountManager.accounts.get(i);
                            if (c.getinvisibleTo().contains(main.currentUser.getUsername())) {
                                AccountManager.buyers.remove(c.getUsername());
                            }
                        }
                        //Main menu
                        //Will keep looping until user chooses to quit
                        boolean mainMenu = true;
                        while (mainMenu) {
                            main.setRecipient(null);
                            String menuChoice = ServerProcessor.sendOptions(writer, reader, "What would you like to do?" +
                                            "\n1. Search for a buyer to message" +
                                            "\n2. Create a store" +
                                            "\n3. Block a user" +
                                            "\n4. Become invisible to a user",
                                    new String[]{"1", "2", "3", "4"});
                            //Find user to message
                            if (menuChoice.equals("1")) {
                                if (AccountManager.buyers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no registered buyers for you to message.",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String buyerSearch = "";
                                    //loops until user successfully chooses a person to message
                                    while (main.recipient == null && !buyerSearch.equals("3")) {
                                        buyerSearch = ServerProcessor.sendOptions(writer, reader, "Would you like to:\n" +
                                                        "1. See list of buyers\n" +
                                                        "2. Search for a buyer",
                                                new String[]{"1", "2"});
                                        if (buyerSearch.equals("1")) {
                                            String buyerList = "";
                                            for (int i = 0; i < AccountManager.buyers.size() - 1; i++) {
                                                buyerList += AccountManager.buyers.get(i);
                                                buyerList += "\n";
                                            }
                                            buyerList += AccountManager.buyers.get(AccountManager.buyers.size() - 1);
                                            do {
                                                String buyerChoice = ServerProcessor.sendInput(writer, reader,
                                                        buyerList + "\nWhich buyer would you like to message?");
                                                if (AccountManager.buyers.contains(buyerChoice)) {
                                                    main.setRecipient(AccountManager.getUserFromUsername(buyerChoice));
                                                }
                                                if (main.recipient == null) {
                                                    ServerProcessor.sendMessage(writer, "Please enter a user from the list", JOptionPane.ERROR_MESSAGE);
                                                } else if (main.recipient.getBlocked().contains(main.currentUser.getUsername())) {
                                                    String errorMessage = "You have been blocked by " +
                                                            main.recipient.getUsername() + " and may not message them";
                                                    ServerProcessor.sendMessage(writer, errorMessage, JOptionPane.ERROR_MESSAGE);
                                                    main.setRecipient(null);
                                                    break;
                                                }
                                            } while (main.recipient == null);
                                        } else if (buyerSearch.equals("2")) {
                                            String buyerChoice = ServerProcessor.sendInput(writer, reader,
                                                    "Please enter the username of the buyer " +
                                                            "you would like to message");
                                            if (AccountManager.buyers.contains(buyerChoice)) {
                                                main.setRecipient(AccountManager.getUserFromUsername(buyerChoice));
                                            }
                                            if (main.recipient == null) {
                                                String nonexistentUser = "There does not exist a user with the username " +
                                                        buyerChoice;
                                                ServerProcessor.sendMessage(writer, nonexistentUser, JOptionPane.ERROR_MESSAGE);
                                            } else if (main.recipient.getBlocked().contains(main.currentUser.getUsername())) {
                                                String errorMessage = "You have been blocked by " +
                                                        main.recipient.getUsername() + " and may not message them";
                                                ServerProcessor.sendMessage(writer, errorMessage, JOptionPane.ERROR_MESSAGE);
                                                main.setRecipient(null);
                                            }

                                        } else if (buyerSearch.equals("0")) {
                                            break;
                                        } else {
                                            ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }

                                    }
                                    //Confirms the recipient and reads in the conversations
                                    if (!buyerSearch.equals("3")) {
                                        String recipientMessage = "You are now messaging " + main.recipient.getUsername();
                                        ServerProcessor.sendMessage(writer, recipientMessage, JOptionPane.PLAIN_MESSAGE);
                                        String messageName =
                                                main.currentUser.getUsername() + "-" + main.recipient.getUsername() + ".txt";
                                        String recipientMessageName =
                                                main.recipient.getUsername() + "-" + main.currentUser.getUsername() + ".txt";

                                        //Message Menu
                                        while (true) {
                                            String messageMenuChoice = ServerProcessor.sendOptions(writer, reader,
                                                    "Would you like to:" +
                                                            "\n1. View message history" +
                                                            "\n2. Send a new message" +
                                                            "\n3. Edit a message" +
                                                            "\n4. Delete a message" +
                                                            "\n5. Export message history" +
                                                            "\n6. Go back to the Main Menu",
                                                    new String[]{"1", "2", "3", "4", "5", "6"});
                                            //Prints out message history of user
                                            if (messageMenuChoice.equals("1")) {
                                                String messageHistory = "";
                                                for (Message m : main.getCurrentConvo()) {
                                                    messageHistory = messageHistory + m.toString() + "\n";
                                                }
                                                if (messageHistory == "") {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to view.", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    ServerProcessor.sendMessage(writer, messageHistory, JOptionPane.PLAIN_MESSAGE);
                                                }
                                                //send message
                                            } else if (messageMenuChoice.equals("2")) {
                                                String messageType = "";
                                                do {
                                                    messageType = ServerProcessor.sendOptions(writer, reader,
                                                            "Would you like to:",
                                                            new String[]{"Send a message", "Send a file"});
                                                    if (messageType.equals("1")) {
                                                        String newMessage = "";
                                                        while (newMessage.equals("") || newMessage == null) {
                                                            newMessage = ServerProcessor.sendInput(writer, reader,
                                                                "Please enter the message you would like to enter");
                                                            if (newMessage.equals("") || newMessage == null) {
                                                                ServerProcessor.sendMessage(writer, "Messages cannot be empty!", JOptionPane.ERROR_MESSAGE);
                                                            }
                                                        }
                                                        ConversationManager.sendMessage(main.currentUser, main.recipient, newMessage);
                                                    } else if (messageType.equals("2")) {
                                                        String fileMessage = "";
                                                        while (fileMessage.equals("")) {
                                                            String importFileName = ServerProcessor.sendInput(writer, reader,
                                                                    "Please enter the name of the file you would " +
                                                                            "like to import");
                                                            fileMessage = FileImportExport.importFile(importFileName, writer, reader);
                                                            if (!fileMessage.equals("")) {
                                                                ConversationManager.sendMessage(main.currentUser, main.recipient, fileMessage);
                                                            }
                                                        }
                                                    } else {
                                                        ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                } while (!messageType.equals("1") && !messageType.equals("2"));
                                                ServerProcessor.sendMessage(writer, "Message sent successfully",
                                                        JOptionPane.PLAIN_MESSAGE);
                                                //edit message
                                            } else if (messageMenuChoice.equals("3")) {
                                                if (main.getCurrentConvo().size() == 0) {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to edit.", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    String messageHistory = "";
                                                    for (int i = 0; i < main.getCurrentConvo().size() - 1; i++) {
                                                        messageHistory += main.getCurrentConvo().get(i);
                                                        messageHistory += "\n";
                                                    }
                                                    messageHistory += main.getCurrentConvo().get(main.getCurrentConvo().size() - 1);
                                                    messageHistory += "\n";
                                                    boolean messageEdited = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessor.sendInput(writer, reader,
                                                            messageHistory + "\nPlease enter the message ID of the message you would " +
                                                                    "like to edit");
                                                    //ignores if user enters string instead of int because the messageID is already set to -1,
                                                    //which cannot exist in the convo, and will automatically go to error line if not changed to
                                                    //a valid message id
                                                    try {
                                                        messageID = Integer.parseInt(messageIDString);
                                                    } catch (InputMismatchException ignored) {

                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.getCurrentConvo()) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessor.sendMessage(writer, "Please enter a valid message ID!",
                                                                JOptionPane.ERROR_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        String editedMessage = ServerProcessor.sendInput(writer, reader,
                                                                "What would you like to edit the message to?");
                                                        messageEdited = ConversationManager.editMessage(main.currentUser, main.recipient,
                                                                messageID, editedMessage, writer);
                                                    }
                                                    if (messageEdited) {
                                                        ServerProcessor.sendMessage(writer, "Message edited successfully!",
                                                                JOptionPane.PLAIN_MESSAGE);
                                                    }

                                                }
                                                //delete message
                                            } else if (messageMenuChoice.equals("4")) {
                                                if (main.getCurrentConvo().size() == 0) {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to delete.", JOptionPane.PLAIN_MESSAGE);
                                                } else {
                                                    String messageHistory = "";
                                                    for (int i = 0; i < main.getCurrentConvo().size() - 1; i++) {
                                                        messageHistory += main.getCurrentConvo().get(i);
                                                        messageHistory += "\n";
                                                    }
                                                    messageHistory += main.getCurrentConvo().get(main.getCurrentConvo().size() - 1);
                                                    messageHistory += "\n";
                                                    boolean messageDeleted = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessor.sendInput(writer, reader,
                                                            messageHistory + "\nPlease enter the message ID of the message you would " +
                                                                    "like to delete");
                                                    try {
                                                        messageID = Integer.parseInt(messageIDString);
                                                    } catch (InputMismatchException ignored) {
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.getCurrentConvo()) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessor.sendMessage(writer, "Please enter a valid message ID!", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        messageDeleted = ConversationManager.deleteMessage(main.currentUser, main.recipient, messageID);
                                                    }
                                                    if (messageDeleted) {
                                                        ServerProcessor.sendMessage(writer, "Message deleted successfully!", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                                //export conversation history
                                            } else if (messageMenuChoice.equals("5")) {
                                                FileImportExport.exportCSV(main.currentUser.getUsername(), main.recipient.getUsername(),
                                                        main.getCurrentConvo(), writer, reader);
                                                ServerProcessor.sendMessage(writer, "Conversation exported successfully!", JOptionPane.PLAIN_MESSAGE);
                                            } else if (messageMenuChoice.equals("6")) {
                                                break;
                                            } else if (messageMenuChoice.equals("0")) {
                                                mainMenu = false;
                                                break;
                                            } else {
                                                ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }

                                    }
                                }
                                //create store
                            } else if (menuChoice.equals("2")) {
                                String store = ServerProcessor.sendInput(writer, reader,
                                        "Please enter the name of the store you would like to create");
                                AccountManager.createStore(main.currentUser.getUsername(), store);
                                ServerProcessor.sendMessage(writer, "Successfully created store!",
                                        JOptionPane.PLAIN_MESSAGE);
                                //block user
                            } else if (menuChoice.equals("3")) {
                                if (AccountManager.buyers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no registered buyers for you to block.",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String block = ServerProcessor.sendInput(writer, reader,
                                            "Please enter the username of the user you would like to block");
                                    User blockUser = AccountManager.getUserFromUsername(block);
                                    if (blockUser != null) {
                                        main.currentUser.blockUser(blockUser);
                                        String blockMessage = block + " is now blocked";
                                        ServerProcessor.sendMessage(writer, blockMessage, JOptionPane.PLAIN_MESSAGE);
                                    } else {
                                        String blockUserError = "There does not exist a user with the username " + block;
                                        ServerProcessor.sendMessage(writer, blockUserError, JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                //become invisible to a user
                            } else if (menuChoice.equals("4")) {
                                if (AccountManager.buyers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no registered buyers " +
                                            "for you to become invisible to.", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String invisibleTo = ServerProcessor.sendInput(writer, reader,
                                            "Please enter the name of the user you would " +
                                                    "like to become invisible to");

                                    User invisibleToUser = AccountManager.getUserFromUsername(invisibleTo);
                                    if (invisibleToUser != null) {
                                        main.currentUser.addInvisible(invisibleToUser);
                                        String invisibleToMessage = "You are now invisible to " + invisibleTo;
                                        ServerProcessor.sendMessage(writer, invisibleToMessage, JOptionPane.PLAIN_MESSAGE);
                                    } else {
                                        String invisibleToErrorMessage = "There does not exist a user with the username " + invisibleTo;
                                        ServerProcessor.sendMessage(writer, invisibleToErrorMessage, JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                //quit
                            } else if (menuChoice.equals("0")) {
                                break;
                            } else {
                                ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        //if user is a buyer
                    } else if (!main.currentUser.isSeller()) {
                        //removes stores whose sellers chose to become invisible to currently logged in buyer from list of
                        // stores
                        for (int i = 0; i < AccountManager.accounts.size(); i++) {
                            User c = AccountManager.accounts.get(i);
                            if (c.getinvisibleTo().contains(main.currentUser.getUsername())) {
                                for (int j = 0; j < AccountManager.stores.size(); j++) {
                                    Store s = AccountManager.stores.get(j);
                                    if (s.getSeller().equals(c.getUsername())) {
                                        AccountManager.stores.remove(s);
                                    }
                                }
                            }
                        }
                        //main menu
                        boolean mainMenu = true;
                        while (mainMenu) {
                            main.setRecipient(null);
                            String menuChoice = ServerProcessor.sendOptions(writer, reader, "What would you like to do?\n1. Message a seller\n2. Block a user\n" +
                                    "3. Become invisible to a user", new String[] {"1", "2", "3"});
                            //FInd a user to message
                            if (menuChoice.equals("1")) {
                                if (AccountManager.sellers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no sellers registered for you to message.", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String buyerSearch = "";
                                    while (main.recipient == null && !buyerSearch.equals("3")) {
                                        buyerSearch = ServerProcessor.sendOptions(writer, reader, "Would you like to:\n1. See a list of stores\n2. Search for a seller",
                                                new String[] {"1", "2"});
                                        //search by store
                                        if (buyerSearch.equals("1")) {
                                            if (AccountManager.stores.size() == 0) {
                                                ServerProcessor.sendMessage(writer, "There are currently no stores available to message.", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                String storeListString = "";
                                                for (int i = 0; i < AccountManager.stores.size(); i++) {
                                                    storeListString = storeListString + AccountManager.stores.get(i).getName() + "\n";
                                                }
                                                ServerProcessor.sendMessage(writer, storeListString, JOptionPane.PLAIN_MESSAGE);
                                                do {
                                                    String storeChoice = ServerProcessor.sendInput(writer, reader, "Which store would you like to message?");
                                                    //searches through stores objects until one of the store names matches
                                                    // the search. THen, gets the seller who owns that store
                                                    for (int i = 0; i < AccountManager.stores.size(); i++) {
                                                        if (AccountManager.stores.get(i).getName().equalsIgnoreCase(storeChoice)) {
                                                            main.setRecipient(AccountManager
                                                                    .getUserFromUsername(AccountManager.stores.get(i).getSeller()));
                                                        }
                                                    }
                                                    if (main.recipient == null) {
                                                        ServerProcessor.sendMessage(writer, "Please enter a store from the list.", JOptionPane.ERROR_MESSAGE);
                                                    } else if (main.recipient.getBlocked()
                                                            .contains(main.currentUser.getUsername())) {
                                                        ServerProcessor.sendMessage(writer, "You have been blocked by the owner of " +
                                                                storeChoice + " and may not message them", JOptionPane.ERROR_MESSAGE);
                                                        main.setRecipient(null);
                                                        break;
                                                    }
                                                } while (main.recipient == null);
                                            }
                                            //search by username
                                        } else if (buyerSearch.equals("2")) {
                                            String sellerChoice  = ServerProcessor.sendInput(writer, reader, "Please enter the username of the seller you would like to " +
                                                    "message");
                                            if (AccountManager.sellers.contains(sellerChoice)) {
                                                main.setRecipient(AccountManager.getUserFromUsername(sellerChoice));
                                            }
                                            if (main.recipient == null) {
                                                ServerProcessor.sendMessage(writer, "There does not exist a user with the username "
                                                        + sellerChoice, JOptionPane.ERROR_MESSAGE);
                                            } else if (main.recipient.getBlocked().contains(main.currentUser.getUsername())) {
                                                ServerProcessor.sendMessage(writer, "You have been blocked by " +
                                                        main.recipient.getUsername() + " and may not message them", JOptionPane.ERROR_MESSAGE);
                                                main.setRecipient(null);
                                            }
                                            //quit
                                        } else if (buyerSearch.equals("3")) {
                                            break;
                                        } else {
                                            ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                    //Confirm recipient and read in message txt files
                                    if (!buyerSearch.equals("3")) {
                                        ServerProcessor.sendMessage(writer, "You are now messaging " + main.recipient.getUsername(), JOptionPane.PLAIN_MESSAGE);

                                        //Message Menu
                                        while (true) {
                                            String messageMenuChoice = ServerProcessor.sendOptions(writer, reader, "Would you like to:\n1. View message history\n2. Send a new message\n" +
                                                            "3. Edit a message\n4. Delete a message\n5. Export message history\n6. Go back to the Main Menu",
                                                    new String[] {"1", "2", "3", "4", "5", "6"});
                                            //print out message history
                                            if (messageMenuChoice.equals("1")) {
                                                String currentConvoString = "";
                                                for (Message m : main.getCurrentConvo()) {
                                                    currentConvoString = currentConvoString + m.toString() + "\n";
                                                }
                                                if (currentConvoString == "") {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to view.", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    ServerProcessor.sendMessage(writer, currentConvoString, JOptionPane.PLAIN_MESSAGE);
                                                }
                                                //send message
                                            } else if (messageMenuChoice.equals("2")) {
                                                String messageType = "";
                                                do {
                                                    messageType = ServerProcessor.sendOptions(writer, reader, "Would you like to:\n1. Send a message\n2. Send a file",
                                                            new String[] {"1", "2"});
                                                    //send new message by typing input
                                                    if (messageType.equals("1")) {
                                                        String newMessage = "";
                                                        while (newMessage.equals("") || newMessage == null) {
                                                            newMessage = ServerProcessor.sendInput(writer, reader,
                                                                "Please enter the message you would like to enter");
                                                            if (newMessage.equals("") || newMessage == null) {
                                                                ServerProcessor.sendMessage(writer, "Messages cannot be empty!", JOptionPane.ERROR_MESSAGE);
                                                            }
                                                        }
                                                        ConversationManager.sendMessage(main.currentUser, main.recipient, newMessage);
                                                        //send message by importing file
                                                    } else if (messageType.equals("2")) {
                                                        String fileMessage = "";
                                                        while (fileMessage.equals("")) {
                                                            String importFileName = ServerProcessor.sendInput(writer, reader, "Please enter the name of the file you would " +
                                                                    "like to import");
                                                            fileMessage = FileImportExport.importFile(importFileName, writer, reader);
                                                            if (!fileMessage.equals("")) {
                                                                ConversationManager.sendMessage(main.currentUser, main.recipient, fileMessage);
                                                            }
                                                        }
                                                    } else {
                                                        ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                } while (!messageType.equals("1") && !messageType.equals("2"));
                                                ServerProcessor.sendMessage(writer, "Message sent successfully", JOptionPane.PLAIN_MESSAGE);
                                                //edit message
                                            } else if (messageMenuChoice.equals("3")) {
                                                if (main.getCurrentConvo().size() == 0) {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to edit.", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    String currentConvoString = "";
                                                    for (Message m : main.getCurrentConvo()) {
                                                        currentConvoString = currentConvoString + m.toString() + "\n";
                                                    }
                                                    ServerProcessor.sendMessage(writer, currentConvoString, JOptionPane.PLAIN_MESSAGE);
                                                    boolean messageEdited = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessor.sendInput(writer, reader, "Please enter the message ID of the message you would " +
                                                            "like to edit");
                                                    try {
                                                        messageID = Integer.valueOf(messageIDString);
                                                    } catch (InputMismatchException e) {
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.getCurrentConvo()) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessor.sendMessage(writer, "Please enter a valid message ID!", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        String editedMessage = ServerProcessor.sendInput(writer, reader, "What would you like to edit the message to?");
                                                        messageEdited = ConversationManager.editMessage(main.currentUser,
                                                                main.recipient, messageID, editedMessage, writer);
                                                    }
                                                    if (messageEdited) {
                                                        ServerProcessor.sendMessage(writer, "Message edited successfully!", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                                //delete messsage
                                            } else if (messageMenuChoice.equals("4")) {
                                                if (main.getCurrentConvo().size() == 0) {
                                                    ServerProcessor.sendMessage(writer, "There is currently no conversation for you to delete.", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    String currentConvoString = "";
                                                    for (Message m : main.getCurrentConvo()) {
                                                        currentConvoString = currentConvoString + m.toString() + "\n";
                                                    }
                                                    ServerProcessor.sendMessage(writer, currentConvoString, JOptionPane.PLAIN_MESSAGE);
                                                    boolean messageDeleted = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessor.sendInput(writer, reader, "Please enter the message ID of the message you would " +
                                                            "like to delete");
                                                    try {
                                                        messageID = Integer.valueOf(messageIDString);
                                                    } catch (InputMismatchException e) {
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.getCurrentConvo()) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessor.sendMessage(writer, "Please enter a valid message ID!", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        messageDeleted = ConversationManager.deleteMessage(main.currentUser,
                                                                main.recipient, messageID);
                                                    }
                                                    if (messageDeleted) {
                                                        ServerProcessor.sendMessage(writer, "Message deleted successfully!", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                                //export message
                                            } else if (messageMenuChoice.equals("5")) {
                                                FileImportExport.exportCSV(main.currentUser.getUsername(), main.recipient.getUsername(),
                                                        main.getCurrentConvo(), writer, reader);
                                                ServerProcessor.sendMessage(writer, "Conversation exported successfully!", JOptionPane.PLAIN_MESSAGE);
                                                //quit to main menu
                                            } else if (messageMenuChoice.equals("6")) {
                                                break;
                                                //quit program
                                            } else if (messageMenuChoice.equals("7")) {
                                                mainMenu = false;
                                                break;
                                            } else {
                                                ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                }
                                //block user
                            } else if (menuChoice.equals("2")) {
                                if (AccountManager.sellers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no sellers registered for you to block.", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String block = ServerProcessor.sendInput(writer, reader, "Please enter the username of the user you would like to block");
                                    User blockUser = AccountManager.getUserFromUsername(block);
                                    if (blockUser != null) {
                                        main.currentUser.blockUser(blockUser);
                                        ServerProcessor.sendMessage(writer, block + " is now blocked", JOptionPane.PLAIN_MESSAGE);
                                    } else {
                                        ServerProcessor.sendMessage(writer, "There does not exist a user with the username " + block, JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                //become invisible to user
                            } else if (menuChoice.equals("3")) {
                                if (AccountManager.sellers.size() == 0) {
                                    ServerProcessor.sendMessage(writer, "There are currently no sellers registered for you to become invisible" +
                                            " to.", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String invisibleTo = ServerProcessor.sendInput(writer, reader, "Please enter the name of the user you would like to become invisible " +
                                            "to");
                                    User invisibleToUser = AccountManager.getUserFromUsername(invisibleTo);
                                    if (invisibleToUser != null) {
                                        main.currentUser.addInvisible(invisibleToUser);
                                        ServerProcessor.sendMessage(writer, "You are now invisible to " + invisibleTo, JOptionPane.PLAIN_MESSAGE);
                                    } else {
                                        ServerProcessor.sendMessage(writer, "There does not exist a user with the username " + invisibleTo, JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                //quit
                            } else if (menuChoice.equals("4") || menuChoice.equals("0")) {
                                break;
                            } else {
                                ServerProcessor.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    }
                }
                //write accounts back to accounts.txt to update blocked and invisibleTo lists
                AccountManager.accounts.writeListToFile();
                AccountManager.stores.writeListToFile();
                if (main.recipient != null)
                    ConversationManager.closeConversation(main.currentUser.getUsername(), main.recipient.getUsername());

                ServerProcessor.sendMessage(writer, "Thank you for using Apartments Messager!", JOptionPane.PLAIN_MESSAGE);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (UserExitException ue) {
                AccountManager.accounts.writeListToFile();
                AccountManager.stores.writeListToFile();
                if (main.recipient != null)
                    ConversationManager.closeConversation(main.currentUser.getUsername(), main.recipient.getUsername());
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Set the recipient to the given value
    // Open the conversation if the new recipient is not null
    // Close the conversation if the current value of the recipient is not null
    // Done so I don't have to determine specifically when this must happen in previous code
    private void setRecipient(User recipientArg) {
        if (this.recipient != null) {
            ConversationManager.closeConversation(this.currentUser.getUsername(), this.recipient.getUsername());
        }

        if (recipientArg != null) {
            ConversationManager.openConversation(this.currentUser.getUsername(), recipientArg.getUsername());
        }

        this.recipient = recipientArg;
    }

    // Getters for currentConvo and recipientConvo
    // Created to ensure that convos are always up-to-date (concurrent)
    private ArrayList<Message> getCurrentConvo() {
        return ConversationManager.getConversation(currentUser.getUsername(), recipient.getUsername());
    }

    private ArrayList<Message> getRecipientConvo() {
        return ConversationManager.getConversation(recipient.getUsername(), currentUser.getUsername());
    }
}
