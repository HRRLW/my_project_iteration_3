package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BusStrategyNightTest {

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
   * Test constructor normal.
   */
  @Test
  public void testConstructor() {
    BusStrategyNight busStrategyNight = new BusStrategyNight();
    assertEquals(0, busStrategyNight.getCounter());
  }

  /**
   * Testing to get correct vehicle according to the strategy.
   */
  @Test
  public void testGetTypeOfVehicle() {
    StorageFacility storageFacility = new StorageFacility(3, 1, 0, 0);
    BusStrategyNight busStrategyDay = new BusStrategyNight();
    String strToCmpr;
    for (int i = 0; i < 1; i++) {
      strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
      assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
      strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
      assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
      strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
      assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
      strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
      assertEquals(LargeBus.LARGE_BUS_VEHICLE, strToCmpr);
    }
  }

  /**
   * Testing when there is no vehicle.
   */
  @Test
  public void testGetTypeOfVehicleWithNoVehicle() {
    StorageFacility storageFacility = new StorageFacility(0, 0, 0, 0);
    BusStrategyNight busStrategyDay = new BusStrategyNight();
    String strToCmpr;
    strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
    assertNull(strToCmpr);
  }

  /**
   * Testing when there is no small bus.
   */
  @Test
  public void testGetTypeOfVehicleWithNoLargeBus() {
    StorageFacility storageFacility = new StorageFacility(3, 0, 0, 0);
    BusStrategyNight busStrategyDay = new BusStrategyNight();
    String strToCmpr;
    strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
    assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
    strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
    assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
    strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
    assertEquals(SmallBus.SMALL_BUS_VEHICLE, strToCmpr);
    strToCmpr = busStrategyDay.getTypeOfVehicle(storageFacility);
    assertNull(strToCmpr);
  }
}
