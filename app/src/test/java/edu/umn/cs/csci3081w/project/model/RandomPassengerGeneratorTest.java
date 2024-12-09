package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomPassengerGeneratorTest {

  private RandomPassengerGenerator testRandomPassengerGenerator;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    List<Stop> stops = new ArrayList<Stop>();
    Stop testStop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop testStop2 = new Stop(1, "test stop 2", new Position(-93.25631, 44.963211));

    stops.add(testStop1);
    stops.add(testStop2);

    List<Double> probabilities = new ArrayList<Double>();
    probabilities.add(.15);

    testRandomPassengerGenerator = new RandomPassengerGenerator(stops, probabilities);

  }

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {

    assertEquals(0, testRandomPassengerGenerator.getStops()
        .get(0).getId());
    assertEquals("test stop 1", testRandomPassengerGenerator.getStops()
        .get(0).getName());
    assertEquals(-93.243774, testRandomPassengerGenerator.getStops()
        .get(0).getPosition().getLongitude());
    assertEquals(44.972392, testRandomPassengerGenerator.getStops()
        .get(0).getPosition().getLatitude());
    assertEquals(.15, testRandomPassengerGenerator.getProbabilities()
        .get(0));

  }

  /**
   * Test generatePassengers function.
   */
  @Test
  public void testGeneratePassengers() {

    testRandomPassengerGenerator.DETERMINISTIC = true;
    assertEquals(1, testRandomPassengerGenerator.generatePassengers());

  }

  /**
   * Test generatePassengers when while (probCount < probSize && stopCount < stopSize) is false.
   */
  @Test
  public void testGeneratePassengersWhileConditionFalse() {
    List<Stop> stops = new ArrayList<>();
    stops.add(new Stop(0, "Stop 0", new Position(-93.0, 44.0)));
    List<Double> probabilities = new ArrayList<>();
    probabilities.add(0.15);
    probabilities.add(0.25);
    testRandomPassengerGenerator = new RandomPassengerGenerator(stops, probabilities);
    int passengersGenerated = testRandomPassengerGenerator.generatePassengers();
    assertEquals(0, passengersGenerated);
  }


  /**
   * Test generatePassengers when if (RandomPassengerGenerator.DETERMINISTIC) is false.
   */
  @Test
  public void testGeneratePassengersNonDeterministic() {
    RandomPassengerGenerator.DETERMINISTIC = false;
    List<Double> probabilities = new ArrayList<>();
    probabilities.add(0.9);
    testRandomPassengerGenerator = new RandomPassengerGenerator(
        testRandomPassengerGenerator.getStops(), probabilities);
    int passengersGenerated = testRandomPassengerGenerator.generatePassengers();
    int firstStopPassengers = testRandomPassengerGenerator.getStops().get(0).getPassengers().size();
    assertEquals(passengersGenerated, firstStopPassengers);
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testRandomPassengerGenerator = null;
  }


}
