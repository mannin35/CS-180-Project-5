# Apartments Messager

## Compilation

If you are not using an IDE, clone the repository and run `javac *.java` to compile all the java files. Then,
run `java ApartmentsMessager` to run the server and `java Client` to run a client instance.

If you are using an IDE like IntelliJ, clone the repository within it and run the ApartmentsMessager and Client files.

## Submission

Arsh Batth - Submitted report and presentation on Brightspace

Nick Andry - Submitted Vocareum workspace

## Program Structure

### AccountManager

The AccountManager class contains all the information about user accounts that may be needed during the runtime of the
program.
It is also responsible for loading in this information from the respective files.

#### Methods

`AccountManager` - constructor that takes in no arguments and calls loadAccounts and loadStores methods

`register` - takes in all the information a user would have and
creates a User object that is loaded into the accounts arraylist and
the accounts.txt file. Returns null if there is an issue.

`logIn` - takes in the username and password of an existing user and checks if that user exists
and if the password matches. Returns the User object of that user if both are true. Returns null if
there is an issue.

`findUser` - takes in a username and returns a User object if that username exists. Returns null if not.

`appendToFile` - a helper method that takes in a filename and a string. Appends the string to the end of the file.

`readFile` - a helper method that takes in a filename and returns an arraylist with all the lines in the file.

`loadStores` - parses stores.txt and creates Store objects from the information found. Loads the Store object
into the stores arraylist.

`loadAccounts` - parses accounts.txt and creates User objects from the information found. Loads this information
into the accounts, buyers, and sellers arraylists.

`createStore` - takes in a seller's username and the name of a store to be created. Creates a Store object
from this information and adds it to stores.txt and the stores arraylist.

`getUserFromUsername` - takes in a username and returns the corresponding User object. Returns null if
user doesn't exist.

#### Interactions

There are three arraylists that are publicly accessible through their get methods. These are used by
`ApartmentsMessager`.

* `accounts` - contains User objects that hold information about each user.
* `buyers` - contains the usernames of all buyers.
* `sellers` - contains the usernames of all sellers.
* `stores` - contains Store objects that hold information about each store.

All the public methods within the class, such as `register` and `logIn` are used within the main
program flow to interact with account information and retrieve User objects.

#### Testing

Applicable Test Cases:

- Test 1: User Register
- Test 2: User Login
- Test 16: User does not exist for Login
- Test 17: User registers with comma
- Test 18: User registers with existing username
- Test 19: User logs in with incorrect password
- Test 20: User does not enter buyer or seller when Registering


### ApartmentsMessager

#### Methods

`ApartmentsMessager` - constructor that takes no arguments and initializes fields to be used in program flow.

`main` - creates the server socket, then connects all client sockets and creates a new thread for each socket. This 
is where the inner class `ClientHandler` is created.

`readConversation` - takes in an ApartmentsMessager object and a filename. Reads the file and updates the
conversation arraylists of Messages.

`writeMessages` - writes the messages in the message arraylists back to the conversation history files.

#### ClientHandler (Class)

`ClientHandler` - constructor that takes in a socket and initializes the field clientSocket.

`run` - creates a BufferedReader and PrintWriter connected to the client, then runs the main program flow. This is 
where interactions with other classes occur. The class has two User object and two arraylists of Message objects as 
fields to keep track of the current user and their conversations. The class contains of object of itself to use these
fields.

#### Interactions

The run method within `ApartmentsMessager` interacts with all the other classes in the projects (i.e
Store, User, AccountManager, Message, FileImportExport, Client, ConversationManager, ServerProcessor, 
and ResourceManager.)

#### Testing

Every test will implicitly test `ApartmentsMessager`, since this class is where the server is located.

Applicable Test Cases:

- Test 1: User Register
- Test 2: User Login
- Test 3: Buyer messages Seller through Stores
- Test 4: Buyer messeges Seller through Search
- Test 5: Seller messages Buyer through List
- Test 6: Seller messages Buyer through Search
- Test 7: Buyer blocks Seller
- Test 8: Buyer becomes invisible to Seller
- Test 9: Seller blocks Buyer
- Test 10: Seller becomes invisible to Buyer
- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer
- Test 15: Seller creates a Store
- Test 16: User does not exist for Login
- Test 17: User registers with comma
- Test 18: User registers with existing username
- Test 19: User logs in with incorrect password
- Test 20: User does not enter buyer or seller when Registering


