import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerDemo {

    public static void main(String[] args) {
        ServerDemo serverDemo = new ServerDemo();

        ServerSocket serverSocket;
        Socket socket;
        try {
            serverSocket = new ServerSocket(4242);
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Could not create a server.");
            return;
        }

        BufferedReader reader;
        PrintWriter writer;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not create readers/writers for socket.");
            return;
        }


        serverDemo.sendMessage(writer, "Hello", JOptionPane.PLAIN_MESSAGE);
        serverDemo.sendMessage(writer, "Hello2\nHello3\nHello4", JOptionPane.ERROR_MESSAGE);

        String result = serverDemo.sendOptions(writer, reader, "Select One\nSelect one line 2\nSelect one line 3",
                new String[] {"Option1", "Option2", "Option3", "Option4"});
        System.out.println(result);

        result = serverDemo.sendInput(writer, reader, "input\nmessage\ntest");
        System.out.println(result);
    }

    private String sendInput(PrintWriter writer, BufferedReader reader, String message) {
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

    private String sendOptions(PrintWriter writer, BufferedReader reader, String message, String[] options) {
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

    private void sendMessage(PrintWriter writer, String message, int messageType) {
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
