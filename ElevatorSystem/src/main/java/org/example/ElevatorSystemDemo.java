package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Elevator implements Runnable {
  private final int id;
  private final AtomicInteger currentFloor;
  private ElevatorState state;
  private volatile boolean isRunning = true;

  private final TreeSet<Integer> upRequests;
  private final TreeSet<Integer> downRequests;

  // Observer Pattern: List of observers
  private final List<ElevatorObserver> observers = new ArrayList<>();

  public Elevator(int id) {
    this.id = id;
    this.currentFloor = new AtomicInteger(1);
    this.upRequests = new TreeSet<>();
    this.downRequests = new TreeSet<>((a, b) -> Integer.compare(b, a));
    this.state = new IdleState();
  }

  // --- Observer Pattern Methods ---
  public void addObserver(ElevatorObserver observer) {
    observers.add(observer);
    observer.update(this); // Send initial state
  }

  public void notifyObservers() {
    for (ElevatorObserver observer : observers) {
      observer.update(this);
    }
  }

  // --- State Pattern Methods ---
  public void setState(ElevatorState state) {
    this.state = state;
    notifyObservers(); // Notify observers on direction change
  }

  public void move() {
    state.move(this);
  }

  // --- Request Handling ---
  public synchronized void addRequest(Request request) {
    System.out.println("Elevator " + id + " processing: " + request);
    state.addRequest(this, request);
  }

  // --- Getters and Setters ---
  public int getId() {
    return id;
  }

  public int getCurrentFloor() {
    return currentFloor.get();
  }

  public void setCurrentFloor(int floor) {
    this.currentFloor.set(floor);
    notifyObservers(); // Notify observers on floor change
  }

  public Direction getDirection() {
    return state.getDirection();
  }

  public TreeSet<Integer> getUpRequests() {
    return upRequests;
  }

  public TreeSet<Integer> getDownRequests() {
    return downRequests;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void stopElevator() {
    this.isRunning = false;
  }

  @Override
  public void run() {
    while (isRunning) {
      move();
      try {
        Thread.sleep(500); // Simulate movement time
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        isRunning = false;
      }
    }
  }
}

class Request {
  private final int targetFloor;
  private final Direction direction; // Primarily for External requests
  private final RequestSource source;

  public Request(int targetFloor, Direction direction, RequestSource source) {
    this.targetFloor = targetFloor;
    this.direction = direction;
    this.source = source;
  }

  public int getTargetFloor() {
    return targetFloor;
  }

  public Direction getDirection() {
    return direction;
  }

  public RequestSource getSource() {
    return source;
  }

  @Override
  public String toString() {
    return source
        + " Request to floor "
        + targetFloor
        + (source == RequestSource.EXTERNAL ? " going " + direction : "");
  }
}

enum Direction {
  UP,
  DOWN,
  IDLE
}

enum RequestSource {
  INTERNAL, // From inside the cabin
  EXTERNAL // From the hall/floor
}

class Display implements ElevatorObserver {
  @Override
  public void update(Elevator elevator) {
    System.out.println(
        "[DISPLAY] Elevator "
            + elevator.getId()
            + " | Current Floor: "
            + elevator.getCurrentFloor()
            + " | Direction: "
            + elevator.getDirection());
  }
}

interface ElevatorObserver {
  void update(Elevator elevator);
}

interface ElevatorState {
  void move(Elevator elevator);

  void addRequest(Elevator elevator, Request request);

  Direction getDirection();
}

class IdleState implements ElevatorState {
  @Override
  public void move(Elevator elevator) {
    if (!elevator.getUpRequests().isEmpty()) {
      elevator.setState(new MovingUpState());
    } else if (!elevator.getDownRequests().isEmpty()) {
      elevator.setState(new MovingDownState());
    }
    // Else stay idle
  }

  @Override
  public void addRequest(Elevator elevator, Request request) {
    if (request.getTargetFloor() > elevator.getCurrentFloor()) {
      elevator.getUpRequests().add(request.getTargetFloor());
    } else if (request.getTargetFloor() < elevator.getCurrentFloor()) {
      elevator.getDownRequests().add(request.getTargetFloor());
    } else {
      System.out.println(
          "Elevator " + elevator.getId() + " is already at floor " + request.getTargetFloor());
    }
    // If request is for current floor, doors would open (handled implicitly by moving to that
    // floor)
  }

  @Override
  public Direction getDirection() {
    return Direction.IDLE;
  }
}

class MovingDownState implements ElevatorState {
  @Override
  public void move(Elevator elevator) {
    if (elevator.getDownRequests().isEmpty()) {
      elevator.setState(new IdleState());
      return;
    }

    Integer nextFloor = elevator.getDownRequests().first();
    elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);

    if (elevator.getCurrentFloor() == nextFloor) {
      System.out.println("Elevator " + elevator.getId() + " stopped at floor " + nextFloor);
      elevator.getDownRequests().pollFirst();
    }

    if (elevator.getDownRequests().isEmpty()) {
      elevator.setState(new IdleState());
    }
  }

  @Override
  public void addRequest(Elevator elevator, Request request) {
    // Internal requests always get added to the appropriate queue
    if (request.getSource() == RequestSource.INTERNAL) {
      if (request.getTargetFloor() > elevator.getCurrentFloor()) {
        elevator.getUpRequests().add(request.getTargetFloor());
      } else if (request.getTargetFloor() < elevator.getCurrentFloor()) {
        elevator.getDownRequests().add(request.getTargetFloor());
      } else {
        System.out.println(
            "Elevator " + elevator.getId() + " is already at floor " + request.getTargetFloor());
      }
      return;
    }

    // External requests
    if (request.getDirection() == Direction.DOWN
        && request.getTargetFloor() <= elevator.getCurrentFloor()) {
      elevator.getDownRequests().add(request.getTargetFloor());
    } else if (request.getDirection() == Direction.UP) {
      elevator.getUpRequests().add(request.getTargetFloor());
    }
  }

  @Override
  public Direction getDirection() {
    return Direction.DOWN;
  }
}