### Client

#### Methods

`main` - creates a Socket, PrintWriter, and BufferedReader, all of which connect with the server, `ApartmentsMessager`.
Then, it runs an infinite loop that takes input from the server. Each time it recieves an input, it calls the 
processCommand method.

`createErrorMessage` - takes in a String message and displays it using GUI. This method is called whenever an error
ocurrs in `Client`.

`processCommand` - takes in a String commandHeader, BufferedReader reader, and PrintWriter writer. Inside this method
is a swwitch statement, which has three different cases based on the commandHeader: "message", "options", and "input". 
These cases will call the handleMessage, handleOptions, and handleInput methods repectively and pass in the reader and 
writer to each method.

`getStringArray` - takes in a BufferedReader reader. Reads in the lines sent by the server and returns a String Array 
comprised of the lines read from the server.

`handleInput` - takes in a BufferedReader reader and PrintWriter writer. First, this method calls the getStringArray 
method and stores the message read from the server in a String Array. If the message is null, this method returns null. 
Otherwise, it will call the method showInputDialog from the JOptionPane class and assign a string result to its return 
value. The result String is then printed back to the server and the method returns true.

`handleOptions` - takes in a BufferedReader reader and PrintWriter writer. First, this method calls the getStringArray
method and stores the message read from the server in a String Array. If the message is null, this method returns null. 
Otherwise, this method will read in another line from the server that contains the options that will be displayed, 
and split it into a String Array using "," as the regex. Then, the method showOptionDialog from the class JOptionPane 
is called using the message String Array and options String Array created earlier, and its return value is assigned to 
an int result. The result is printed to the server as a String and the method returns true.

`handleMessage` - takes in a BufferedReader reader. First, this method calls the getStringArray method and stores the 
message read from the server in a String Array. If the message is null, this method returns null. Otherwise, this 
method will read in an int from the server which contains the messageType (which corresponds to 
JOptionPane.INFORMATION_MESSAGE, JOptionPane.ERROR_MESSAGE, etc.). Then, the method showMessageDialog from
the class JOptionPane is called using the message String Array and int messageType and this method returns true.

#### Interactions

This class will read in information from and print information to `ApartmentsMessager`.

#### Testing

Every test will implicitly test `Client`, since this class will constantly be interacting with the server.

Applicable Test Cases:

- Test 1: User Register
- Test 2: User Login
- Test 3: Buyer messages Seller through Stores
- Test 4: Buyer messeges Seller through Search
- Test 5: Seller messages Buyer through List
- Test 6: Seller messages Buyer through Search
- Test 7: Buyer blocks Seller
- Test 8: Buyer becomes invisible to Seller
- Test 9: Seller blocks Buyer
- Test 10: Seller becomes invisible to Buyer
- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer
- Test 15: Seller creates a Store
- Test 16: User does not exist for Login
- Test 17: User registers with comma
- Test 18: User registers with existing username
- Test 19: User logs in with incorrect password
- Test 20: User does not enter buyer or seller when Registering


### ConversationManager

#### Methods

`static` - static intialization block that assigns the fields.

`getConversation` - takes in a String user and String recipient. 

#### Testing

Applicable Test Cases:

- Test 3: Buyer messages Seller through Stores
- Test 4: Buyer messeges Seller through Search
- Test 5: Seller messages Buyer through List
- Test 6: Seller messages Buyer through Search
- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer


### FileImportExport

#### Methods

`importFile` - takes in a filename and returns a string with the contents of that file with newlines removed.

`exportCSV` - takes in the usernames of the current user and the user they are messaging, as well as the
arraylist of messages. Creates a csv file that stores all the information in these messages.

#### Interactions

Used within `ApartmentsMessager` to allow the user to either import files for sending messages or export
files for storing past conversations.

#### Testing

Applicable Test Cases:

- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file


### Message

#### Methods

`Message` - constructor that takes in the username of the sender, the message contents,
the message ID, and the timestamp of the message. It initializes the object's fields to these
values. There is another version that does not take in a timestamp.

#### Interactions

