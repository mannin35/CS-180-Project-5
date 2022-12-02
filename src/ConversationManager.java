import java.util.*;

public class ConversationManager {
    static HashMap<String, ArrayList<Message>> conversations;
    static HashMap<String, Object> locks;
    static Object addLock;
    private Object lock;


    public ConversationManager() {
        conversations = new HashMap<>();
        locks = new HashMap<>();
    }

    public ArrayList<Message> getConversation(String filename) {
        lock = locks.get(filename);
        synchronized(lock) {
            return conversations.get(filename);
        }
    }

    public void addConversation(String user, String recipient, ArrayList<Message> conversation) {
        String filename = user + "-" + recipient + ".txt";
        String reversefile = recipient + "-" + user + ".txt";
        synchronized(addLock) {
            conversations.put(filename, conversation);
            if (locks.get(reversefile) != null) {
                locks.put(filename, locks.get(reversefile));
            } else {
                locks.put(filename, new Object());
            }
            
            conversations.put(filename, conversation);
        }
    }

    public void setConversation(String filename, ArrayList<Message> newConversation) {
        synchronized(locks.get(filename)) {
            conversations.put(filename, newConversation);
        }
    }

    public void sendMessage(User user, User recipient, String message) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        String reversefile = recipient + "-" + user + ".txt";
        synchronized(locks.get(filename)) {
            user.sendMessage(message, conversations.get(filename), conversations.get(reversefile));
        }
    }

    public boolean deleteMessage(User user, User recipient, int messageID) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        synchronized(locks.get(filename)) {
            return user.deleteMessage(messageID, conversations.get(filename));
        }
    }

    public boolean editMessage(User user, User recipient, int messageID, String newMessage) {
        String userName = user.getUsername();
        String recipientName = recipient.getUsername();
        String filename = userName + "-" + recipientName + ".txt";
        String reversefile = recipient + "-" + user + ".txt";
        synchronized(locks.get(filename)) {
            return user.editMessage(messageID, newMessage, conversations.get(filename), conversations.get(reversefile), user);
        }
    }

}