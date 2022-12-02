import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerProcessor {
    public static String sendInput(PrintWriter writer, BufferedReader reader, String message) {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;

        writer.println("input");
        writer.println(messageLines);
        for (String line: splitMessage) {
            writer.println(line);
        }
        writer.flush();

        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static String sendOptions(PrintWriter writer, BufferedReader reader, String message, String[] options) {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;
        String optionsString = String.join(",", options);

        writer.println("options");
        writer.println(messageLines);
        for (String line: splitMessage) {
            writer.println(line);
        }
        writer.println(optionsString);
        writer.flush();

        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static void sendMessage(PrintWriter writer, String message, int messageType) {
        String[] splitMessage = message.split("\n");
        int messageLines = splitMessage.length;

        writer.println("message");
        writer.println(messageLines);
        for(String line: splitMessage) {
            writer.println(line);
        }
        writer.println(messageType);
        writer.flush();
    }


}


