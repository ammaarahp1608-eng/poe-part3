import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class MessageManagerTest {

    private MessageManager manager;

    @BeforeEach
    public void setUp() {
        manager = new MessageManager();

        // Populate using Test Data Messages 1 - 5
        // Message 1 (Sent)
        manager.addMessage("ID001", "00:1:DIDCAKE?", "Developer1", "+27834557896",
                "Did you get the cake?", "Sent");

        // Message 2 (Stored)
        manager.addMessage("ID002", "00:2:WHERETIME.", "Developer1", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "Stored");

        // Message 3 (Disregard)
        manager.addMessage("ID003", "00:3:YOHOOGATE.", "Developer1", "+27834484567",
                "Yohoooo, I am at your gate.", "Disregard");

        // Message 4 (Sent) - Note: Test data lists 0838884567 as ID/Developer
        manager.addMessage("0838884567", "00:4:ITTIME!", "0838884567", "+27838884567",
                "It is dinner time !", "Sent");

        // Message 5 (Stored)
        manager.addMessage("ID005", "00:5:OKYOU.", "Developer1", "+27838884567",
                "Ok, I am leaving without you.", "Stored");
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        // Verify Sent array contains Message 1 and Message 4
        assertEquals("Did you get the cake?", manager.sentMessages[0]);
        assertEquals("It is dinner time !", manager.sentMessages[1]);
    }

    @Test
    public void testDisplayLongestMessage() {
        // Longest message among test data is Message 2
        String expected = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expected, manager.getLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        // Search for Message 4 using ID "0838884567"
        String result = manager.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchMessagesByRecipient() {
        // Search for +27838884567
        String result = manager.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        // Delete Test Message 2 using its hash
        String expected = "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";
        String actual = manager.deleteByHash("00:2:WHERETIME.");
        assertEquals(expected, actual);
    }

    @Test
    public void testDisplayReport() {
        // Verify report includes Message Hash, Recipient, and Message
        String report = manager.displayFullReport();
        assertTrue(report.contains("Message Hash: 00:1:DIDCAKE?"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
    }
}