The message object is used throughout the program to encapsulate all the data a message has in a single object.
It's getters, setters, and toString method are used heavily to retrieve pieces of information about a given message.

#### Testing

Applicable Test Cases:

- Test 3: Buyer messages Seller through Stores
- Test 4: Buyer messeges Seller through Search
- Test 5: Seller messages Buyer through List
- Test 6: Seller messages Buyer through Search
- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer


### ServerProcessor

#### Methods

`sendInput` - takes in a PrintWriter writer, BufferedReader reader, and String message. First, this message prints out 
a commandHeader, "input", to the client. Then, it prints all the lines of the message to the client and returns the line
read from the client. 

`sendOptions` - takes in a PrintWriter writer, BufferedReader reader, a String message, and a String Array options. 
 First, this message prints out a commandHeader, "options", to the client. Then, it prints all the lines of the message 
 and the options as a String to the client, and returns the line read from the client. 
 
 `sendMessage` - takes in a PrintWriter writer, BufferedReader reader, and String message. First, this message prints out 
a commandHeader, "message", to the client. Then, it prints all the lines of the message to the client. 

#### Interactions

The methods in ServerProcessor are public and static, and are used to faciliate communcation between `Client` and `ApartmentsMessager`.

#### Testing

Every test will implicitly test `ServerProcessor`, since the server will call methods from this class every time it needs to 
commnicate with `Client`.

Applicable Test Cases:

- Test 1: User Register
- Test 2: User Login
- Test 3: Buyer messages Seller through Stores
- Test 4: Buyer messeges Seller through Search
- Test 5: Seller messages Buyer through List
- Test 6: Seller messages Buyer through Search
- Test 7: Buyer blocks Seller
- Test 8: Buyer becomes invisible to Seller
- Test 9: Seller blocks Buyer
- Test 10: Seller becomes invisible to Buyer
- Test 11: Seller imports '.txt' file
- Test 12: Buyer exports '.txt' file
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer
- Test 15: Seller creates a Store
- Test 16: User does not exist for Login
- Test 17: User registers with comma
- Test 18: User registers with existing username
- Test 19: User logs in with incorrect password
- Test 20: User does not enter buyer or seller when Registering


### Store

#### Methods

`Store` - constructor that takes in the seller's name and the name of the store. Then initializes fields based off that.

#### Interactions

It is used whenever the program interacts with stores to encapsulate their data. The getters and toString method are
utilized.

#### Testing

Applicable Test Cases:

- Test 3: Buyer messages Seller through Stores
- Test 15: Seller creates a Store


### User

#### Methods

`User` - constructor that takes in strings containing a user's username, email, and password, as
well as a boolean that says whether they are a seller. There is another version that lets you pass
in arraylists of blocked and invisible users, in case that past information is present.

`sendMessage` - takes in the message contents and the arraylists containing the messages for
the current user and the recipient. Creates a message object and updates the arraylist for both.

`deleteMessage` - takes in a message ID and the arraylist of messages for the current user.
Finds the message in the arraylist with the given ID and deletes it.

`editMessage` - takes in a message ID, the new message contents, the arraylists of messages for the
current user and the recipient, and the user object of the editor. Finds the message by message ID and
updates it for both arraylists.

`blockUser` - takes in the user object of the user to be blocked. Adds that user to the blocked list of the
current user.

`addInvisible` - takes in the user object of the user to be blocked. Adds that user to the invisible list
of the current user.

#### Interactions

The User class is used extensively throughout the program. Since it encapsulates all the information pertaining
to a user, a User object can be passed around to have its data modified or retrieved. The getters are used
heavily, as well as the toString method for writing to files.

#### Testing 

Applicable Test Cases:

- Test 7: Buyer blocks Seller
- Test 8: Buyer becomes invisible to Seller
- Test 9: Seller blocks Buyer
- Test 10: Seller becomes invisible to Buyer
- Test 13: Buyer edits message to Seller
- Test 14: Seller deletes message to Buyer


### UserExitException

#### Methods

`UserExitException` - constructor that takes in an error message and calls the Exception constructor.

#### Interactions

The UserExitException class is a simple class that extends Exception. Its only use is to indicate when the user exits or cancels the program.
It is thrown by the ServerProcessor methods when the client sends a null byte, and it is caught by a try block that encapsulates most of the 
main program logic.
 
