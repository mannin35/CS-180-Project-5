import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Project 5 - Server Processor
 * <p>
 * This is the class that facilitates communication between the server and client classes
 *
 * @author Nick Andry, Lab Sec L15
 * @version December 12, 2022
 */

public class ServerProcessor {
    public static String sendInput(PrintWriter writer, BufferedReader reader, String message) throws UserExitException {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;

        writer.println("input");
        writer.println(messageLines);
        for (String line : splitMessage) {
            writer.println(line);
        }
        writer.flush();

        try {
            String result = reader.readLine();
            if (result.equals("\0"))
                throw new UserExitException("User exited the program");
            else
                return result;
        } catch (IOException e) {
            return null;
        }
    }

    public static String sendOptions(PrintWriter writer, BufferedReader reader, String message, String[] options) throws UserExitException {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;
        String optionsString = String.join(",", options);

        writer.println("options");
        writer.println(messageLines);
        for (String line : splitMessage) {
            writer.println(line);
        }
        writer.println(optionsString);
        writer.flush();

        try {
            String result = reader.readLine();
            if (result.equals("\0"))
                throw new UserExitException("User exited the program");
            else
                return result;
        } catch (IOException e) {
            return null;
        }
    }

    public static void sendMessage(PrintWriter writer, String message, int messageType) {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;

        writer.println("message");
        writer.println(messageLines);
        for (String line : splitMessage) {
            writer.println(line);
        }
        writer.println(messageType);
        writer.flush();
    }

    public static String importFile(PrintWriter writer, BufferedReader reader, String filename) {
        String contents = "";

        writer.println("import");
        writer.println(filename);
        writer.flush();
        try {
            contents += reader.readLine();
        } catch (IOException e) {
            return "";
        }
        return contents;
    }

    public static boolean exportCSV(PrintWriter writer, BufferedReader reader, String filename, String csv) {
        String[] splitMessage = csv.split("\n");
        int messageLines = splitMessage.length;

        writer.println("export");
        writer.println(filename);
        writer.println(messageLines);
        for (String line : splitMessage) {
            writer.println(line);
        }
        writer.flush();
        try {
            String result = reader.readLine();
            if (result.equals("error")) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }
}


