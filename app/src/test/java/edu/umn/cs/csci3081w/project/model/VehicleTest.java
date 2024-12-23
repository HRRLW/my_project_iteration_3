package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class VehicleTest {

  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    //Vehicle.TESTING = true;
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testVehicle.getId());
    assertEquals("testRouteOut1", testVehicle.getName());
    assertEquals(3, testVehicle.getCapacity());
    assertEquals(1, testVehicle.getSpeed());
    assertEquals(testRouteOut, testVehicle.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testVehicle.getLine().getInboundRoute());
  }

  /**
   * Tests if testIsTripComplete function works properly.
   */
  @Test
  public void testIsTripComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    assertEquals(true, testVehicle.isTripComplete());

  }


  /**
   * Tests if loadPassenger function works properly.
   */
  @Test
  public void testLoadPassenger() {

    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    assertEquals(1, testVehicle.loadPassenger(testPassenger1));
    assertEquals(1, testVehicle.loadPassenger(testPassenger2));
    assertEquals(1, testVehicle.loadPassenger(testPassenger3));
    assertEquals(0, testVehicle.loadPassenger(testPassenger4));
  }


  /**
   * Tests if move function works properly.
   */
  @Test
  public void testMove() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.move();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Tests move when (!isTripComplete() && distanceRemaining <= 0) is false.
   */
  @Test
  public void testMoveWhenDistanceRemainingIsPositive() {
    List<Stop> stops = new ArrayList<>();
    stops.add(new Stop(0, "Stop 1", new Position(-93.235071, 44.973580)));
    stops.add(new Stop(1, "Stop 2", new Position(-93.243774, 44.972392)));
    List<Double> distances = new ArrayList<>();
    distances.add(5.0);
    List<Double> probabilities = new ArrayList<>();
    probabilities.add(0.5);
    probabilities.add(0.5);

    PassengerGenerator generator = new RandomPassengerGenerator(stops, probabilities);
    Route testRoute = new Route(0, "Test Route", stops, distances, generator);

    Line testLine = new Line(10000, "Test Line", "VEHICLE_LINE", testRoute, testRoute, new Issue());

    testVehicle = new VehicleTestImpl(1, testLine, 3, 2.0,
        new PassengerLoader(), new PassengerUnloader());
    testVehicle.move();
    assertTrue(testVehicle.getDistanceRemaining() > 0);
    assertFalse(testVehicle.isTripComplete());

    testVehicle.move();
    assertTrue(testVehicle.getDistanceRemaining() > 0);
    assertEquals("Stop 2", testVehicle.getNextStop().getName());
  }

  /**
   * Tests move when passengersHandled >= 0 is false.
   */
  @Test
  public void testMoveWhenHandleStopReturnsNegative() {
    Stop mockStop = Mockito.mock(Stop.class);
    Position mockPosition = new Position(-93.243774, 44.972392);
    Mockito.when(mockStop.getPosition()).thenReturn(mockPosition);
    Mockito.when(mockStop.loadPassengers(Mockito.any())).thenReturn(-1);
    List<Stop> mockStops = new ArrayList<>();
    mockStops.add(mockStop);
    List<Double> mockDistances = new ArrayList<>();
    mockDistances.add(0.843774422231134);
    PassengerGenerator mockGenerator = Mockito.mock(PassengerGenerator.class);

    Route mockRouteOut = new Route(0, "mockRouteOut", mockStops, mockDistances, mockGenerator);
    Route mockRouteIn = new Route(1, "mockRouteIn", mockStops, mockDistances, mockGenerator);

    Line mockLine = new Line(10001, "mockLine", "VEHICLE_LINE",
        mockRouteOut, mockRouteIn, new Issue());
    testVehicle = new VehicleTestImpl(2, mockLine, 3, 1.0,
        new PassengerLoader(), new PassengerUnloader());
    testVehicle.move();
    assertEquals(mockStop, testVehicle.getNextStop());
  }

  /**
   * Tests if update function works properly.
   */
  @Test
  public void testUpdate() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.update();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Test update when the vehicle has passengers.
   */
  @Test
  public void testUpdateWithPassengers() {
    Passenger testPassenger1 = new Passenger(1, "Test Passenger 1");
    Passenger testPassenger2 = new Passenger(2, "Test Passenger 2");

    testVehicle.loadPassenger(testPassenger1);
    testVehicle.loadPassenger(testPassenger2);

    assertEquals(2, testVehicle.getPassengers().size());
    testVehicle.update();
    assertEquals(1, testVehicle.getPassengers().size());
    for (Passenger passenger : testVehicle.getPassengers()) {
      assertNotNull(passenger);
      passenger.pasUpdate();
    }
  }

  /**
   * Test update when the line has an active issue.
   */
  @Test
  public void testUpdateWhenLineHasActiveIssue() {
    // Create a Line and trigger an issue
    Issue issue = new Issue();
    Line lineWithIssue = new Line(10000, "Line With Issue",
        "VEHICLE_LINE", testRouteOut, testRouteIn, issue);
    issue.createIssue();

    Vehicle vehicleWithIssue = new VehicleTestImpl(1, lineWithIssue, 3, 1.0,
        new PassengerLoader(), new PassengerUnloader());
    vehicleWithIssue.update();
    assertEquals("test stop 2", vehicleWithIssue.getNextStop().getName());
    assertEquals(1, vehicleWithIssue.getNextStop().getId());
    assertEquals(0, vehicleWithIssue.getDistanceRemaining());

    for (int i = 0; i < 10; i++) {
      issue.decrementCounter();
    }
    assertTrue(issue.isIssueResolved());
    vehicleWithIssue.update();
    assertEquals("test stop 1", vehicleWithIssue.getNextStop().getName());
    assertEquals(0, vehicleWithIssue.getNextStop().getId());
  }


  /**
   * Tests updateDistance when if (isTripComplete()) is true.
   */
  @Test
  public void testUpdateDistanceWhenTripIsComplete() {
    testVehicle = new VehicleTestImpl(1, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()),
        3, 1.0, new PassengerLoader(), new PassengerUnloader());
    while (!testVehicle.isTripComplete()) {
      testVehicle.move();
    }
    assertTrue(testVehicle.isTripComplete());
    testVehicle.move();
    assertNull(testVehicle.getNextStop());
    assertEquals(999.0, testVehicle.getDistanceRemaining());
  }

  /**
   * Test move when speed is negative.
   */
  @Test
  public void testMoveWithNegativeSpeed() {
    Vehicle vehicleWithNegativeSpeed = new VehicleTestImpl(1, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()),
        3, -1.0, new PassengerLoader(), new PassengerUnloader());
    vehicleWithNegativeSpeed.move();

    assertEquals(0.843774422231134, vehicleWithNegativeSpeed.getDistanceRemaining());
    assertEquals("test stop 1", vehicleWithNegativeSpeed.getNextStop().getName());
    assertEquals(0, vehicleWithNegativeSpeed.getNextStop().getId());
  }


  /**
   * Test to see if observer got attached.
   */
  @Test
  public void testProvideInfo() {
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    testVehicle.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(testVehicle);
    testVehicle.update();

    vehicleSubject.notifyObservers();
    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    String command = capturedData.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedData.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test provideInfo when the trip is complete.
   */
  @Test
  public void testProvideInfoWhenTripIsComplete() {
    while (!testVehicle.isTripComplete()) {
      testVehicle.move();
    }
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    testVehicle.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(testVehicle);

    boolean tripCompleted = testVehicle.provideInfo();
    assertTrue(tripCompleted);

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();

    assertEquals("observedVehicle", capturedData.get("command").getAsString());
    assertEquals("", capturedData.get("text").getAsString());
  }

  /**
   * Test provideInfo for small buses.
   */
  @Test
  public void testProvideInfoForSmallBus() {
    SmallBus smallBus = new SmallBus(1, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 10, 1.0);
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    smallBus.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(smallBus);
    smallBus.provideInfo();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    assertTrue(capturedData.get("text").getAsString().contains("SMALL_BUS_VEHICLE"));
  }

  /**
   * Test provideInfo for large buses.
   */
  @Test
  public void testProvideInfoForLargeBus() {
    LargeBus largeBus = new LargeBus(2, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 30, 1.0);
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    largeBus.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(largeBus);
    largeBus.provideInfo();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    assertTrue(capturedData.get("text").getAsString().contains("LARGE_BUS_VEHICLE"));
  }

  /**
   * Test provideInfo for electric train.
   */
  @Test
  public void testProvideInfoForElectricTrain() {
    ElectricTrain electricTrain = new ElectricTrain(3, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 50, 2.0);
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    electricTrain.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(electricTrain);
    electricTrain.provideInfo();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    assertTrue(capturedData.get("text").getAsString().contains("ELECTRIC_TRAIN_VEHICLE"));
  }

  /**
   * Test provideInfo for diesel train.
   */
  @Test
  public void testProvideInfoForDieselTrain() {
    DieselTrain dieselTrain = new DieselTrain(4, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 50, 2.0);
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    dieselTrain.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(dieselTrain);
    dieselTrain.provideInfo();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    assertTrue(capturedData.get("text").getAsString().contains("DIESEL_TRAIN_VEHICLE"));
  }

  /**
   * Test provideInfo when there are multiple carbon emission history entries.
   */
  @Test
  public void testProvideInfoWithMultipleCarbonEmissionHistoryEntries() {
    SmallBus smallBus = new SmallBus(1, new Line(10000, "Test Line",
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 10, 1.0);
    WebServerSession mockSession = Mockito.mock(WebServerSession.class);
    VehicleConcreteSubject vehicleSubject = new VehicleConcreteSubject(mockSession);
    smallBus.setVehicleSubject(vehicleSubject);
    vehicleSubject.attachObserver(smallBus);
    smallBus.update();
    smallBus.update();
    smallBus.update();
    smallBus.provideInfo();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    Mockito.verify(mockSession).sendJson(captor.capture());
    JsonObject capturedData = captor.getValue();
    String text = capturedData.get("text").getAsString();
    assertTrue(text.contains("CO2: "));
    assertTrue(text.split(", ").length >= 3);
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testVehicle = null;
  }

}
