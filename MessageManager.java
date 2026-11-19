import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class MessageManager {

    private static final int MAX_SIZE = 100;
    
    public String[] sentMessages = new String[MAX_SIZE];
    public String[] disregardedMessages = new String[MAX_SIZE];
    public String[] storedMessages = new String[MAX_SIZE];
    public String[] messageHashes = new String[MAX_SIZE];
    public String[] messageIDs = new String[MAX_SIZE];
    public String[] recipients = new String[MAX_SIZE];
    public String[] senders = new String[MAX_SIZE]; // Developer/Sender

    public int sentCount = 0;
    public int disregardedCount = 0;
    public int storedCount = 0;
    public int totalCount = 0;

    public void addMessage(String id, String hash, String sender, String recipient, String message, String flag) {
        messageIDs[totalCount] = id;
        messageHashes[totalCount] = hash;
        senders[totalCount] = sender;
        recipients[totalCount] = recipient;
        totalCount++;

        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages[sentCount++] = message;
        } else if (flag.equalsIgnoreCase("Disregard") || flag.equalsIgnoreCase("Disregarded")) {
            disregardedMessages[disregardedCount++] = message;
        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages[storedCount++] = message;
        }
    }

    public void loadStoredMessagesFromJSON(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object obj : jsonArray) {
                JSONObject msgObj = (JSONObject) obj;
                String id = (String) msgObj.get("MessageID");
                String hash = (String) msgObj.get("MessageHash");
                String sender = (String) msgObj.getOrDefault("Developer", "SystemUser");
                String recipient = (String) msgObj.get("Recipient");
                String text = (String) msgObj.get("Message");
                String flag = (String) msgObj.getOrDefault("Flag", "Stored");

                addMessage(id, hash, sender, recipient, text, flag);
            }
        } catch (Exception e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
    }

    public void storedMessagesMenu() {
        String menu = "Stored Messages Menu:\n"
                + "a. Display sender and recipient of all stored messages\n"
                + "b. Display the longest stored message\n"
                + "c. Search for a message ID\n"
                + "d. Search for all messages for a particular recipient\n"
                + "e. Delete a message using the message hash\n"
                + "f. Display full details report\n"
                + "x. Back to Main Menu";

        String choice = "";
        while (!choice.equalsIgnoreCase("x")) {
            choice = JOptionPane.showInputDialog(null, menu, "Stored Messages", JOptionPane.QUESTION_MESSAGE);
            if (choice == null) break;

            switch (choice.toLowerCase()) {
                case "a":
                    JOptionPane.showMessageDialog(null, displaySendersAndRecipients());
                    break;
                case "b":
                    JOptionPane.showMessageDialog(null, getLongestMessage());
                    break;
                case "c":
                    String searchId = JOptionPane.showInputDialog("Enter Message ID to search:");
                    JOptionPane.showMessageDialog(null, searchByMessageID(searchId));
                    break;
                case "d":
                    String searchRec = JOptionPane.showInputDialog("Enter Recipient number to search:");
                    JOptionPane.showMessageDialog(null, searchByRecipient(searchRec));
                    break;
                case "e":
                    String delHash = JOptionPane.showInputDialog("Enter Message Hash to delete:");
                    JOptionPane.showMessageDialog(null, deleteByHash(delHash));
                    break;
                case "f":
                    JOptionPane.showMessageDialog(null, displayFullReport());
                    break;
                case "x":
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Choose a - f or x.");
            }
        }
    }

    public String displaySendersAndRecipients() {
        StringBuilder sb = new StringBuilder("Senders & Recipients:\n");
        for (int i = 0; i < totalCount; i++) {
            sb.append("Sender: ").append(senders[i])
              .append(" | Recipient: ").append(recipients[i]).append("\n");
        }
        return sb.toString();
    }

    public String getLongestMessage() {
        String longest = "";
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
         
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].length() > longest.length()) {
                longest = sentMessages[i];
            }
        }
        return longest;
    }

    public String searchByMessageID(String targetID) {
        for (int i = 0; i < totalCount; i++) {
            if (messageIDs[i] != null && messageIDs[i].equals(targetID)) {
                String msgText = findMessageTextByIndex(i);
                return "Recipient: " + recipients[i] + "\nMessage: " + msgText;
            }
        }
        return "Message ID not found.";
    }

    public String searchByRecipient(String targetRecipient) {
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < totalCount; i++) {
            if (recipients[i] != null && recipients[i].equals(targetRecipient)) {
                String msgText = findMessageTextByIndex(i);
                if (!msgText.isEmpty()) {
                    results.append(msgText).append("\n");
                }
            }
        }
        return results.length() > 0 ? results.toString().trim() : "No messages found for recipient.";
    }

    public String deleteByHash(String targetHash) {
        for (int i = 0; i < totalCount; i++) {
            if (messageHashes[i] != null && messageHashes[i].equals(targetHash)) {
                String deletedMsg = findMessageTextByIndex(i);
                
                for (int j = 0; j < storedCount; j++) {
                    if (storedMessages[j] != null && storedMessages[j].equals(deletedMsg)) {
                        for (int k = j; k < storedCount - 1; k++) {
                            storedMessages[k] = storedMessages[k + 1];
                        }
                        storedMessages[--storedCount] = null;
                        break;
                    }
                }

                for (int k = i; k < totalCount - 1; k++) {
                    messageIDs[k] = messageIDs[k + 1];
                    messageHashes[k] = messageHashes[k + 1];
                    recipients[k] = recipients[k + 1];
                    senders[k] = senders[k + 1];
                }
                totalCount--;

                return "Message: \"" + deletedMsg + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    public String displayFullReport() {
        StringBuilder report = new StringBuilder("--- MESSAGE REPORT ---\n");
        for (int i = 0; i < totalCount; i++) {
            String msgText = findMessageTextByIndex(i);
            if (!msgText.isEmpty()) {
                report.append("Message Hash: ").append(messageHashes[i]).append("\n")
                      .append("Recipient: ").append(recipients[i]).append("\n")
                      .append("Message: ").append(msgText).append("\n")
                      .append("-----------------------------\n");
            }
        }
        return report.toString();
    }

    private String findMessageTextByIndex(int parallelIndex) {
        int activeCounter = 0;
        for (int s = 0; s < sentCount; s++) {
            if (activeCounter == parallelIndex) return sentMessages[s];
            activeCounter++;
        }
        for (int st = 0; st < storedCount; st++) {
            if (activeCounter == parallelIndex) return storedMessages[st];
            activeCounter++;
        }
        return "";
    }
}