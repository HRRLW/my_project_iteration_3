package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassengerFactoryTest {

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
   * Tests generate function.
   */
  @Test
  public void testGenerate() {
    assertEquals(3, PassengerFactory.generate(1, 10).getDestination());

  }

  /**
   * Tests generate function.
   */
  @Test
  public void nameGeneration() {
    assertEquals("Goldy", PassengerFactory.nameGeneration());

  }

  /**
   * Tests generate function in non-deterministic mode.
   */
  @Test
  public void testGenerateNonDeterministic() {
    PassengerFactory.DETERMINISTIC = false;
    Passenger passenger = PassengerFactory.generate(1, 10);
    int destination = passenger.getDestination();
    assertEquals(true, destination > 1 && destination <= 10);
  }

  /**
   * Tests nameGeneration function in non-deterministic mode.
   */
  @Test
  public void testNameGenerationNonDeterministic() {
    PassengerFactory.DETERMINISTIC = false;
    String name = PassengerFactory.nameGeneration();
    assertFalse(name.isEmpty());
  }


}
