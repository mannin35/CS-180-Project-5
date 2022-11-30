import java.util.*;

public class ConversationManager {
    private HashMap<String, ArrayList<Message>> conversations;
    private HashMap<String, Object> locks;
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

    public void addConversation(String filename, ArrayList<Message> conversation) {
        String reversefile = filename.substring(filename.indexOf("-") + 1, filename.indexOf(".")) + 
            "-" + filename.substring(0, filename.indexOf("-")) + ".txt";
        conversations.put(filename, conversation);
        if (locks.get(reversefile) != null) {
            locks.put(filename, locks.get(reversefile));
        } else {
            locks.put(filename, new Object());
        }
        
        conversations.put(filename, conversation);
    }

}