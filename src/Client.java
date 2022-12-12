import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Project 5 - Client
 * <p>
 * This is the client class that interacts with the server
 *
 * @author Nick Andry, Chloe Yao Lab Sec L15
 * @version December 12, 2022
 */

public class Client {

    public static Socket socket;

    public static void main(String[] args) {
        Client client = new Client();

        try {
            socket = new Socket("localhost", 4242);
        } catch (IOException e) {
            client.createErrorMessage("Could not connect to server.");
            return;
        }

        BufferedReader reader;
        PrintWriter writer;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            client.createErrorMessage("Could not create readers/writers for socket.");
            try {
                socket.close();
            } catch (IOException ioe) {
                client.createErrorMessage("Error closing socket.");
            }
            return;
        }

        while (true) {
            if (socket.isClosed())
                return;

            String commandHeader;
            try {
                commandHeader = reader.readLine();
            } catch (IOException e) {
                continue;
            }

            boolean success = client.processCommand(commandHeader, reader, writer);
            if (!success) {
                client.createErrorMessage("Error processing server command");
            }
        }

    }

    private void createErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message,
                "Apartments Messager", JOptionPane.ERROR_MESSAGE);
    }

    private boolean processCommand(String commandHeader, BufferedReader reader, PrintWriter writer) {
        boolean success = switch (commandHeader) {
            /*
            Command looks like:
                message
                [number of lines in contents]
                [contents] * [number of lines]
                [message type number]
             */
            case "message" -> handleMessage(reader);
            /*
            Command looks like:
                options
                [number of lines in contents]
                [contents] * [number of lines]
                [options separated by commas]
             */
            case "options" -> handleOptions(reader, writer);
            /*
            Command looks like:
                input
                [number of lines in contents]
                [contents] * [number of lines]
             */
            case "input" -> handleInput(reader, writer);
            /*
            Command looks like:
                import
                filename
            */
            case "import" -> handleImport(reader, writer);
            /*
            Command looks like:
                export
                [number of lines in contents]
                [contents] * [number of lines]
             */
            case "export" -> handleExport(reader, writer);
            default -> true;
        };

        return success;
    }

    private String[] getStringArray(BufferedReader reader) {
        int lines;
        try {
            lines = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            return null;
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < lines; i++) {
            try {
                strings.add(reader.readLine());
            } catch (IOException e) {
                return null;
            }
        }

        String[] returnArray = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            returnArray[i] = strings.get(i);
        }

        return returnArray;
    }

    private boolean handleInput(BufferedReader reader, PrintWriter writer) {
        String[] message = getStringArray(reader);
        if (message == null)
            return false;

        String result;
        if (message.length > 8) {
            JList scrollable = new JList<>(message);
            JScrollPane scrollPane = new JScrollPane(scrollable);
            result = JOptionPane.showInputDialog(null,
                    scrollPane, "Apartments Messager", JOptionPane.QUESTION_MESSAGE);
        } else {
            result = JOptionPane.showInputDialog(null,
                    message, "Apartments Messager", JOptionPane.QUESTION_MESSAGE);
        }

        if (result == null) {
            writer.println('\0');
            writer.flush();
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            writer.println(result);
            writer.flush();
        }

        return true;
    }

    private boolean handleOptions(BufferedReader reader, PrintWriter writer) {
        String[] message = getStringArray(reader);
        if (message == null)
            return false;

        String[] options;
        try {
            options = reader.readLine().split(",");
        } catch (IOException e) {
            return false;
        }

        int intResult = JOptionPane.showOptionDialog(null,
                message, "Apartments Messager", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, null);
        // Adding 1 to adjust for existing option logic (options here start at 0, but they need to start at 1)
        int intResultAdjusted = intResult + 1;

        String result = String.valueOf(intResultAdjusted);

        if (result.equalsIgnoreCase("0")) {
            writer.println('\0');
            writer.flush();
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            writer.println(result);
            writer.flush();
        }

        return true;
    }

    private boolean handleMessage(BufferedReader reader) {
        String[] message = getStringArray(reader);
        if (message == null) {
            return false;
        }

        int messageType;
        try {
            messageType = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            createErrorMessage("Could not read from server.");
            return false;
        }
        if (message.length > 8) {
            JList scrollable = new JList<>(message);
            JScrollPane scrollPane = new JScrollPane(scrollable);
            JOptionPane.showMessageDialog(null, scrollPane,
                    "Apartments Messager", messageType);
        } else {
            JOptionPane.showMessageDialog(null, message,
                    "Apartments Messager", messageType);
        }


        return true;
    }

    private boolean handleImport(BufferedReader reader, PrintWriter writer) {
        String filename = "";
        try {
            filename = reader.readLine();
        } catch (IOException e) {
            return false;
        }
        //Get contents from file and send it to server
        //Null String represents error (or empty file)
        String returnValue = "";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader bfr = new BufferedReader(fr);

            String line = bfr.readLine();

            while (line != null) {
                returnValue += line;
                line = bfr.readLine();
            }
            bfr.close();
            writer.println(returnValue);
        } catch (FileNotFoundException e) {
            writer.println("");
        } catch (IOException e) {
            writer.println("");
        }
        writer.flush();
        return true;
    }

    private boolean handleExport(BufferedReader reader, PrintWriter writer) {
        String actualFile = "";
        try {
            actualFile = reader.readLine();
        } catch (IOException e) {
            return false;
        }

        String[] message = getStringArray(reader);
        if (message == null)
            return false;

        File csvFile = new File(actualFile);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(csvFile, true));
            for (String line : message) {
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            writer.println("error");
            writer.flush();
            return true;
        }
        writer.println("no errors");
        writer.flush();
        return true;
    }
}
