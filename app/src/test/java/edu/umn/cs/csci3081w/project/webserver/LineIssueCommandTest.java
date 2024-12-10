package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for LineIssueCommand.
 * Verifies the correct injection of issues into specified lines based on the command.
 */
public class LineIssueCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    @Mock
    private Line mockLine1;

    @Mock
    private Line mockLine2;

    private LineIssueCommand lineIssueCommand;

    /**
     * Sets up the test environment by initializing mocks and the LineIssueCommand instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        lineIssueCommand = new LineIssueCommand(mockSimulator);
    }

    /**
     * Tests the execute method of LineIssueCommand.
     * Ensures the correct line is identified and an issue is injected.
     */
    @Test
    public void testExecute() {
        // Arrange: Set up mock lines and IDs
        List<Line> mockLines = Arrays.asList(mockLine1, mockLine2);
        when(mockSimulator.getLines()).thenReturn(mockLines);

        // Set IDs for the mock lines
        when(mockLine1.getId()).thenReturn(201);
        when(mockLine2.getId()).thenReturn(202);

        // Act: Inject issue into mockLine1
        JsonObject command = new JsonObject();
        command.addProperty("command", "lineIssue");
        command.addProperty("id", 201);
        lineIssueCommand.execute(mockSession, command);

        // Assert: Verify only mockLine1.createIssue() was called
        verify(mockLine1, times(1)).createIssue();
        verify(mockLine2, never()).createIssue();

        // Act: Inject issue into mockLine2
        JsonObject command2 = new JsonObject();
        command2.addProperty("command", "lineIssue");
        command2.addProperty("id", 202);
        lineIssueCommand.execute(mockSession, command2);

        // Assert: Verify only mockLine2.createIssue() was called
        verify(mockLine2, times(1)).createIssue();
    }

    /**
     * Tests the execute method of LineIssueCommand when the specified line ID does not exist.
     * Ensures no issues are injected into any lines.
     */
    @Test
    public void testExecute_LineNotFound() {
        // Arrange: Set up mock lines and IDs
        List<Line> mockLines = Arrays.asList(mockLine1, mockLine2);
        when(mockSimulator.getLines()).thenReturn(mockLines);

        // Set IDs for the mock lines
        when(mockLine1.getId()).thenReturn(201);
        when(mockLine2.getId()).thenReturn(202);

        // Act: Attempt to inject issue into a non-existent line
        JsonObject command = new JsonObject();
        command.addProperty("command", "lineIssue");
        command.addProperty("id", 999);
        lineIssueCommand.execute(mockSession, command);

        // Assert: Verify no createIssue() method was called
        verify(mockLine1, never()).createIssue();
        verify(mockLine2, never()).createIssue();
    }
}
