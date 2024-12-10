package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for GetRoutesCommand.
 * Ensures the functionality of gathering route data from the simulator and sending it in JSON format.
 */
public class GetRoutesCommandTest {

    @Mock
    private VisualTransitSimulator mockSimulator;

    @Mock
    private WebServerSession mockSession;

    @Mock
    private Line mockLine1;

    @Mock
    private Route mockOutboundRoute1;

    @Mock
    private Route mockInboundRoute1;

    @Mock
    private Line mockLine2;

    @Mock
    private Route mockOutboundRoute2;

    @Mock
    private Route mockInboundRoute2;

    @Mock
    private Stop mockStop1;

    @Mock
    private Stop mockStop2;

    private GetRoutesCommand getRoutesCommand;

    /**
     * Sets up the test environment before each test case.
     * Initializes mocks and the GetRoutesCommand object.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        getRoutesCommand = new GetRoutesCommand(mockSimulator);
    }

    /**
     * Tests the execute method of GetRoutesCommand.
     * Verifies that the correct JSON data is generated and sent to the WebServerSession.
     */
    @Test
    public void testExecute() {
        // Arrange: Set up mock data for simulator lines and routes
        when(mockSimulator.getLines()).thenReturn(Arrays.asList(mockLine1, mockLine2));

        // Configure mockLine1 with outbound and inbound routes
        when(mockLine1.getOutboundRoute()).thenReturn(mockOutboundRoute1);
        when(mockLine1.getInboundRoute()).thenReturn(mockInboundRoute1);
        when(mockOutboundRoute1.getId()).thenReturn(1);
        when(mockInboundRoute1.getId()).thenReturn(2);
        when(mockOutboundRoute1.getStops()).thenReturn(List.of(mockStop1));
        when(mockInboundRoute1.getStops()).thenReturn(List.of(mockStop2));

        // Configure mockLine2 with outbound and inbound routes
        when(mockLine2.getOutboundRoute()).thenReturn(mockOutboundRoute2);
        when(mockLine2.getInboundRoute()).thenReturn(mockInboundRoute2);
        when(mockOutboundRoute2.getId()).thenReturn(3);
        when(mockInboundRoute2.getId()).thenReturn(4);
        when(mockOutboundRoute2.getStops()).thenReturn(Arrays.asList(mockStop1, mockStop2));
        when(mockInboundRoute2.getStops()).thenReturn(Arrays.asList(mockStop2, mockStop1));

        // Act: Execute the command
        JsonObject command = new JsonObject();
        getRoutesCommand.execute(mockSession, command);

        // Assert: Verify the data sent to the session
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        verify(mockSession).sendJson(captor.capture());

        JsonObject sentData = captor.getValue();
        assertEquals("updateRoutes", sentData.get("command").getAsString());

        JsonArray routesArray = sentData.getAsJsonArray("routes");
        assertEquals(4, routesArray.size());

        // Verify the first route
        JsonObject route1 = routesArray.get(0).getAsJsonObject();
        assertEquals(1, route1.get("id").getAsInt());
        JsonArray stops1 = route1.getAsJsonArray("stops");
        assertEquals(1, stops1.size());

        // Verify the stop details for the first route
        JsonObject stop1 = stops1.get(0).getAsJsonObject();
        assertEquals(mockStop1.getId(), stop1.get("id").getAsInt());
        assertEquals(mockStop1.getPassengers().size(), stop1.get("numPeople").getAsInt());

        // Verify the second route
        JsonObject route2 = routesArray.get(1).getAsJsonObject();
        assertEquals(2, route2.get("id").getAsInt());
        JsonArray stops2 = route2.getAsJsonArray("stops");
        assertEquals(1, stops2.size());

        // Verify the stop details for the second route
        JsonObject stop2 = stops2.get(0).getAsJsonObject();
        assertEquals(mockStop2.getId(), stop2.get("id").getAsInt());
        assertEquals(mockStop2.getPassengers().size(), stop2.get("numPeople").getAsInt());

        // Verify the third route
        JsonObject route3 = routesArray.get(2).getAsJsonObject();
        assertEquals(3, route3.get("id").getAsInt());
        JsonArray stops3 = route3.getAsJsonArray("stops");
        assertEquals(2, stops3.size());

        // Verify the stop details for the third route
        JsonObject stop3 = stops3.get(0).getAsJsonObject();
        assertEquals(mockStop1.getId(), stop3.get("id").getAsInt());
        assertEquals(mockStop1.getPassengers().size(), stop3.get("numPeople").getAsInt());
        JsonObject stop4 = stops3.get(1).getAsJsonObject();
        assertEquals(mockStop2.getId(), stop4.get("id").getAsInt());
        assertEquals(mockStop2.getPassengers().size(), stop4.get("numPeople").getAsInt());

        // Verify the fourth route
        JsonObject route4 = routesArray.get(3).getAsJsonObject();
        assertEquals(4, route4.get("id").getAsInt());
        JsonArray stops4 = route4.getAsJsonArray("stops");
        assertEquals(2, stops4.size());

        // Verify the stop details for the fourth route
        JsonObject stop5 = stops4.get(0).getAsJsonObject();
        assertEquals(mockStop2.getId(), stop5.get("id").getAsInt());
        assertEquals(mockStop2.getPassengers().size(), stop5.get("numPeople").getAsInt());
        JsonObject stop6 = stops4.get(1).getAsJsonObject();
        assertEquals(mockStop1.getId(), stop6.get("id").getAsInt());
        assertEquals(mockStop1.getPassengers().size(), stop6.get("numPeople").getAsInt());
    }
}
