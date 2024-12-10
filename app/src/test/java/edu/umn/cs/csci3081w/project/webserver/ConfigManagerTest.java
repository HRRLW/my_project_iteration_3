package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.*;

import edu.umn.cs.csci3081w.project.model.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ConfigManager class.
 */
public class ConfigManagerTest {

    private ConfigManager configManager;
    private Counter counter;
    private File tempConfigFile;

    /**
     * Setup operations before each test runs.
     */
    @BeforeEach
    public void setUp() throws Exception {
        configManager = new ConfigManager();
        counter = new Counter();
        // Create a temporary configuration file
        tempConfigFile = File.createTempFile("config", ".txt");
    }

    /**
     * Clean up after each test.
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (tempConfigFile != null && tempConfigFile.exists()) {
            tempConfigFile.delete();
        }
    }

    /**
     * Helper method to write content to the temporary config file.
     *
     * @param content The content to write.
     * @throws Exception If an I/O error occurs.
     */
    private void writeToConfigFile(String content) throws Exception {
        FileWriter writer = new FileWriter(tempConfigFile, StandardCharsets.UTF_8);
        writer.write(content);
        writer.close();
    }

    /**
     * Tests reading a valid configuration file with one line, two routes, two stops, and storage facilities.
     */
    @Test
    public void testReadValidConfig() throws Exception {
        String configContent = ""
                + "LINE_START,BUS_LINE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "STOP,Stop2,44.973580,-93.235071,0.2\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.3\n"
                + "STOP,Stop1,44.972392,-93.243774,0.4\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "STORAGE_FACILITY_START\n"
                + "SMALL_BUSES,10\n"
                + "LARGE_BUSES,5\n"
                + "ELECTRIC_TRAINS,3\n"
                + "DIESEL_TRAINS,2\n";

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(1, lines.size(), "There should be one line parsed.");
        Line line1 = lines.get(0);
        assertEquals("Line1", line1.getName(), "Line name should be Line1.");
        assertEquals(Line.BUS_LINE, line1.getType(), "Line type should be BUS_LINE.");

        // Verify routes
        List<Route> routes = configManager.getRoutes();
        assertEquals(2, routes.size(), "There should be two routes parsed.");
        Route route1 = routes.get(0);
        assertEquals("Route1", route1.getName(), "First route name should be Route1.");
        assertEquals(2, route1.getStops().size(), "Route1 should have two stops.");
        assertEquals("Stop1", route1.getStops().get(0).getName(), "First stop of Route1 should be Stop1.");
        assertEquals("Stop2", route1.getStops().get(1).getName(), "Second stop of Route1 should be Stop2.");

        Route route2 = routes.get(1);
        assertEquals("Route2", route2.getName(), "Second route name should be Route2.");
        assertEquals(2, route2.getStops().size(), "Route2 should have two stops.");
        assertEquals("Stop2", route2.getStops().get(0).getName(), "First stop of Route2 should be Stop2.");
        assertEquals("Stop1", route2.getStops().get(1).getName(), "Second stop of Route2 should be Stop1.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNotNull(storage, "StorageFacility should not be null.");
        assertEquals(10, storage.getSmallBusesNum(), "Number of small buses should be 10.");
        assertEquals(5, storage.getLargeBusesNum(), "Number of large buses should be 5.");
        assertEquals(3, storage.getElectricTrainsNum(), "Number of electric trains should be 3.");
        assertEquals(2, storage.getDieselTrainsNum(), "Number of diesel trains should be 2.");
    }

    /**
     * Tests reading a configuration file with multiple lines.
     */
    @Test
    public void testReadConfigWithMultipleLines() throws Exception {
        String configContent = ""
                + "LINE_START,BUS_LINE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "STOP,Stop2,44.973580,-93.235071,0.2\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.3\n"
                + "STOP,Stop1,44.972392,-93.243774,0.4\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "LINE_START,TRAIN_LINE,Line2\n"
                + "ROUTE_START,Route3\n"
                + "STOP,Stop3,44.974000,-93.240000,0.5\n"
                + "STOP,Stop4,44.975000,-93.250000,0.6\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route4\n"
                + "STOP,Stop4,44.975000,-93.250000,0.7\n"
                + "STOP,Stop3,44.974000,-93.240000,0.8\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "STORAGE_FACILITY_START\n"
                + "SMALL_BUSES,15\n"
                + "LARGE_BUSES,7\n"
                + "ELECTRIC_TRAINS,4\n"
                + "DIESEL_TRAINS,3\n";

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(2, lines.size(), "There should be two lines parsed.");

        Line line1 = lines.get(0);
        assertEquals("Line1", line1.getName(), "First line name should be Line1.");
        assertEquals(Line.BUS_LINE, line1.getType(), "First line type should be BUS_LINE.");
        assertEquals("Route1", line1.getOutboundRoute().getName(), "Line1 outbound route should be Route1.");
        assertEquals("Route2", line1.getInboundRoute().getName(), "Line1 inbound route should be Route2.");

        Line line2 = lines.get(1);
        assertEquals("Line2", line2.getName(), "Second line name should be Line2.");
        assertEquals(Line.TRAIN_LINE, line2.getType(), "Second line type should be TRAIN_LINE.");
        assertEquals("Route3", line2.getOutboundRoute().getName(), "Line2 outbound route should be Route3.");
        assertEquals("Route4", line2.getInboundRoute().getName(), "Line2 inbound route should be Route4.");

        // Verify routes
        List<Route> routes = configManager.getRoutes();
        assertEquals(4, routes.size(), "There should be four routes parsed.");

        Route route1 = routes.get(0);
        assertEquals("Route1", route1.getName(), "First route name should be Route1.");
        assertEquals(2, route1.getStops().size(), "Route1 should have two stops.");
        assertEquals("Stop1", route1.getStops().get(0).getName(), "First stop of Route1 should be Stop1.");
        assertEquals("Stop2", route1.getStops().get(1).getName(), "Second stop of Route1 should be Stop2.");

        Route route2 = routes.get(1);
        assertEquals("Route2", route2.getName(), "Second route name should be Route2.");
        assertEquals(2, route2.getStops().size(), "Route2 should have two stops.");
        assertEquals("Stop2", route2.getStops().get(0).getName(), "First stop of Route2 should be Stop2.");
        assertEquals("Stop1", route2.getStops().get(1).getName(), "Second stop of Route2 should be Stop1.");

        Route route3 = routes.get(2);
        assertEquals("Route3", route3.getName(), "Third route name should be Route3.");
        assertEquals(2, route3.getStops().size(), "Route3 should have two stops.");
        assertEquals("Stop3", route3.getStops().get(0).getName(), "First stop of Route3 should be Stop3.");
        assertEquals("Stop4", route3.getStops().get(1).getName(), "Second stop of Route3 should be Stop4.");

        Route route4 = routes.get(3);
        assertEquals("Route4", route4.getName(), "Fourth route name should be Route4.");
        assertEquals(2, route4.getStops().size(), "Route4 should have two stops.");
        assertEquals("Stop4", route4.getStops().get(0).getName(), "First stop of Route4 should be Stop4.");
        assertEquals("Stop3", route4.getStops().get(1).getName(), "Second stop of Route4 should be Stop3.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNotNull(storage, "StorageFacility should not be null.");
        assertEquals(15, storage.getSmallBusesNum(), "Number of small buses should be 15.");
        assertEquals(7, storage.getLargeBusesNum(), "Number of large buses should be 7.");
        assertEquals(4, storage.getElectricTrainsNum(), "Number of electric trains should be 4.");
        assertEquals(3, storage.getDieselTrainsNum(), "Number of diesel trains should be 3.");
    }

    /**
     * Tests reading a configuration file without storage facility configurations.
     */
    @Test
    public void testReadConfigWithoutStorageFacilities() throws Exception {
        String configContent = ""
                + "LINE_START,BUS_LINE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "STOP,Stop2,44.973580,-93.235071,0.2\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.3\n"
                + "STOP,Stop1,44.972392,-93.243774,0.4\n"
                + "ROUTE_END\n"
                + "LINE_END\n";
        // No STORAGE_FACILITY_START

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(1, lines.size(), "There should be one line parsed.");
        Line line1 = lines.get(0);
        assertEquals("Line1", line1.getName(), "Line name should be Line1.");
        assertEquals(Line.BUS_LINE, line1.getType(), "Line type should be BUS_LINE.");

        // Verify routes
        List<Route> routes = configManager.getRoutes();
        assertEquals(2, routes.size(), "There should be two routes parsed.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNull(storage, "StorageFacility should be null when not configured.");
    }

    /**
     * Tests reading a configuration file with an invalid line type.
     */
    @Test
    public void testReadConfigWithInvalidLineType() throws Exception {
        String configContent = ""
                + "LINE_START,INVALID_LINE_TYPE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.2\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "STORAGE_FACILITY_START\n"
                + "SMALL_BUSES,10\n"
                + "LARGE_BUSES,5\n"
                + "ELECTRIC_TRAINS,3\n"
                + "DIESEL_TRAINS,2\n";

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(1, lines.size(), "There should be one line parsed.");
        Line line1 = lines.get(0);
        assertEquals("Line1", line1.getName(), "Line name should be Line1.");
        assertEquals("", line1.getType(), "Line type should be empty for invalid line type.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNotNull(storage, "StorageFacility should not be null.");
        assertEquals(10, storage.getSmallBusesNum(), "Number of small buses should be 10.");
        assertEquals(5, storage.getLargeBusesNum(), "Number of large buses should be 5.");
        assertEquals(3, storage.getElectricTrainsNum(), "Number of electric trains should be 3.");
        assertEquals(2, storage.getDieselTrainsNum(), "Number of diesel trains should be 2.");
    }

    /**
     * Tests reading a configuration file with duplicate stops in a route.
     */
    @Test
    public void testReadConfigWithDuplicateStopsInRoute() throws Exception {
        String configContent = ""
                + "LINE_START,BUS_LINE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.2\n" // Duplicate Stop1
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.3\n"
                + "STOP,Stop3,44.974000,-93.240000,0.4\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "STORAGE_FACILITY_START\n"
                + "SMALL_BUSES,10\n"
                + "LARGE_BUSES,5\n"
                + "ELECTRIC_TRAINS,3\n"
                + "DIESEL_TRAINS,2\n";

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(1, lines.size(), "There should be one line parsed.");
        Line line1 = lines.get(0);
        assertEquals("Line1", line1.getName(), "Line name should be Line1.");
        assertEquals(Line.BUS_LINE, line1.getType(), "Line type should be BUS_LINE.");

        // Verify routes
        List<Route> routes = configManager.getRoutes();
        assertEquals(2, routes.size(), "There should be two routes parsed.");

        Route route1 = routes.get(0);
        assertEquals("Route1", route1.getName(), "First route name should be Route1.");
        assertEquals(1, route1.getStops().size(), "Route1 should have one stop due to duplicate Stop1.");

        Route route2 = routes.get(1);
        assertEquals("Route2", route2.getName(), "Second route name should be Route2.");
        assertEquals(2, route2.getStops().size(), "Route2 should have two stops.");
        assertEquals("Stop2", route2.getStops().get(0).getName(), "First stop of Route2 should be Stop2.");
        assertEquals("Stop3", route2.getStops().get(1).getName(), "Second stop of Route2 should be Stop3.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNotNull(storage, "StorageFacility should not be null.");
        assertEquals(10, storage.getSmallBusesNum(), "Number of small buses should be 10.");
        assertEquals(5, storage.getLargeBusesNum(), "Number of large buses should be 5.");
        assertEquals(3, storage.getElectricTrainsNum(), "Number of electric trains should be 3.");
        assertEquals(2, storage.getDieselTrainsNum(), "Number of diesel trains should be 2.");
    }

    /**
     * Tests reading an empty configuration file.
     */
    @Test
    public void testReadEmptyConfigFile() throws Exception {
        String configContent = ""; // Empty content

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify lines
        List<Line> lines = configManager.getLines();
        assertEquals(0, lines.size(), "There should be no lines parsed.");

        // Verify storage facility
        StorageFacility storage = configManager.getStorageFacility();
        assertNull(storage, "StorageFacility should be null when not configured.");
    }


    /**
     * Tests reading a configuration file with multiple storage facility entries.
     */
    @Test
    public void testReadConfigWithMultipleStorageFacilityEntries() throws Exception {
        String configContent = ""
                + "LINE_START,BUS_LINE,Line1\n"
                + "ROUTE_START,Route1\n"
                + "STOP,Stop1,44.972392,-93.243774,0.1\n"
                + "STOP,Stop2,44.973580,-93.235071,0.2\n"
                + "ROUTE_END\n"
                + "ROUTE_START,Route2\n"
                + "STOP,Stop2,44.973580,-93.235071,0.3\n"
                + "STOP,Stop1,44.972392,-93.243774,0.4\n"
                + "ROUTE_END\n"
                + "LINE_END\n"
                + "STORAGE_FACILITY_START\n"
                + "SMALL_BUSES,10\n"
                + "LARGE_BUSES,5\n"
                + "ELECTRIC_TRAINS,3\n"
                + "DIESEL_TRAINS,2\n"
                + "SMALL_BUSES,20\n" // Duplicate entry for SMALL_BUSES
                + "LARGE_BUSES,10\n"; // Duplicate entry for LARGE_BUSES

        writeToConfigFile(configContent);

        // Read the configuration
        configManager.readConfig(counter, tempConfigFile.getAbsolutePath());

        // Verify storage facility: Assuming that later entries overwrite earlier ones
        StorageFacility storage = configManager.getStorageFacility();
        assertNotNull(storage, "StorageFacility should not be null.");
        assertEquals(20, storage.getSmallBusesNum(), "Number of small buses should be updated to 20.");
        assertEquals(10, storage.getLargeBusesNum(), "Number of large buses should be updated to 10.");
        assertEquals(3, storage.getElectricTrainsNum(), "Number of electric trains should remain 3.");
        assertEquals(2, storage.getDieselTrainsNum(), "Number of diesel trains should remain 2.");
    }

}

