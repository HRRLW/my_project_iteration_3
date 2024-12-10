package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for UpdateCommand.
 * Verifies that the simulator's update method is called correctly.
 */
public class UpdateCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    private UpdateCommand updateCommand;

    /**
     * Sets up the test environment with mock objects and initializes the command.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        updateCommand = new UpdateCommand(mockSimulator);
    }

    /**
     * Tests that the update method is called exactly the expected number of times.
     */
    @Test
    public void testExecute() {
        // Arrange: Create a mock command
        JsonObject command = new JsonObject();

        // Act: Execute the update command three times
        updateCommand.execute(mockSession, command);
        updateCommand.execute(mockSession, command);
        updateCommand.execute(mockSession, command);

        // Assert: Verify the simulator's update method was called three times
        verify(mockSimulator, times(3)).update();
    }
}
