import java.util.*;
import java.io.*;

/**
 * Project 4 - File Import Export
 * <p>
 * Contains functionality for importing files into a
 * conversation as well as exporting conversations into a CSV format.
 *
 * @author Arsh Batth, Lab Sec L15
 * @version November 13th, 2022
 */

public class FileImportExport {
    // Allows us to get whatever is in the file and return it as a string to be
    // added as a message in a Message Object
    public static String importFile(String fileName) {
        String returnValue = "";
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader bfr = new BufferedReader(fr);

            String line = bfr.readLine();

            while (line != null) {
                returnValue += line;
                line = bfr.readLine();
            }
            bfr.close();
        } catch (FileNotFoundException e) {
            System.out.println("Import File Error!");
        } catch (IOException e) {
            System.out.println("Import File Error!");
        }
        return returnValue;
    }

    // Goes through each mesage object in messageToCSV and writes them in the format
    // [timeStamp,username,messageID,message]
    public static void exportCSV(String currentUser, String otherUser, ArrayList<Message> messageToCSV) {
        String actualFile = ((currentUser) + "-" + (otherUser) + "-EXPORT" + ".csv");
        File input = new File(actualFile);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(input, false));
            for (int i = 0; i < messageToCSV.size(); i++) {
                bw.write(messageToCSV.get(i).getTimeStamp() +
                        "," + messageToCSV.get(i).getUsername() +
                        "," + messageToCSV.get(i).getMessageID() +
                        "," + messageToCSV.get(i).getMessage() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Export CSV Error!");
        }
    }
}