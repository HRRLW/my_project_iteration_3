package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for RegisterVehicleCommand.
 * Ensures that vehicles are correctly registered as observers in the simulation.
 */
public class RegisterVehicleCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    @Mock
    private Vehicle mockVehicle1;

    @Mock
    private Vehicle mockVehicle2;

    private RegisterVehicleCommand registerVehicleCommand;

    /**
     * Sets up the test environment with mock objects and initializes the command.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registerVehicleCommand = new RegisterVehicleCommand(mockSimulator);
    }

    /**
     * Tests that a vehicle is registered as an observer when the ID matches an active vehicle.
     */
    @Test
    public void testExecute() {
        // Arrange: Mock active vehicles
        List<Vehicle> mockVehicles = Arrays.asList(mockVehicle1, mockVehicle2);
        when(mockSimulator.getActiveVehicles()).thenReturn(mockVehicles);

        // Arrange: Mock vehicle properties
        when(mockVehicle1.getId()).thenReturn(501);
        when(mockVehicle2.getId()).thenReturn(502);

        // Act: Register mockVehicle1
        JsonObject command = new JsonObject();
        command.addProperty("command", "registerVehicle");
        command.addProperty("id", 501);
        registerVehicleCommand.execute(mockSession, command);

        // Assert: Verify mockVehicle1 is added as an observer
        verify(mockSimulator, times(1)).addObserver(mockVehicle1);

        // Act: Register mockVehicle2
        JsonObject command2 = new JsonObject();
        command2.addProperty("command", "registerVehicle");
        command2.addProperty("id", 502);
        registerVehicleCommand.execute(mockSession, command2);

        // Assert: Verify mockVehicle2 is added as an observer
        verify(mockSimulator, times(1)).addObserver(mockVehicle2);
    }

    /**
     * Tests that no vehicle is registered if the specified ID does not match any active vehicle.
     */
    @Test
    public void testExecute_VehicleNotFound() {
        // Arrange: Mock active vehicles
        List<Vehicle> mockVehicles = Arrays.asList(mockVehicle1, mockVehicle2);
        when(mockSimulator.getActiveVehicles()).thenReturn(mockVehicles);

        // Act: Attempt to register a non-existent vehicle
        JsonObject command = new JsonObject();
        command.addProperty("command", "registerVehicle");
        command.addProperty("id", 999);
        registerVehicleCommand.execute(mockSession, command);

        // Assert: Verify no observer is added
        verify(mockSimulator, never()).addObserver(any(Vehicle.class));
    }
}
