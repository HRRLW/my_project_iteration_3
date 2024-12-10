package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ElectricTrainColorDecorator class.
 */
public class ElectricTrainColorDecoratorTest {

    private ElectricTrain baseElectricTrain;
    private ElectricTrainColorDecorator decorator;
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
        Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
        Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
        stopsIn.add(stop1);
        stopsIn.add(stop2);

        // Initialize inbound route
        List<Double> distancesIn = new ArrayList<>();
        distancesIn.add(0.843774422231134);
        List<Double> probabilitiesIn = new ArrayList<>();
        probabilitiesIn.add(0.025);
        probabilitiesIn.add(0.3);
        PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);
        testRouteIn = new Route(0, "testRouteIn", stopsIn, distancesIn, generatorIn);

        // Initialize outbound route
        List<Stop> stopsOut = new ArrayList<>();
        stopsOut.add(stop2);
        stopsOut.add(stop1);
        List<Double> distancesOut = new ArrayList<>();
        distancesOut.add(0.843774422231134);
        List<Double> probabilitiesOut = new ArrayList<>();
        probabilitiesOut.add(0.3);
        probabilitiesOut.add(0.025);
        PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);
        testRouteOut = new Route(1, "testRouteOut", stopsOut, distancesOut, generatorOut);

        // Initialize a Line with outbound and inbound routes
        testLine = new Line(10001, "testLineElectric", "TRAIN", testRouteOut, testRouteIn, new Issue());

        // Initialize the base ElectricTrain with a valid Line
        baseElectricTrain = new ElectricTrain(2, testLine, 60, 90);

        // Create a decorator using the base ElectricTrain
        decorator = new ElectricTrainColorDecorator(baseElectricTrain);
    }

    /**
     * Tests whether the constructor works properly when passing in an ElectricTrain type vehicle.
     */
    @Test
    public void testConstructorWithElectricTrain() {
        assertNotNull(decorator, "Decorator should not be null when initialized with an ElectricTrain.");
        assertEquals(baseElectricTrain.getId(), decorator.getId(), "IDs should match.");
        assertEquals(baseElectricTrain.getLine(), decorator.getLine(), "Lines should match.");
        assertEquals(baseElectricTrain.getCapacity(), decorator.getCapacity(), "Capacities should match.");
        assertEquals(baseElectricTrain.getSpeed(), decorator.getSpeed(), "Speeds should match.");
    }

    /**
     * Tests whether the constructor throws an exception when passing in a non-ElectricTrain type vehicle.
     */
    @Test
    public void testConstructorWithNonElectricTrain() {
        // Create a non-ElectricTrain type vehicle, for example, DieselTrain
        Vehicle nonElectricTrain = new DieselTrain(3, testLine, 70, 100);

        // Assert that the constructor throws IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ElectricTrainColorDecorator(nonElectricTrain);
        });

        String expectedMessage = "vehicle does not implement ElectricTrain";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate incorrect vehicle type.");
    }

    /**
     * Tests whether the getColor() method returns the correct initial color.
     */
    @Test
    public void testGetColorInitial() {
        int[] expectedColor = {0, 128, 255, 255}; // Example color: Blue with full opacity
        assertArrayEquals(expectedColor, decorator.getColor(), "Initial color should match the expected RGBA values.");
    }

    /**
     * Tests whether the setColor(int[] rgba) method correctly updates the color.
     */
    @Test
    public void testSetColor() {
        int[] newColor = {255, 0, 0, 128}; // Example color: Red with 50% opacity
        decorator.setColor(newColor);
        assertArrayEquals(newColor, decorator.getColor(), "Color should be updated to the new RGBA values.");
    }

    /**
     * Tests whether the getColor() method returns the updated color after setColor(int[] rgba) is called.
     */
    @Test
    public void testSetColorAndGetColor() {
        int[] newColor = {0, 255, 0, 0}; // Example color: Green with 0% opacity
        decorator.setColor(newColor);
        assertArrayEquals(newColor, decorator.getColor(), "getColor should return the updated color after setColor is called.");
    }

    /**
     * Tests setting multiple colors sequentially.
     */
    @Test
    public void testMultipleSetColor() {
        int[] firstColor = {10, 20, 30, 40};
        int[] secondColor = {50, 60, 70, 80};
        decorator.setColor(firstColor);
        assertArrayEquals(firstColor, decorator.getColor(), "First set color should be applied correctly.");
        decorator.setColor(secondColor);
        assertArrayEquals(secondColor, decorator.getColor(), "Second set color should override the first set color.");
    }

    /**
     * Tests whether the report method correctly delegates to the decorated ElectricTrain object.
     */
    @Test
    public void testReport() {
        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // Call the report method
        decorator.report(printStream);

        // Generate the expected report content
        String expectedReport = "====ElectricTrain Report Start====" + System.lineSeparator()
                + "ID: 2" + System.lineSeparator()
                + "Line: testLineElectric" + System.lineSeparator()
                + "Capacity: 60" + System.lineSeparator()
                + "Speed: 90" + System.lineSeparator()
                + "Current CO2 Emission: 0" + System.lineSeparator()
                + "====ElectricTrain Report End====" + System.lineSeparator();

        // Get the actual output
        String actualReport = outputStream.toString();

        // Assert that the output matches the expected report
        assertEquals(expectedReport, actualReport, "Report should be delegated to the base ElectricTrain and match the expected output.");

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
        String expectedReport = "====ElectricTrain Report Start====" + System.lineSeparator()
                + "ID: 2" + System.lineSeparator()
                + "Line: testLineElectric" + System.lineSeparator()
                + "Capacity: 60" + System.lineSeparator()
                + "Speed: 90" + System.lineSeparator()
                + "Current CO2 Emission: 0" + System.lineSeparator()
                + "====ElectricTrain Report End====" + System.lineSeparator();

        // Get the actual output
        String actualReport = outputStream.toString();

        // Assert that the output matches the expected report
        assertEquals(expectedReport, actualReport, "Report should reflect the current state of the base ElectricTrain.");

        // Close the streams
        printStream.close();
    }
}

