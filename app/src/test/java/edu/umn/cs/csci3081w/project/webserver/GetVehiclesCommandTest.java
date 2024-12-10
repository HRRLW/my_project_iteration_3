package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class GetVehiclesCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    @Mock
    private SmallBusColorDecorator mockSmallBus;

    @Mock
    private LargeBusColorDecorator mockLargeBus;

    @Mock
    private ElectricTrainColorDecorator mockElectricTrain;

    @Mock
    private DieselTrainColorDecorator mockDieselTrain;

    private GetVehiclesCommand getVehiclesCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        getVehiclesCommand = new GetVehiclesCommand(mockSimulator);
    }

    @Test
    public void testExecute() {
        // Arrange: Set up mock vehicle data
        List<Vehicle> mockVehicles = Arrays.asList(mockSmallBus, mockLargeBus, mockElectricTrain, mockDieselTrain);
        when(mockSimulator.getActiveVehicles()).thenReturn(mockVehicles);

        // Configure mockSmallBus properties
        when(mockSmallBus.getId()).thenReturn(1);
        when(mockSmallBus.getPassengers()).thenReturn(Arrays.asList(new Passenger(1, "Alice")));
        when(mockSmallBus.getCapacity()).thenReturn(20);
        when(mockSmallBus.getCurrentCO2Emission()).thenReturn(5);
        when(mockSmallBus.getPosition()).thenReturn(new Position(-93.243774, 44.972392));

        // Configure mockLargeBus properties
        when(mockLargeBus.getId()).thenReturn(2);
        when(mockLargeBus.getPassengers()).thenReturn(Arrays.asList(new Passenger(2, "Bob")));
        when(mockLargeBus.getCapacity()).thenReturn(50);
        when(mockLargeBus.getCurrentCO2Emission()).thenReturn(15);
        when(mockLargeBus.getPosition()).thenReturn(new Position(-93.243975, 44.973001));

        // Configure mockElectricTrain properties
        when(mockElectricTrain.getId()).thenReturn(3);
        when(mockElectricTrain.getPassengers()).thenReturn(Arrays.asList(new Passenger(3, "Charlie")));
        when(mockElectricTrain.getCapacity()).thenReturn(100);
        when(mockElectricTrain.getCurrentCO2Emission()).thenReturn(0);
        when(mockElectricTrain.getPosition()).thenReturn(new Position(-93.245001, 44.974500));

        // Configure mockDieselTrain properties
        when(mockDieselTrain.getId()).thenReturn(4);
        when(mockDieselTrain.getPassengers()).thenReturn(Arrays.asList(new Passenger(4, "Diana")));
        when(mockDieselTrain.getCapacity()).thenReturn(80);
        when(mockDieselTrain.getCurrentCO2Emission()).thenReturn(20);
        when(mockDieselTrain.getPosition()).thenReturn(new Position(-93.246001, 44.975001));

        // Act: Execute the command
        JsonObject command = new JsonObject();
        getVehiclesCommand.execute(mockSession, command);

        // Capture and validate the sent JSON data
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        verify(mockSession).sendJson(captor.capture());

        JsonObject sentData = captor.getValue();
        assertEquals("updateVehicles", sentData.get("command").getAsString());

        JsonArray vehiclesArray = sentData.getAsJsonArray("vehicles");
        assertEquals(4, vehiclesArray.size());

        // Validate each vehicle
        JsonObject vehicle1 = vehiclesArray.get(0).getAsJsonObject();
        assertEquals(1, vehicle1.get("id").getAsInt());
        assertEquals(1, vehicle1.get("numPassengers").getAsInt());
        assertEquals(20, vehicle1.get("capacity").getAsInt());
        assertEquals(SmallBus.SMALL_BUS_VEHICLE, vehicle1.get("type").getAsString());
        assertEquals(5, vehicle1.get("co2").getAsInt());
        JsonObject position1 = vehicle1.getAsJsonObject("position");
        assertEquals(-93.243774, position1.get("longitude").getAsDouble(), 0.0001);
        assertEquals(44.972392, position1.get("latitude").getAsDouble(), 0.0001);

        JsonObject vehicle4 = vehiclesArray.get(3).getAsJsonObject();
        assertEquals(4, vehicle4.get("id").getAsInt());
        assertEquals(1, vehicle4.get("numPassengers").getAsInt());
        assertEquals(80, vehicle4.get("capacity").getAsInt());
        assertEquals(DieselTrain.DIESEL_TRAIN_VEHICLE, vehicle4.get("type").getAsString());
        assertEquals(20, vehicle4.get("co2").getAsInt());
        JsonObject position4 = vehicle4.getAsJsonObject("position");
        assertEquals(-93.246001, position4.get("longitude").getAsDouble(), 0.0001);
        assertEquals(44.975001, position4.get("latitude").getAsDouble(), 0.0001);
        JsonObject color4 = vehicle4.getAsJsonObject("color");
        assertEquals(255, color4.get("r").getAsInt()); // Default color
        assertEquals(255, color4.get("g").getAsInt());
        assertEquals(255, color4.get("b").getAsInt());
        assertEquals(255, color4.get("alpha").getAsInt());
    }
}
