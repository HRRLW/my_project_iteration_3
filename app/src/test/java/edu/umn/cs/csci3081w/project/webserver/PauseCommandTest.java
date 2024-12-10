package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PauseCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    private PauseCommand pauseCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pauseCommand = new PauseCommand(mockSimulator);
    }

    /**
     * Test the execute method of PauseCommand to make sure that the pause state is toggled correctly
     */
    @Test
    public void testExecute() {
        JsonObject command = new JsonObject();
        pauseCommand.execute(mockSession, command);

        verify(mockSimulator, times(1)).togglePause();

        pauseCommand.execute(mockSession, command);

        verify(mockSimulator, times(2)).togglePause();
    }
}
