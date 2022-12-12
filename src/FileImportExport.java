import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * Project 5 - File Import Export
 * <p>
 * Contains functionality for importing files into a
 * conversation as well as exporting conversations into a CSV format.
 *
 * @author Arsh Batth, Rei Manning Lab Sec L15
 * @version December 12, 2022
 */

public class FileImportExport {
    // Allows us to get whatever is in the file and return it as a string to be
    // added as a message in a Message Object
    public static String importFile(String fileName, PrintWriter writer, BufferedReader reader) {
        String returnValue = "";
        returnValue += ServerProcessor.importFile(writer, reader, fileName);
        if (returnValue.equals("")) {
            ServerProcessor.sendMessage(writer, "File Import Error!", JOptionPane.ERROR_MESSAGE);
        }
        return returnValue;
    }

    // Goes through each mesage object in messageToCSV and writes them in the format
    // [timeStamp,username,messageID,message]
    public static void exportCSV(String currentUser, String otherUser, ArrayList<Message> messageToCSV,
        PrintWriter writer, BufferedReader reader) {
        String actualFile = ((currentUser) + "-" + (otherUser) + "-EXPORT" + ".csv");
        File input = new File(actualFile);
        String csv = "";
        for (int i = 0; i < messageToCSV.size(); i++) {
            csv += messageToCSV.get(i).getTimeStamp() +
                "," + messageToCSV.get(i).getUsername() +
                "," + messageToCSV.get(i).getMessageID() +
                "," + messageToCSV.get(i).getMessage() + "\n";
            }
        boolean success = ServerProcessor.exportCSV(writer, reader, actualFile, csv);
        if (!success) {
            ServerProcessor.sendMessage(writer, "Export CSV Error!", JOptionPane.ERROR_MESSAGE);
        }
        
    }
}