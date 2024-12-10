package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SmallBusColorDecorator class.
 */
public class SmallBusColorDecoratorTest {

    private SmallBus baseSmallBus;
    private SmallBusColorDecorator decorator;
    private Route testRouteIn;
    private Route testRouteOut;
    private Line testLine;

    /**
     * Setup operations before each test runs.
     */
    @BeforeEach
    public void setUp() {
        // Set deterministic behavior for passenger generation
        PassengerFactory.DETERMINISTIC = true;
        PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
        PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
        RandomPassengerGenerator.DETERMINISTIC = true;

        // Initialize stops
        List<Stop> stopsIn = new ArrayList<>();
        Stop stop1 = new Stop(0, "stop1", new Position(-93.243774, 44.972392));
        Stop stop2 = new Stop(1, "stop2", new Position(-93.235071, 44.973580));
        stopsIn.add(stop1);
        stopsIn.add(stop2);

        // Initialize inbound route
        List<Double> distancesIn = new ArrayList<>();
        distancesIn.add(1.2);
        List<Double> probabilitiesIn = new ArrayList<>();
        probabilitiesIn.add(0.15);
        probabilitiesIn.add(0.25);
        PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);
        testRouteIn = new Route(0, "inboundRoute", stopsIn, distancesIn, generatorIn);

        // Initialize outbound route
        List<Stop> stopsOut = new ArrayList<>();
        stopsOut.add(stop2);
        stopsOut.add(stop1);
        List<Double> distancesOut = new ArrayList<>();
        distancesOut.add(1.2);
        List<Double> probabilitiesOut = new ArrayList<>();
        probabilitiesOut.add(0.25);
        probabilitiesOut.add(0.15);
        PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);
        testRouteOut = new Route(1, "outboundRoute", stopsOut, distancesOut, generatorOut);

        // Initialize a Line with outbound and inbound routes
        testLine = new Line(10003, "testLineSmallBus", "BUS", testRouteOut, testRouteIn, new Issue());

        // Initialize the base SmallBus with a valid Line
        baseSmallBus = new SmallBus(4, testLine, 40, 30);

        // Create a decorator using the base SmallBus
        decorator = new SmallBusColorDecorator(baseSmallBus);
    }

    /**
     * Tests whether the constructor works properly when passing in a SmallBus type vehicle.
     */
    @Test
    public void testConstructorWithSmallBus() {
        assertNotNull(decorator, "Decorator should not be null when initialized with a SmallBus.");
        assertEquals(baseSmallBus.getId(), decorator.getId(), "IDs should match.");
        assertEquals(baseSmallBus.getLine(), decorator.getLine(), "Lines should match.");
        assertEquals(baseSmallBus.getCapacity(), decorator.getCapacity(), "Capacities should match.");
        assertEquals(baseSmallBus.getSpeed(), decorator.getSpeed(), "Speeds should match.");
    }

    /**
     * Tests whether the constructor throws an exception when passing in a non-SmallBus type vehicle.
     */
    @Test
    public void testConstructorWithNonSmallBus() {
        // Create a non-SmallBus type vehicle, for example, LargeBus
        Vehicle nonSmallBus = new LargeBus(5, testLine, 80, 50);

        // Assert that the constructor throws IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SmallBusColorDecorator(nonSmallBus);
        });

        String expectedMessage = "vehicle does not implement SmallBus";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate incorrect vehicle type.");
    }

    /**
     * Tests whether the getColor() method returns the correct initial color.
     */
    @Test
    public void testGetColorInitial() {
        int[] expectedColor = {0, 255, 0, 255}; // Example color: Green with full opacity
        assertArrayEquals(expectedColor, decorator.getColor(), "Initial color should match the expected RGBA values.");
    }

    /**
     * Tests whether the setColor(int[] rgba) method correctly updates the color.
     */
    @Test
    public void testSetColor() {
        int[] newColor = {255, 255, 0, 128}; // Example color: Yellow with 50% opacity
        decorator.setColor(newColor);
        assertArrayEquals(newColor, decorator.getColor(), "Color should be updated to the new RGBA values.");
    }

    /**
     * Tests whether the getColor() method returns the updated color after setColor(int[] rgba) is called.
     */
    @Test
    public void testSetColorAndGetColor() {
        int[] newColor = {128, 0, 128, 0}; // Example color: Purple with 0% opacity
        decorator.setColor(newColor);
        assertArrayEquals(newColor, decorator.getColor(), "getColor should return the updated color after setColor is called.");
    }

    /**
     * Tests setting multiple colors sequentially.
     */
    @Test
    public void testMultipleSetColor() {
        int[] firstColor = {20, 40, 60, 80};
        int[] secondColor = {80, 60, 40, 20};
        decorator.setColor(firstColor);
        assertArrayEquals(firstColor, decorator.getColor(), "First set color should be applied correctly.");
        decorator.setColor(secondColor);
        assertArrayEquals(secondColor, decorator.getColor(), "Second set color should override the first set color.");
    }

    /**
     * Tests whether the report method correctly delegates to the decorated SmallBus object.
     */
    @Test
    public void testReport() {
        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // Call the report method
        decorator.report(printStream);

        // Generate the expected report content
        String expectedReport = "====SmallBus Report Start====" + System.lineSeparator()
                + "ID: 4" + System.lineSeparator()
                + "Line: testLineSmallBus" + System.lineSeparator()
                + "Capacity: 40" + System.lineSeparator()
                + "Speed: 30" + System.lineSeparator()
                + "Current CO2 Emission: 0" + System.lineSeparator()
                + "====SmallBus Report End====" + System.lineSeparator();

        // Get the actual output
        String actualReport = outputStream.toString();

        // Assert that the output matches the expected report
        assertEquals(expectedReport, actualReport, "Report should be delegated to the base SmallBus and match the expected output.");

        // Close the streams
        printStream.close();
    }

    /**
     * Tests whether the report method correctly reflects the current state of the decorated object when it hasn't been modified.
     */
    @Test
    public void testReportWithoutModification() {
        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // Call the report method
        decorator.report(printStream);

        // Generate the expected report content
        String expectedReport = "====SmallBus Report Start====" + System.lineSeparator()
                + "ID: 4" + System.lineSeparator()
                + "Line: testLineSmallBus" + System.lineSeparator()
                + "Capacity: 40" + System.lineSeparator()
                + "Speed: 30" + System.lineSeparator()
                + "Current CO2 Emission: 0" + System.lineSeparator()
                + "====SmallBus Report End====" + System.lineSeparator();

        // Get the actual output
        String actualReport = outputStream.toString();

        // Assert that the output matches the expected report
        assertEquals(expectedReport, actualReport, "Report should reflect the current state of the base SmallBus.");

        // Close the streams
        printStream.close();
    }
}
