import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;


public class ApartmentsMessager {

    private ArrayList<Message> currentConvo;
    private ArrayList<Message> recipientConvo;
    private ArrayList<User> accounts;
    private ArrayList<String> buyers;
    private ArrayList<String> sellers;
    private ArrayList<Store> stores;
    private User current;
    private User recipient;

    public ApartmentsMessager() {
        currentConvo = new ArrayList<Message>();
        recipientConvo = new ArrayList<Message>();
        accounts = new ArrayList<User>();
        buyers = new ArrayList<String>();
        sellers = new ArrayList<String>();
        stores = new ArrayList<Store>();
        current = null;
        recipient = null;
    }

    // Server class
    public static void main(String[] args) {
        ServerSocket server = null;
        try {

            server = new ServerSocket(4242);
            //server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());

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
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
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
            try {

                // get the outputstream of client
                writer = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                // get the inputstream of client
                reader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                ApartmentsMessager main = new ApartmentsMessager();
                AccountManager accountManager = new AccountManager();
                FileImportExport fileIO = new FileImportExport();

                main.accounts = accountManager.getAccounts();
                main.buyers = accountManager.getBuyers();
                main.sellers = accountManager.getSellers();
                main.stores = accountManager.getStores();

                Scanner input = new Scanner(System.in);
                boolean loggedIn = false;

                //Register menu
                System.out.println("Welcome to L15 Apartments Messager!");
                while (!loggedIn) {
                    System.out.println("1. Login");
                    System.out.println("2. Register");
                    System.out.println("3. Quit");
                    String login = input.nextLine();
                    //If they choose to login, check that username already exists and password matches to move on
                    if (login.equals("1")) {
                        System.out.println("Enter username: ");
                        String username = input.nextLine();
                        System.out.println("Enter password: ");
                        String password = input.nextLine();
                        main.current = accountManager.logIn(username, password);
                        if (main.current != null) {
                            loggedIn = true;
                        }
                        //Check if a username with name already exists. If not, register new user
                    } else if (login.equals("2")) {
                        System.out.println("Enter email:");
                        String email = input.nextLine();
                        System.out.println("Enter username:");
                        String username = input.nextLine();
                        System.out.println("Enter password:");
                        String password = input.nextLine();
                        String buyerOrSeller;
                        boolean isSeller = false;
                        do {
                            System.out.println("Would you like to register as a buyer or a seller? (B/S)");
                            buyerOrSeller = input.nextLine();
                            if (buyerOrSeller.equalsIgnoreCase("S")) {
                                isSeller = true;
                            } else if (buyerOrSeller.equalsIgnoreCase("B")) {
                                isSeller = false;
                            } else {
                                System.out.println("Please type B for buyer or S for seller!");
                            }
                        } while (!(buyerOrSeller.equalsIgnoreCase("S")) && !buyerOrSeller.equalsIgnoreCase("B"));
                        main.current = accountManager.register(email, username, password, isSeller);
                        if (main.current != null) {
                            loggedIn = true;
                        }

                    } else if (login.equals("3")) {
                        break;
                    } else {
                        System.out.println("Please choose a number from the menu to proceed.");
                    }
                }
                if (loggedIn) {
                    ServerProcessser.sendMessage(writer, "You are now logged in as ",
                            JOptionPane.INFORMATION_MESSAGE);
                    //logged in as seller
                    if (main.current.isSeller()) {
                        //removes buyers who chose to be invisible to currently logged in seller from list of buyers
                        for (int i = 0; i < main.accounts.size(); i++) {
                            User c = main.accounts.get(i);
                            if (c.getinvisibleTo().contains(main.current.getUsername())) {
                                main.buyers.remove(c.getUsername());
                            }
                        }
                        //Main menu
                        //Will keep looping until user chooses to quit
                        boolean mainMenu = true;
                        while (mainMenu) {
                            main.recipient = null;
                            String menuChoice = ServerProcessser.sendOptions(writer, reader, "What would you like to do?",
                                    new String[]{"Search for a buyer to message", "Create a store", "Block a user", "Become invisible to a user"
                                            , "Quit"});
                            //Find user to message
                            if (menuChoice.equals("1")) {
                                if (main.buyers.size() == 0) {
                                    ServerProcessser.sendMessage(writer, "There are currently no registered buyers for you to message.",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String buyerSearch = "";
                                    //loops until user successfully chooses a person to message
                                    while (main.recipient == null && !buyerSearch.equals("3")) {
                                        buyerSearch = ServerProcessser.sendOptions(writer, reader, "Would you like to:",
                                                new String[]{"See list of buyers", "Search for a buyer", "Quit"});
                                        if (buyerSearch.equals("1")) {
                                            String buyerList = "";
                                            for (int i = 0; i < main.buyers.size() - 1; i++) {
                                                buyerList += main.buyers.get(i);
                                                buyerList += "\n";
                                            }
                                            buyerList += main.buyers.get(main.buyers.size() - 1);
                                            do {
                                                String buyerChoice = ServerProcessser.sendInput(writer, reader,
                                                        buyerList + "\nWhich buyer would you like to message?");
                                                if (main.buyers.contains(buyerChoice)) {
                                                    main.recipient = accountManager.getUserFromUsername(buyerChoice);
                                                }
                                                if (main.recipient == null) {
                                                    ServerProcessser.sendMessage(writer, "Please enter a user from the list", JOptionPane.WARNING_MESSAGE);
                                                } else if (main.recipient.getBlocked().contains(main.current.getUsername())) {
                                                    String errorMessage = "You have been blocked by " +
                                                            main.recipient.getUsername() + " and may not message them";
                                                    ServerProcessser.sendMessage(writer, errorMessage, JOptionPane.WARNING_MESSAGE);
                                                    main.recipient = null;
                                                    break;
                                                }
                                            } while (main.recipient == null);
                                        } else if (buyerSearch.equals("2")) {
                                            String buyerChoice = ServerProcessser.sendInput(writer, reader,
                                                    "Please enter the username of the buyer " +
                                                            "you would like to message");
                                            if (main.buyers.contains(buyerChoice)) {
                                                main.recipient = accountManager.getUserFromUsername(buyerChoice);
                                            }
                                            if (main.recipient == null) {
                                                String nonexistentUser = "There does not exist a user with the username " +
                                                        buyerChoice;
                                                ServerProcessser.sendMessage(writer, nonexistentUser, JOptionPane.WARNING_MESSAGE);
                                            } else if (main.recipient.getBlocked().contains(main.current.getUsername())) {
                                                String errorMessage = "You have been blocked by " +
                                                        main.recipient.getUsername() + " and may not message them";
                                                ServerProcessser.sendMessage(writer, errorMessage, JOptionPane.WARNING_MESSAGE);
                                                main.recipient = null;
                                            }

                                        } else if (buyerSearch.equals("3")) {
                                            break;
                                        } else {
                                            ServerProcessser.sendMessage(writer, "Please choose a number from the menu to proceed.",
                                                    JOptionPane.WARNING_MESSAGE);
                                        }

                                    }
                                    //Confirms the recipient and reads in the conversations
                                    if (!buyerSearch.equals("3")) {
                                        String recipientMessage = "You are now messaging " + main.recipient.getUsername();
                                        ServerProcessser.sendMessage(writer, recipientMessage, JOptionPane.INFORMATION_MESSAGE);
                                        String messageName =
                                                main.current.getUsername() + "-" + main.recipient.getUsername() + ".txt";
                                        String recipientMessageName =
                                                main.recipient.getUsername() + "-" + main.current.getUsername() + ".txt";
                                        main.currentConvo = main.readConversation(main, messageName);
                                        main.recipientConvo = main.readConversation(main, recipientMessageName);

                                        //Message Menu
                                        while (true) {
                                            String messageMenuChoice = ServerProcessser.sendOptions(writer, reader,
                                                    "Would you like to:", new String[]{"View message history",
                                                            "Send a new message", "Edit a message",
                                                            "Delete a message", "Export message history",
                                                            "Go back to the Main Menu", "Quit"});
                                            //Prints out message history of user
                                            if (messageMenuChoice.equals("1")) {
                                                String messageHistory = "";
                                                for (int i = 0; i < main.currentConvo.size() - 1; i++) {
                                                    messageHistory += main.currentConvo.get(i);
                                                    messageHistory += "\n";
                                                }
                                                messageHistory += main.currentConvo.get(main.currentConvo.size() - 1);
                                                ServerProcessser.sendMessage(writer, messageHistory, JOptionPane.INFORMATION_MESSAGE);
                                                //send message
                                            } else if (messageMenuChoice.equals("2")) {
                                                String messageType = "";
                                                do {
                                                    messageType = ServerProcessser.sendOptions(writer, reader,
                                                            "Would you like to:",
                                                            new String[]{"Send a message", "Send a file"});
                                                    if (messageType.equals("1")) {
                                                        String newMessage = ServerProcessser.sendInput(writer, reader,
                                                                "Please enter the message you would like to enter");
                                                        main.current.sendMessage(newMessage, main.currentConvo,
                                                                main.recipientConvo);
                                                    } else if (messageType.equals("2")) {
                                                        String fileMessage = "";
                                                        while (fileMessage.equals("")) {
                                                            String importFileName = ServerProcessser.sendInput(writer, reader,
                                                                    "Please enter the name of the file you would " +
                                                                            "like to import");
                                                            fileMessage = fileIO.importFile(importFileName);
                                                            if (!fileMessage.equals("")) {
                                                                main.current.sendMessage(fileMessage, main.currentConvo,
                                                                        main.recipientConvo);
                                                            }
                                                        }
                                                    } else {
                                                        ServerProcessser.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                } while (!messageType.equals("1") && !messageType.equals("2"));
                                                ServerProcessser.sendMessage(writer, "Message sent successfully",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                                //edit message
                                            } else if (messageMenuChoice.equals("3")) {
                                                if (main.currentConvo.size() == 0) {
                                                    ServerProcessser.sendMessage(writer, "There is currently no conversation for you to edit.", JOptionPane.WARNING_MESSAGE);
                                                } else {
                                                    String messageHistory = "";
                                                    for (int i = 0; i < main.currentConvo.size() - 1; i++) {
                                                        messageHistory += main.currentConvo.get(i);
                                                        messageHistory += "\n";
                                                    }
                                                    messageHistory += main.currentConvo.get(main.currentConvo.size() - 1);
                                                    messageHistory += "\n";
                                                    boolean messageEdited = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessser.sendInput(writer, reader,
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
                                                    for (Message message : main.currentConvo) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessser.sendMessage(writer, "Please enter a valid message ID!",
                                                                JOptionPane.WARNING_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        String editedMessage = ServerProcessser.sendInput(writer, reader,
                                                                "What would you like to edit the message to?");
                                                        messageEdited = main.current.editMessage(messageID, editedMessage,
                                                                main.currentConvo, main.recipientConvo, main.current);
                                                    }
                                                    if (messageEdited) {
                                                        ServerProcessser.sendMessage(writer, "Message edited successfully!",
                                                                JOptionPane.INFORMATION_MESSAGE);
                                                    }

                                                }
                                                //delete message
                                            } else if (messageMenuChoice.equals("4")) {
                                                if (main.currentConvo.size() == 0) {
                                                    ServerProcessser.sendMessage(writer, "There is currently no conversation for you to delete.", JOptionPane.INFORMATION_MESSAGE);
                                                } else {
                                                    String messageHistory = "";
                                                    for (int i = 0; i < main.currentConvo.size() - 1; i++) {
                                                        messageHistory += main.currentConvo.get(i);
                                                        messageHistory += "\n";
                                                    }
                                                    messageHistory += main.currentConvo.get(main.currentConvo.size() - 1);
                                                    messageHistory += "\n";
                                                    boolean messageDeleted = false;
                                                    int messageID = -1;
                                                    String messageIDString = ServerProcessser.sendInput(writer, reader,
                                                            messageHistory + "\nPlease enter the message ID of the message you would " +
                                                            "like to delete");
                                                    try {
                                                        messageID = Integer.parseInt(messageIDString);
                                                    } catch (InputMismatchException ignored) {
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.currentConvo) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        ServerProcessser.sendMessage(writer, "Please enter a valid message ID!", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                    if (messageID != -1) {
                                                        messageDeleted = main.current.deleteMessage(messageID,
                                                                main.currentConvo);
                                                    }
                                                    if (messageDeleted) {
                                                        ServerProcessser.sendMessage(writer, "Message deleted successfully!", JOptionPane.INFORMATION_MESSAGE);
                                                    }
                                                }
                                                //export conversation history
                                            } else if (messageMenuChoice.equals("5")) {
                                                fileIO.exportCSV(main.current.getUsername(), main.recipient.getUsername(),
                                                        main.currentConvo);
                                                ServerProcessser.sendMessage(writer, "Conversation exported successfully!", JOptionPane.INFORMATION_MESSAGE);
                                            } else if (messageMenuChoice.equals("6")) {
                                                break;
                                            } else if (messageMenuChoice.equals("7")) {
                                                mainMenu = false;
                                                break;
                                            } else {
                                                ServerProcessser.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.WARNING_MESSAGE);
                                            }
                                        }

                                        //updates conversation txt files
                                        main.writeMessages(main);
                                    }
                                }
                                //create store
                            } else if (menuChoice.equals("2")) {
                                String store = ServerProcessser.sendInput(writer, reader,
                                        "Please enter the name of the store you would like to create");
                                accountManager.createStore(main.current.getUsername(), store);
                                ServerProcessser.sendMessage(writer, "Successfully created store!",
                                        JOptionPane.INFORMATION_MESSAGE);
                                //block user
                            } else if (menuChoice.equals("3")) {
                                if (main.buyers.size() == 0) {
                                    ServerProcessser.sendMessage(writer, "There are currently no registered buyers for you to block.",
                                            JOptionPane.WARNING_MESSAGE);
                                } else {
                                    String block = ServerProcessser.sendInput(writer, reader,
                                            "Please enter the username of the user you would like to block");
                                    User blockUser = accountManager.getUserFromUsername(block);
                                    if (blockUser != null) {
                                        main.current.blockUser(blockUser);
                                        String blockMessage = block + " is now blocked";
                                        ServerProcessser.sendMessage(writer, blockMessage, JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        String blockUserError = "There does not exist a user with the username " + block;
                                        ServerProcessser.sendMessage(writer, blockUserError, JOptionPane.WARNING_MESSAGE);
                                    }
                                }
                                //become invisible to a user
                            } else if (menuChoice.equals("4")) {
                                if (main.buyers.size() == 0) {
                                    ServerProcessser.sendMessage(writer, "There are currently no registered buyers " +
                                            "for you to become invisible to.", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    String invisibleTo = ServerProcessser.sendInput(writer, reader,
                                            "Please enter the name of the user you would " +
                                                    "like to become invisible to");

                                    User invisibleToUser = accountManager.getUserFromUsername(invisibleTo);
                                    if (invisibleToUser != null) {
                                        main.current.addInvisible(invisibleToUser);
                                        String invisibleToMessage = "You are now invisible to " + invisibleTo;
                                        ServerProcessser.sendMessage(writer, invisibleToMessage, JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        String invisibleToErrorMessage = "There does not exist a user with the username " + invisibleTo;
                                        ServerProcessser.sendMessage(writer, invisibleToErrorMessage, JOptionPane.WARNING_MESSAGE);
                                    }
                                }
                                //quit
                            } else if (menuChoice.equals("5")) {
                                break;
                            } else {
                                ServerProcessser.sendMessage(writer, "Please choose a number from the menu to proceed.", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                        //if user is a buyer
                    } else if (!main.current.isSeller()) {
                        //removes stores whose sellers chose to become invisible to currently logged in buyer from list of
                        // stores
                        for (int i = 0; i < main.accounts.size(); i++) {
                            User c = main.accounts.get(i);
                            if (c.getinvisibleTo().contains(main.current.getUsername())) {
                                for (int j = 0; j < main.stores.size(); j++) {
                                    Store s = main.stores.get(j);
                                    if (s.getSeller().equals(c.getUsername())) {
                                        main.stores.remove(s);
                                    }
                                }
                            }
                        }
                        //main menu
                        boolean mainMenu = true;
                        while (mainMenu) {
                            main.recipient = null;
                            System.out.println("What would you like to do?");
                            System.out.println("1. Message a seller");
                            System.out.println("2. Block a user");
                            System.out.println("3. Become invisible to a user");
                            System.out.println("4. Quit");
                            String menuChoice = input.nextLine();
                            //FInd a user to message
                            if (menuChoice.equals("1")) {
                                if (main.sellers.size() == 0) {
                                    System.out.println("There are currently no sellers registered for you to message.");
                                } else {
                                    String buyerSearch = "";
                                    while (main.recipient == null && !buyerSearch.equals("3")) {
                                        System.out.println("Would you like to:");
                                        System.out.println("1. See list of stores");
                                        System.out.println("2. Search for a seller");
                                        System.out.println("3. Quit");
                                        buyerSearch = input.nextLine();
                                        //search by store
                                        if (buyerSearch.equals("1")) {
                                            if (main.stores.size() == 0) {
                                                System.out.println("There are currently no stores available to message.");
                                            } else {
                                                for (int i = 0; i < main.stores.size(); i++) {
                                                    System.out.println(main.stores.get(i));
                                                }
                                                do {
                                                    System.out.println("Which store would you like to message?");
                                                    String storeChoice = input.nextLine();
                                                    //searches through stores objects until one of the store names matches
                                                    // the search. THen, gets the seller who owns that store
                                                    for (int i = 0; i < main.stores.size(); i++) {
                                                        if (main.stores.get(i).getName().equalsIgnoreCase(storeChoice)) {
                                                            main.recipient =
                                                                    accountManager.getUserFromUsername(main.stores.get(i)
                                                                            .getSeller());
                                                        }
                                                    }
                                                    if (main.recipient == null) {
                                                        System.out.println("Please enter a store from the list.");
                                                    } else if (main.recipient.getBlocked()
                                                            .contains(main.current.getUsername())) {
                                                        System.out.println("You have been blocked by the owner of" +
                                                                storeChoice + " and may not message them");
                                                        main.recipient = null;
                                                        break;
                                                    }
                                                } while (main.recipient == null);
                                            }
                                            //search by username
                                        } else if (buyerSearch.equals("2")) {
                                            System.out.println("Please enter the username of the seller you would like to " +
                                                    "message");
                                            String sellerChoice = input.nextLine();
                                            if (main.sellers.contains(sellerChoice)) {
                                                main.recipient = accountManager.getUserFromUsername(sellerChoice);
                                            }
                                            if (main.recipient == null) {
                                                System.out.println("There does not exist a user with the username "
                                                        + sellerChoice);
                                            } else if (main.recipient.getBlocked().contains(main.current.getUsername())) {
                                                System.out.println("You have been blocked by " +
                                                        main.recipient.getUsername() + " and may not message them");
                                                main.recipient = null;
                                            }
                                            //quit
                                        } else if (buyerSearch.equals("3")) {
                                            break;
                                        } else {
                                            System.out.println("Please choose a number from the menu to proceed.");
                                        }
                                    }
                                    //Confirm recipient and read in message txt files
                                    if (!buyerSearch.equals("3")) {
                                        System.out.println("You are now messaging " + main.recipient.getUsername());
                                        String messageName =
                                                main.current.getUsername() + "-" + main.recipient.getUsername() + ".txt";
                                        String recipientMessageName =
                                                main.recipient.getUsername() + "-" + main.current.getUsername() + ".txt";
                                        main.currentConvo = main.readConversation(main, messageName);
                                        main.recipientConvo = main.readConversation(main, recipientMessageName);

                                        //Message Menu
                                        while (true) {
                                            System.out.println("Would you like to:");
                                            System.out.println("1. View message history");
                                            System.out.println("2. Send a new message");
                                            System.out.println("3. Edit a message");
                                            System.out.println("4. Delete a message");
                                            System.out.println("5. Export message history");
                                            System.out.println("6. Go back to the Main Menu");
                                            System.out.println("7. Quit");
                                            String messageMenuChoice = input.nextLine();
                                            //print out message history
                                            if (messageMenuChoice.equals("1")) {
                                                for (Message m : main.currentConvo) {
                                                    System.out.println(m.toString());
                                                }
                                                //send message
                                            } else if (messageMenuChoice.equals("2")) {
                                                String messageType = "";
                                                do {
                                                    System.out.println("Would you like to:");
                                                    System.out.println("1. Send a message");
                                                    System.out.println("2. Send a file");
                                                    messageType = input.nextLine();
                                                    //send new message by typing input
                                                    if (messageType.equals("1")) {
                                                        System.out.println("Please enter the message you would like to enter");
                                                        String newMessage = input.nextLine();
                                                        main.current.sendMessage(newMessage, main.currentConvo,
                                                                main.recipientConvo);
                                                        //send message by importing file
                                                    } else if (messageType.equals("2")) {
                                                        String fileMessage = "";
                                                        while (fileMessage.equals("")) {
                                                            System.out.println("Please enter the name of the file you would " +
                                                                    "like to import");
                                                            String importFileName = input.nextLine();
                                                            fileMessage = fileIO.importFile(importFileName);
                                                            if (!fileMessage.equals("")) {
                                                                main.current.sendMessage(fileMessage, main.currentConvo,
                                                                        main.recipientConvo);
                                                            }
                                                        }
                                                    } else {
                                                        System.out.println("Please choose a number from the menu to proceed.");
                                                    }
                                                } while (!messageType.equals("1") && !messageType.equals("2"));
                                                System.out.println("Message sent successfully");
                                                //edit message
                                            } else if (messageMenuChoice.equals("3")) {
                                                if (main.currentConvo.size() == 0) {
                                                    System.out.println("There is currently no conversation for you to edit.");
                                                } else {
                                                    for (Message m : main.currentConvo) {
                                                        System.out.println(m.toString());
                                                    }
                                                    boolean messageEdited = false;
                                                    int messageID = -1;
                                                    System.out.println("Please enter the message ID of the message you would " +
                                                            "like to edit");
                                                    try {
                                                        messageID = input.nextInt();
                                                        input.nextLine();
                                                    } catch (InputMismatchException e) {
                                                        input.nextLine();
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.currentConvo) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        System.out.println("Please enter a valid message ID!");
                                                    }
                                                    if (messageID != -1) {
                                                        System.out.println("What would you like to edit the message to?");
                                                        String editedMessage = input.nextLine();
                                                        messageEdited = main.current.editMessage(messageID, editedMessage,
                                                                main.currentConvo, main.recipientConvo, main.current);
                                                    }
                                                    if (messageEdited) {
                                                        System.out.println("Message edited successfully!");
                                                    }
                                                }
                                                //delete messsage
                                            } else if (messageMenuChoice.equals("4")) {
                                                if (main.currentConvo.size() == 0) {
                                                    System.out.println("There is currently no conversation for you to delete.");
                                                } else {
                                                    for (Message m : main.currentConvo) {
                                                        System.out.println(m.toString());
                                                    }
                                                    boolean messageDeleted = false;
                                                    int messageID = -1;
                                                    System.out.println("Please enter the message ID of the message you would " +
                                                            "like to delete");
                                                    try {
                                                        messageID = input.nextInt();
                                                        input.nextLine();
                                                    } catch (InputMismatchException e) {
                                                        input.nextLine();
                                                    }
                                                    boolean foundID = false;
                                                    for (Message message : main.currentConvo) {
                                                        if (message.getMessageID() == messageID) {
                                                            foundID = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundID) {
                                                        messageID = -1;
                                                        System.out.println("Please enter a valid message ID!");
                                                    }
                                                    if (messageID != -1) {
                                                        messageDeleted = main.current.deleteMessage(messageID,
                                                                main.currentConvo);
                                                    }
                                                    if (messageDeleted) {
                                                        System.out.println("Message deleted successfully!");
                                                    }
                                                }
                                                //export message
                                            } else if (messageMenuChoice.equals("5")) {
                                                fileIO.exportCSV(main.current.getUsername(), main.recipient.getUsername(),
                                                        main.currentConvo);
                                                System.out.println("Conversation exported successfully!");
                                                //quit to main menu
                                            } else if (messageMenuChoice.equals("6")) {
                                                break;
                                                //quit program
                                            } else if (messageMenuChoice.equals("7")) {
                                                mainMenu = false;
                                                break;
                                            } else {
                                                System.out.println("Please choose a number from the menu to proceed.");
                                            }
                                        }
                                        //write new edits/messages to txt files
                                        main.writeMessages(main);
                                    }
                                }
                                //block user
                            } else if (menuChoice.equals("2")) {
                                if (main.sellers.size() == 0) {
                                    System.out.println("There are currently no sellers registered for you to block.");
                                } else {
                                    System.out.println("Please enter the username of the user you would like to block");
                                    String block = input.nextLine();
                                    User blockUser = accountManager.getUserFromUsername(block);
                                    if (blockUser != null) {
                                        main.current.blockUser(blockUser);
                                        System.out.println(block + " is now blocked");
                                    } else {
                                        System.out.println("There does not exist a user with the username " + block);
                                    }
                                }
                                //become invisible to user
                            } else if (menuChoice.equals("3")) {
                                if (main.sellers.size() == 0) {
                                    System.out.println("There are currently no sellers registered for you to become invisible" +
                                            " to.");
                                } else {
                                    System.out.println("Please enter the name of the user you would like to become invisible " +
                                            "to");
                                    String invisibleTo = input.nextLine();
                                    User invisibleToUser = accountManager.getUserFromUsername(invisibleTo);
                                    if (invisibleToUser != null) {
                                        main.current.addInvisible(invisibleToUser);
                                        System.out.println("You are now invisible to " + invisibleTo);
                                    } else {
                                        System.out.println("There does not exist a user with the username " + invisibleTo);
                                    }
                                }
                                //quit
                            } else if (menuChoice.equals("4")) {
                                break;
                            } else {
                                System.out.println("Please choose a number from the menu to proceed.");
                            }
                        }
                    }
                }
                //write accounts back to accounts.txt to update blocked and invisibleTo lists
                main.writeAccounts(main);
                System.out.println("Thank you for using Apartments Messager!");

            } catch (IOException e) {
                e.printStackTrace();
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

    //Reads in a conversation txt file and returns an ArrayList of messages
    public ArrayList<Message> readConversation(ApartmentsMessager main, String filename) {
        ArrayList<Message> read = new ArrayList<Message>();
        File file = new File(filename);
        ArrayList<String> convo = new ArrayList<String>();

        try {
            if (!file.createNewFile()) {
                FileReader fr = new FileReader(file);
                BufferedReader bfr = new BufferedReader(fr);
                String line = bfr.readLine();
                while (line != null) {
                    convo.add(line);
                    line = bfr.readLine();
                }
                bfr.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (convo.size() > 0) {
            for (int i = 0; i < convo.size(); i += 4) {
                String timestamp = convo.get(i);
                String username = convo.get(i + 1);
                int messageID = Integer.parseInt(convo.get(i + 2));
                String message = convo.get(i + 3);
                read.add(new Message(username, message, messageID, timestamp));
            }
        }
        return read;
    }

    //Write messages into the conversation txt files
    public void writeMessages(ApartmentsMessager messager) {
        String userName = messager.current.getUsername();
        String recipientName = messager.recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        File file = new File(filename);

        String conversation = "";
        if (messager.currentConvo.size() > 0) {
            conversation = messager.currentConvo.get(0).toString();
            for (int i = 1; i < messager.currentConvo.size(); i++) {
                conversation += "\n" + messager.currentConvo.get(i).toString();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
            pw.print(conversation);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filename = recipientName + "-" + userName + ".txt";
        file = new File(filename);

        conversation = "";
        if (messager.recipientConvo.size() > 0) {
            conversation = messager.recipientConvo.get(0).toString();
            for (int i = 1; i < messager.recipientConvo.size(); i++) {
                conversation += "\n" + messager.recipientConvo.get(i).toString();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.print(conversation);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Write accounts to accounts.txt
    public void writeAccounts(ApartmentsMessager messager) {
        File file = new File("accounts.txt");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
            for (User u : messager.accounts) {
                pw.println(u.toString());
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




