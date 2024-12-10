package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for StartCommand.
 * Validates that the simulator is correctly initialized and started based on the input command.
 */
public class StartCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    private StartCommand startCommand;

    /**
     * Sets up the test environment with mock objects and initializes the command.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        startCommand = new StartCommand(mockSimulator);
    }

    /**
     * Tests that the simulator is started with correct parameters.
     */
    @Test
    public void testExecute() {
        // Arrange: Create a mock command
        JsonObject command = new JsonObject();
        command.addProperty("command", "start");
        command.addProperty("numTimeSteps", 100);
        JsonArray timeBetweenVehicles = new JsonArray();
        timeBetweenVehicles.add(5);
        timeBetweenVehicles.add(10);
        command.add("timeBetweenVehicles", timeBetweenVehicles);

        // Act: Execute the start command
        startCommand.execute(mockSession, command);

        // Assert: Verify simulator's start is called with correct parameters
        ArgumentCaptor<List<Integer>> timingsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Integer> numTimeStepsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockSimulator).start(timingsCaptor.capture(), numTimeStepsCaptor.capture());

        assertEquals(Arrays.asList(5, 10), timingsCaptor.getValue());
        assertEquals(100, numTimeStepsCaptor.getValue());
    }

    /**
     * Tests behavior when the timeBetweenVehicles array is empty.
     */
    @Test
    public void testExecute_EmptyTimeBetweenVehicles() {
        // Arrange: Create a mock command with an empty array
        JsonObject command = new JsonObject();
        command.addProperty("command", "start");
        command.addProperty("numTimeSteps", 50);
        command.add("timeBetweenVehicles", new JsonArray());

        // Act: Execute the start command
        startCommand.execute(mockSession, command);

        // Assert: Verify simulator's start is called with empty timing list
        ArgumentCaptor<List<Integer>> timingsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Integer> numTimeStepsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockSimulator).start(timingsCaptor.capture(), numTimeStepsCaptor.capture());

        assertTrue(timingsCaptor.getValue().isEmpty());
        assertEquals(50, numTimeStepsCaptor.getValue());
    }
}
