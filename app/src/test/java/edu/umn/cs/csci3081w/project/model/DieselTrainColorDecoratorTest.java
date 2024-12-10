package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for DieselTrainColorDecorator class.
 */
public class DieselTrainColorDecoratorTest {

    private DieselTrain baseDieselTrain;
    private DieselTrainColorDecorator decorator;
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
        testLine = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue());

        // Initialize the base DieselTrain with a valid Line
        baseDieselTrain = new DieselTrain(1, testLine, 50, 80);

        // Create a decorator using the base DieselTrain
        decorator = new DieselTrainColorDecorator(baseDieselTrain);
    }

    /**
     * Tests whether the constructor works properly when passing in a DieselTrain type vehicle.
     */
    @Test
    public void testConstructorWithDieselTrain() {
        assertNotNull(decorator, "Decorator should not be null when initialized with a DieselTrain.");
        assertEquals(baseDieselTrain.getId(), decorator.getId(), "IDs should match.");
        assertEquals(baseDieselTrain.getLine(), decorator.getLine(), "Lines should match.");
        assertEquals(baseDieselTrain.getCapacity(), decorator.getCapacity(), "Capacities should match.");
        assertEquals(baseDieselTrain.getSpeed(), decorator.getSpeed(), "Speeds should match.");
    }

    /**
     * Tests whether the constructor throws an exception when passing in a non-DieselTrain type vehicle.
     */
    @Test
    public void testConstructorWithNonDieselTrain() {
        // Create a non-DieselTrain type vehicle, for example, ElectricTrain
        Vehicle nonDieselTrain = new ElectricTrain(2, testLine, 60, 90);

        // Assert that the constructor throws IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DieselTrainColorDecorator(nonDieselTrain);
        });

        String expectedMessage = "vehicle does not implement Bus";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate incorrect vehicle type.");
    }

    /**
     * Tests whether the getColor() method returns the correct initial color.
     */
    @Test
    public void testGetColorInitial() {
        int[] expectedColor = {255, 204, 51, 255};
        assertArrayEquals(expectedColor, decorator.getColor(), "Initial color should match the expected RGBA values.");
    }

    /**
     * Tests whether the setColor(int[] rgba) method correctly updates the color.
     */
    @Test
    public void testSetColor() {
        int[] newColor = {100, 150, 200, 128};
        decorator.setColor(newColor);
        assertArrayEquals(newColor, decorator.getColor(), "Color should be updated to the new RGBA values.");
    }

    /**
     * Tests whether the getColor() method returns the updated color after setColor(int[] rgba) is called.
     */
    @Test
    public void testSetColorAndGetColor() {
        int[] newColor = {0, 0, 0, 0};
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
}
