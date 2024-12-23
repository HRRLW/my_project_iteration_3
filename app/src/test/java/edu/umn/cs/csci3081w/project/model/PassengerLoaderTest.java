package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassengerLoaderTest {

  private PassengerLoader testPassengerLoader;

  /**
   * Setup deterministic operations before each test run.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
  }

  /**
   * Tests loadPassenger function.
   */
  @Test
  public void testLoadPassenger() {

    testPassengerLoader = new PassengerLoader();

    Passenger testPassenger = new Passenger(1, "testPassenger");

    List<Passenger> passengerList = new ArrayList<Passenger>();

    assertEquals(1, testPassengerLoader.loadPassenger(testPassenger, 1, passengerList));
    assertEquals(1, passengerList.size());
    assertEquals(true, testPassenger.isOnVehicle());

    assertEquals(0, testPassengerLoader.loadPassenger(testPassenger, 1, passengerList));

  }

}