class MovingUpState implements ElevatorState {
  @Override
  public void move(Elevator elevator) {
    if (elevator.getUpRequests().isEmpty()) {
      elevator.setState(new IdleState());
      return;
    }

    Integer nextFloor = elevator.getUpRequests().first();
    elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);

    if (elevator.getCurrentFloor() == nextFloor) {
      System.out.println("Elevator " + elevator.getId() + " stopped at floor " + nextFloor);
      elevator.getUpRequests().pollFirst();
    }

    if (elevator.getUpRequests().isEmpty()) {
      elevator.setState(new IdleState());
    }
  }

  @Override
  public void addRequest(Elevator elevator, Request request) {
    // Internal requests always get added to the appropriate queue
    if (request.getSource() == RequestSource.INTERNAL) {
      if (request.getTargetFloor() > elevator.getCurrentFloor()) {
        elevator.getUpRequests().add(request.getTargetFloor());
      } else if (request.getTargetFloor() < elevator.getCurrentFloor()) {
        elevator.getDownRequests().add(request.getTargetFloor());
      } else {
        System.out.println(
            "Elevator " + elevator.getId() + " is already at floor " + request.getTargetFloor());
      }
      return;
    }

    // External requests
    if (request.getDirection() == Direction.UP
        && request.getTargetFloor() >= elevator.getCurrentFloor()) {
      elevator.getUpRequests().add(request.getTargetFloor());
    } else if (request.getDirection() == Direction.DOWN) {
      elevator.getDownRequests().add(request.getTargetFloor());
    }
  }

  @Override
  public Direction getDirection() {
    return Direction.UP;
  }
}

interface ElevatorSelectionStrategy {
  Optional<Elevator> selectElevator(List<Elevator> elevators, Request request);
}

class NearestElevatorStrategy implements ElevatorSelectionStrategy {
  @Override
  public Optional<Elevator> selectElevator(List<Elevator> elevators, Request request) {
    Elevator bestElevator = null;
    int minDistance = Integer.MAX_VALUE;

    for (Elevator elevator : elevators) {
      if (isSuitable(elevator, request)) {
        int distance = Math.abs(elevator.getCurrentFloor() - request.getTargetFloor());
        if (distance < minDistance) {
          minDistance = distance;
          bestElevator = elevator;
        }
      }
    }
    return Optional.ofNullable(bestElevator);
  }

