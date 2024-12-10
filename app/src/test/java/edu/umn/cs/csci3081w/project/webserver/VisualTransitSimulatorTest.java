package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.umn.cs.csci3081w.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test class for VisualTransitSimulator.
 */
public class VisualTransitSimulatorTest {

    @Mock
    private WebServerSession mockSession;

    @Mock
    private Line mockLine;

    @Mock
    private Route mockOutboundRoute;

    @Mock
    private Route mockInboundRoute;

    @Mock
    private StorageFacility mockStorageFacility;

    @Mock
    private BusFactory mockBusFactory;

    @Mock
    private TrainFactory mockTrainFactory;

    @Mock
    private Vehicle mockVehicle;

    private VisualTransitSimulator simulator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        simulator = new VisualTransitSimulator(
                "src\\main\\resources\\config.txt",
                mockSession
        );

        simulator.setVehicleFactories(LocalDateTime.now().getHour());
        simulator.start(List.of(1,2,3,4,5), 50);

        // Mock the ConfigManager's lines and storage facility
        when(mockLine.getOutboundRoute()).thenReturn(mockOutboundRoute);
        when(mockLine.getInboundRoute()).thenReturn(mockInboundRoute);

        simulator.getLines().add(mockLine); // Add a mocked line
    }

    /**
     * Test starting the simulation.
     */
    @Test
    public void testStart() {
        List<Integer> vehicleStartTimings = Arrays.asList(5, 10, 15);
        int numTimeSteps = 100;

        simulator.start(vehicleStartTimings, numTimeSteps);

        assertEquals(vehicleStartTimings, getPrivateField("vehicleStartTimings"));
        assertEquals(numTimeSteps, getPrivateField("numTimeSteps"));
        assertEquals(0, getPrivateField("simulationTimeElapsed"));
    }

    /**
     * Test updating the simulation when not paused.
     */
    @Test
    public void testUpdateNotPaused() {
        List<Integer> vehicleStartTimings = Arrays.asList(5, 10);
        int numTimeSteps = 3;

        simulator.start(vehicleStartTimings, numTimeSteps);

        when(mockBusFactory.generateVehicle(any(Line.class))).thenReturn(mockVehicle);
        when(mockTrainFactory.generateVehicle(any(Line.class))).thenReturn(mockVehicle);

        simulator.update();
        assertEquals(1, getPrivateField("simulationTimeElapsed"));

        simulator.update();
        assertEquals(2, getPrivateField("simulationTimeElapsed"));

        verify(mockVehicle, times(0)).update(); // As mockVehicle is not part of activeVehicles
    }

    /**
     * Test updating the simulation when paused.
     */
    @Test
    public void testUpdatePaused() {
        simulator.togglePause(); // Pause the simulation
        simulator.update();

        assertEquals(0, getPrivateField("simulationTimeElapsed"));
    }

    /**
     * Test registering an observer.
     */
    @Test
    public void testAddObserver() {
        simulator.addObserver(mockVehicle);
    }

    /**
     * Helper method to retrieve private fields using reflection.
     */
    private Object getPrivateField(String fieldName) {
        try {
            var field = VisualTransitSimulator.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(simulator);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing field: " + e.getMessage());
        }
    }
}
