// These imports helped us get the currentTime for our messages
import java.sql.Timestamp;
import java.time.Instant;
/**
 * Project 5 - Message
 *
 * Encapsulates all the information a message has so that it
 * can be manipulated during runtime and written back to files.
 *
 * @author Arsh Batth, Lab Sec L15
 *
 * @version December 12, 2022
 */

public class Message {
	// These are the fields for our Message Class. They are the username [user whos
	// sends message]. The exact timestamp for when the message is sent. A messageID
	// that allows us to differentiate between messages [useful for delete and edit]
	// and the actual message.
	private String username;
	private String timeStamp;
	private int messageID;
	private String message;

	// This is the Message Constructor
	public Message(String username, String message, int messageID) {
		this.username = username;
		this.message = message;
		this.messageID = messageID;
		this.timeStamp = String.valueOf(Timestamp.from(Instant.now()));
	}

	public Message(String username, String message, int messageID, String timeStamp) {
		this.username = username;
		this.message = message;
		this.messageID = messageID;
		this.timeStamp = timeStamp;
	}

	// Get username
	public String getUsername() {
		return username;
	}

	// Get timeStamp
	public String getTimeStamp() {
		return timeStamp;
	}

	// Get messageID
	public int getMessageID() {
		return messageID;
	}

	// Get message
	public String getMessage() {
		return message;
	}

	// Set message [useful for editing in conversations]
	public void setMessage(String newMessage) {
		this.message = newMessage;
	}

	// toString for when we need to write the message objects into a file
	public String toString() {
		return (timeStamp + "\n" + username + "\n" + messageID + "\n" + message);
	}
}
