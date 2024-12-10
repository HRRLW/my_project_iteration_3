package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

/**
 * Test class for the InitLinesCommand.
 * Ensures line initialization data is correctly retrieved from the simulator and sent as JSON.
 */
public class InitLinesCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    @Mock
    private Line mockLine1;

    @Mock
    private Line mockLine2;

    private InitLinesCommand initLinesCommand;

    /**
     * Sets up the test environment by initializing mocks and the InitLinesCommand instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initLinesCommand = new InitLinesCommand(mockSimulator);
    }

    /**
     * Tests the execute method of InitLinesCommand.
     * Validates that lines are initialized correctly and the JSON response is accurate.
     */
    @Test
    public void testExecute() {
        // Arrange: Set up mock lines
        when(mockSimulator.getLines()).thenReturn(Arrays.asList(mockLine1, mockLine2));

        // Configure properties for the first mock line
        when(mockLine1.getId()).thenReturn(101);
        when(mockLine1.getName()).thenReturn("Campus Connector");
        when(mockLine1.getType()).thenReturn(Line.BUS_LINE);

        // Configure properties for the second mock line
        when(mockLine2.getId()).thenReturn(102);
        when(mockLine2.getName()).thenReturn("Central Express");
        when(mockLine2.getType()).thenReturn(Line.TRAIN_LINE);

        // Act: Execute the command
        JsonObject command = new JsonObject();
        initLinesCommand.execute(mockSession, command);

        // Capture and validate the JSON response sent to the session
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        verify(mockSession).sendJson(captor.capture());

        JsonObject sentData = captor.getValue();
        assertEquals("initLines", sentData.get("command").getAsString());
        assertEquals(2, sentData.get("numLines").getAsInt());

        JsonArray linesArray = sentData.getAsJsonArray("lines");
        assertEquals(2, linesArray.size());

        // Validate the first line
        JsonObject line1 = linesArray.get(0).getAsJsonObject();
        assertEquals(101, line1.get("id").getAsInt());
        assertEquals("Campus Connector", line1.get("name").getAsString());
        assertEquals(Line.BUS_LINE, line1.get("type").getAsString());

        // Validate the second line
        JsonObject line2 = linesArray.get(1).getAsJsonObject();
        assertEquals(102, line2.get("id").getAsInt());
        assertEquals("Central Express", line2.get("name").getAsString());
        assertEquals(Line.TRAIN_LINE, line2.get("type").getAsString());
    }
}
