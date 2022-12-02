import java.util.*;

public class ConversationManager {
    static HashMap<String, ArrayList<Message>> conversations;
    static HashMap<String, Object> locks;
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
        conversations.put(filename, conversation);
        if (locks.get(reversefile) != null) {
            locks.put(filename, locks.get(reversefile));
        } else {
            locks.put(filename, new Object());
        }
        
        conversations.put(filename, conversation);
    }

    public void setConversation(String filename, ArrayList<Message> newConversation) {
        synchronized(locks.get(filename)) {
            conversations.put(filename, newConversation);
        }
    }

}