  private boolean isSuitable(Elevator elevator, Request request) {
    if (elevator.getDirection() == Direction.IDLE) {
      return true;
    }
    if (elevator.getDirection() == request.getDirection()) {
      if (request.getDirection() == Direction.UP
          && elevator.getCurrentFloor() <= request.getTargetFloor()) {
        return true;
      }
      if (request.getDirection() == Direction.DOWN
          && elevator.getCurrentFloor() >= request.getTargetFloor()) {
        return true;
      }
    }
    return false;
  }
}

class ElevatorSystem {
  private static ElevatorSystem instance;

  private final Map<Integer, Elevator> elevators;
  private final ElevatorSelectionStrategy selectionStrategy;
  private final ExecutorService executorService;

  private ElevatorSystem(int numElevators) {
    this.selectionStrategy = new NearestElevatorStrategy();
    this.executorService = Executors.newFixedThreadPool(numElevators);

    List<Elevator> elevatorList = new ArrayList<>();
    Display display = new Display(); // Create observer

    for (int i = 1; i <= numElevators; i++) {
      Elevator elevator = new Elevator(i);
      elevator.addObserver(display); // Attach observer
      elevatorList.add(elevator);
    }

    this.elevators = elevatorList.stream().collect(Collectors.toMap(Elevator::getId, e -> e));
  }

  public static synchronized ElevatorSystem getInstance(int numElevators) {
    if (instance == null) {
      instance = new ElevatorSystem(numElevators);
    }
    return instance;
  }

  public void start() {
    for (Elevator elevator : elevators.values()) {
      executorService.submit(elevator);
    }
  }

  // EXTERNAL Request (Hall Call)
  public void requestElevator(int floor, Direction direction) {
    System.out.println(
        "\n>> EXTERNAL Request: User at floor " + floor + " wants to go " + direction);
    Request request = new Request(floor, direction, RequestSource.EXTERNAL);

    // Use strategy to find best elevator
    Optional<Elevator> selectedElevator =
        selectionStrategy.selectElevator(new ArrayList<>(elevators.values()), request);

    if (selectedElevator.isPresent()) {
      selectedElevator.get().addRequest(request);
    } else {
      System.out.println("System busy, please wait.");
    }
  }

  // INTERNAL Request (Cabin Call)
  public void selectFloor(int elevatorId, int destinationFloor) {
    System.out.println(
        "\n>> INTERNAL Request: User in Elevator "
            + elevatorId
            + " selected floor "
            + destinationFloor);
    Request request = new Request(destinationFloor, Direction.IDLE, RequestSource.INTERNAL);

    Elevator elevator = elevators.get(elevatorId);
    if (elevator != null) {
      elevator.addRequest(request);
    } else {
      System.err.println("Invalid elevator ID.");
    }
  }

  public void shutdown() {
    System.out.println("Shutting down elevator system...");
    for (Elevator elevator : elevators.values()) {
      elevator.stopElevator();
    }
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}

public class ElevatorSystemDemo {
  public static void main(String[] args) throws InterruptedException {
    // Setup: building with 2 elevators
    int numElevators = 2;
    ElevatorSystem elevatorSystem = ElevatorSystem.getInstance(numElevators);

    // Start system
    elevatorSystem.start();
    System.out.println("Elevator system started. ConsoleDisplay is observing.\n");

    // 1. External Request: User at floor 5 wants to go UP.
    elevatorSystem.requestElevator(5, Direction.UP);
    Thread.sleep(100); // Wait for elevator to start moving

    // 2. Internal Request: Assume E1 took previous request.
    elevatorSystem.selectFloor(1, 10);
    Thread.sleep(200);

    // 3. External Request: User at floor 3 wants to go DOWN.
    elevatorSystem.requestElevator(3, Direction.DOWN);
    Thread.sleep(300);

    // 4. Internal Request: User in E2 presses 1.
    elevatorSystem.selectFloor(2, 1);

    // Let simulation run long enough to observe floor changes and stops
    System.out.println("\n--- Letting simulation run for 6 seconds ---");
    Thread.sleep(6000);

    // Shutdown system
    elevatorSystem.shutdown();
    System.out.println("\n--- SIMULATION END ---");
  }
}
