import java.io.*;
import java.util.*;

/**
 * Project 5 - Conversation Manager
 *
 * This is the class that handles concurrency for methods and resources regarding conversations
 *
 * @author Nick Andry, Rei Manning, Lab Sec L15
 *
 * @version December 12, 2022
 *
 */

public class ConversationManager {
    private static HashMap<String, ArrayList<Message>> conversations;
    private static HashMap<String, Object> locks;
    private static final Object modifyLock;


    static {
        conversations = new HashMap<>();
        locks = new HashMap<>();
        modifyLock = new Object();
    }

    public static ArrayList<Message> getConversation(String user, String recipient) {
        String filename = user + "-" + recipient + ".txt";
        Object lock = locks.get(filename);
        synchronized(lock) {
            return conversations.get(filename);
        }
    }

    public static void openConversation(String user, String recipient) {
        String filename = user + "-" + recipient + ".txt";
        String reverseFile = recipient + "-" + user + ".txt";

        // Make sure the two files exist
        try {
            File userFile = new File(filename);
            File recipientFile = new File(reverseFile);
            userFile.createNewFile();
            recipientFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized(modifyLock) {
            // Return if conversation is already opened. Done as a precaution
            if(locks.get(filename) != null)
                return;

            ArrayList<Message> conversation;
            if (locks.get(reverseFile) != null) {
                locks.put(filename, locks.get(reverseFile));
                conversation = conversations.get(filename);
                setConversation(filename, conversation);
            } else {
                locks.put(filename, new Object());
                conversation = readConversation(filename);
                setConversation(filename, conversation);

                ArrayList<Message> otherConversation = readConversation(reverseFile);
                conversations.put(reverseFile, otherConversation);
            }
        }
    }

    public static void closeConversation(String user, String recipient) {
        
        String filename = user + "-" + recipient + ".txt";
        String reverseFile = recipient + "-" + user + ".txt";
        synchronized (modifyLock) {
            // Return if conversation is already closed. Done as a precaution
            if (locks.get(filename) == null)
                return;

            if (locks.get(reverseFile) == null) {
                writeConversation(filename);
                writeConversation(reverseFile);
                conversations.remove(filename);
                conversations.remove(reverseFile);
                locks.remove(filename);
            } else {
                writeConversation(filename);
                locks.remove(filename);
            }
        }
    }

    public static void setConversation(String filename, ArrayList<Message> newConversation) {
        synchronized(locks.get(filename)) {
            conversations.put(filename, newConversation);
        }
    }

    public static void sendMessage(User user, User recipient, String message) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        String reverseFile = recipientName + "-" + userName + ".txt";
        synchronized(locks.get(filename)) {
            user.sendMessage(message, conversations.get(filename), conversations.get(reverseFile));
        }
    }

    public static boolean deleteMessage(User user, User recipient, int messageID) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        synchronized(locks.get(filename)) {
            return user.deleteMessage(messageID, conversations.get(filename));
        }
    }

    public static boolean editMessage(User user, User recipient, int messageID, String newMessage, PrintWriter writer) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        String reverseFile = recipientName + "-" + userName + ".txt";
        synchronized(locks.get(filename)) {
            return user.editMessage(messageID, newMessage, conversations.get(filename), conversations.get(reverseFile), user, writer);
        }
    }

    public static ArrayList<Message> readConversation(String filename) {

        ArrayList<Message> read = new ArrayList<>();
        File file = new File(filename);
        ArrayList<String> convo = new ArrayList<>();

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
    public static void writeConversation(String filename) {
        File file = new File(filename);

        ArrayList<Message> userConvo = conversations.get(filename);

        String conversation = "";
        if (userConvo.size() > 0) {
            conversation = userConvo.get(0).toString();
            for (int i = 1; i < userConvo.size(); i++) {
                conversation += "\n" + userConvo.get(i).toString();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
            pw.print(conversation);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}