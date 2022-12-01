import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientDemo {


    public static void main(String[] args) {
        ClientDemo clientDemo = new ClientDemo();

        Socket socket;
        try {
            socket = new Socket("localhost", 4242);
        } catch (IOException e) {
            clientDemo.createErrorMessage("Could not connect to server.");
            return;
        }

        BufferedReader reader;
        PrintWriter writer;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            clientDemo.createErrorMessage("Could not create readers/writers for socket.");
            try {
                socket.close();
            } catch (IOException ioe) {
                clientDemo.createErrorMessage("Error closing socket.");
            }
            return;
        }

        while (true) {
            String commandHeader;
            try {
                commandHeader = reader.readLine();
            } catch (IOException e) {
                continue;
            }

            boolean success = clientDemo.processCommand(commandHeader, reader, writer);
            if (!success) {
                clientDemo.createErrorMessage("Error processing server command");
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
            default -> true;
        };

        return success;
    }

    private String getMultilineString(BufferedReader reader) {
        int lines;
        try {
            lines = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            return null;
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < lines; i ++) {
            try {
                strings.add(reader.readLine());
            } catch (IOException e) {
                return null;
            }
        }

        return String.join("\n", strings);
    }

    private boolean handleInput(BufferedReader reader, PrintWriter writer) {
        String message = getMultilineString(reader);
        if (message == null)
            return false;

        String result = JOptionPane.showInputDialog(null,
                message, "Apartments Messager", JOptionPane.QUESTION_MESSAGE);

        writer.println(result);
        writer.flush();

        return true;
    }

    private boolean handleOptions(BufferedReader reader, PrintWriter writer) {
        String message = getMultilineString(reader);
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

        writer.println(result);
        writer.flush();

        return true;
    }

    private boolean handleMessage(BufferedReader reader) {
        String message = getMultilineString(reader);
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


        JOptionPane.showMessageDialog(null, message,
                "Apartments Messager", messageType);

        return true;
    }
}