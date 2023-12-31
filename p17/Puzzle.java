package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
  public static enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0),
    SOUTH(0, 1),
    STOPPED(0, 0);
    
    public int dx;
    public int dy;
    
    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
    
    public Direction reverse() {
      switch (this) {
        case NORTH:
          return SOUTH;
        case SOUTH:
          return NORTH;
        case EAST:
          return WEST;
        case WEST:
        default:
          return EAST;
      }
    }
  };
  
  public record Position(int x, int y) {
    
  }
  
  public record DirectionTuple(Direction d1, Direction d2, Direction d3) {}
  
  public static class Path implements Comparable<Path> {
    LinkedList<Direction> lastDirections = new LinkedList<>(List.of(Direction.STOPPED, Direction.STOPPED, Direction.STOPPED));
    List<Position> positionsVisited = new ArrayList<>();
    int heatLoss = 0;
    Position currentPosition;
    Position targetPosition;
    
    public Path(Position currentPosition, Position targetPosition) {
      this.currentPosition = currentPosition;
      this.targetPosition = targetPosition;
      positionsVisited.add(currentPosition);
    }
    
    public Path copy() {
      Path path = new Path(currentPosition, targetPosition);
      path.lastDirections = new LinkedList<>();
      path.lastDirections.addAll(this.lastDirections);
      path.positionsVisited = new ArrayList<>(this.positionsVisited);
      path.heatLoss = this.heatLoss;
      return path;
    }
    
    public boolean isDone() {
      return currentPosition.equals(targetPosition);
    }
    
    public boolean move(Integer[][] losses, Direction direction) {
      lastDirections.addFirst(direction);
      if (lastDirections.size() > 3) {
        lastDirections.removeLast();
      }
      currentPosition = new Position(currentPosition.x + direction.dx, currentPosition.y + direction.dy);
      if (currentPosition.x < 0 || currentPosition.x >= losses[0].length) return false;
      if (currentPosition.y < 0 || currentPosition.y >= losses.length) return false;
      if (positionsVisited.contains(currentPosition)) return false;
      
      positionsVisited.add(currentPosition);
      heatLoss += losses[currentPosition.y][currentPosition.x];
      return true;
    }
    
    @Override
    public int compareTo(Path p) {
      return this.heatLoss - p.heatLoss;
    }
    
    public int approxDistance(Path p) {
      int thisDistance = currentPosition.x + currentPosition.y;
      int pDistance = p.currentPosition.x + p.currentPosition.y;
      return thisDistance - pDistance;
    }
    
    public String toString() {
      return "Path(heatloss=" + heatLoss + ", x=" + currentPosition.x + ", y=" + currentPosition.y + ", positionsVisited=" + positionsVisited.size() + ")";
    }
  }
  
  public static boolean isDirectionValid(LinkedList<Direction> lastDirections, Direction nextDirection) {
    if (lastDirections.isEmpty()) {
      return true;
    }
    
    boolean goingBackwards = lastDirections.getFirst().reverse() == nextDirection;
    if (lastDirections.size() < 3 && !goingBackwards) return true;
    
    boolean allSame = true;
    Direction lastDirection = lastDirections.getFirst();
    for (Direction direction : lastDirections) {
      if (direction != lastDirection) {
        allSame = false;
      }
    }
    
    return (nextDirection != lastDirection || !allSame) && !goingBackwards;
  }
  
  public static long calculate(Integer[][] losses, Map<DirectionTuple, Integer[][]> bestLosses, Path startingPath, Integer[] minX, Integer[] minY) {
    //PriorityQueue<Path> completedPaths = new PriorityQueue<>();
    PriorityQueue<Path> paths = new PriorityQueue<>((p1, p2) -> -p1.approxDistance(p2));
    //PriorityQueue<Path> paths = new PriorityQueue<>();
    paths.add(startingPath);
    
    // Seed a path;
    Path seedPath = startingPath.copy();
    for (int i = 1; i < losses.length; i++) {
      {
        seedPath.move(losses, Direction.EAST);
        DirectionTuple dt = new DirectionTuple(seedPath.lastDirections.get(0), seedPath.lastDirections.get(1), seedPath.lastDirections.get(2));
        bestLosses.get(dt)[seedPath.currentPosition.y][seedPath.currentPosition.x] = seedPath.heatLoss;
      }
      {
        DirectionTuple dt = new DirectionTuple(seedPath.lastDirections.get(0), seedPath.lastDirections.get(1), seedPath.lastDirections.get(2));
        seedPath.move(losses, Direction.SOUTH);
        bestLosses.get(dt)[seedPath.currentPosition.y][seedPath.currentPosition.x] = seedPath.heatLoss;
      }
    }
    if (!seedPath.isDone()) {
      throw new RuntimeException("Bad seed path.");
    }
    Path bestCompletedPath = seedPath;
    //completedPaths.add(seedPath);
    System.out.println("Seeded heatLoss = " + seedPath.heatLoss);
    
    int iter = 0;
    while (!paths.isEmpty()) {
      iter++;
      Path path = paths.poll();
      int bestHeatLossSoFar = bestCompletedPath.heatLoss;
      if (iter % 1000000 == 0)
        //System.out.println("paths=" + paths.size() + " completed=" + completedPaths.size() + " best=" + bestHeatLossSoFar);
        System.out.println("paths=" + paths.size() + " best=" + bestHeatLossSoFar);
      if (path.heatLoss > bestHeatLossSoFar) {
        continue;
      }
      // Optimization.
      if (path.heatLoss + minX[path.currentPosition.x] + minY[path.currentPosition.y] > bestHeatLossSoFar) {
        continue;
      }

      if (path.isDone()) {
        if (path.heatLoss < bestCompletedPath.heatLoss) {
          bestCompletedPath = path;
        }
        //completedPaths.add(path);
        continue;
      }

      for (Direction direction : Direction.values()) {
        if (isDirectionValid(path.lastDirections, direction)) {
          Path newPath = path.copy();
          if (newPath.move(losses, direction)) {
            DirectionTuple dt = new DirectionTuple(newPath.lastDirections.get(0), newPath.lastDirections.get(1), newPath.lastDirections.get(2));
            if (newPath.heatLoss < bestLosses.get(dt)[newPath.currentPosition.y][newPath.currentPosition.x]) {
              bestLosses.get(dt)[newPath.currentPosition.y][newPath.currentPosition.x] = newPath.heatLoss;
              paths.add(newPath);
            }
            //if (iter % 100000 == 0)
            //  System.out.println(newPath);
          }
        }
      }
    }
    
    return bestCompletedPath.heatLoss;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p17/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Integer[][] losses = new Integer[lines.size()][lines.get(0).length()];
    Map<DirectionTuple, Integer[][]> bestLosses = new HashMap<>();
    Integer[] minX = new Integer[lines.get(0).length()];
    Integer[] minY = new Integer[lines.size()];
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        losses[i][j] = Integer.parseInt(String.valueOf(line.charAt(j)));
      }
    }
    for (Direction d1 : Direction.values()) {
      if (d1 == Direction.STOPPED) continue;
      for (Direction d2 : Direction.values()) {
        if (d1 == Direction.STOPPED && d2 == Direction.STOPPED) continue;
        for (Direction d3 : Direction.values()) {
          Integer[][] bestLossesArray = new Integer[lines.size()][lines.get(0).length()];
          for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(0).length(); j++) {
              bestLossesArray[i][j] = Integer.MAX_VALUE;
            }
          }
          bestLosses.put(new DirectionTuple(d1, d2, d3), bestLossesArray);
        }
      }
    }
    
    
    minX[losses[0].length - 1] = 0;
    minY[losses.length - 1] = 0;
    for (int i = losses.length - 1; i > 0; i--) {
      int min = losses[i][0];
      for (int j = losses[0].length - 1; j >= 0; j--) {
        if (losses[i][j] < min) {
          min = losses[i][j];
        }
      }
      minY[i - 1] = minY[i] + min;
    }
    
    for (int j = losses[0].length - 1; j > 0; j--) {
      int min = losses[0][j];
      for (int i = losses.length - 1; i >= 0; i--) {
        if (losses[i][j] < min) {
          min = losses[i][j];
        }
      }
      minX[j - 1] = minX[j] + min;
    }
    System.out.println("minX = " + Arrays.asList(minX));
    System.out.println("minY = " + Arrays.asList(minY));
    
    
    Position startingPosition = new Position(0, 0);
    Position targetPosition = new Position(lines.get(0).length() - 1, lines.size() - 1);
    Path startingPath = new Path(startingPosition, targetPosition);
    long heatLoss = calculate(losses, bestLosses, startingPath, minX, minY);

    System.out.println("heatLoss is " + heatLoss);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
    /*
    for (DirectionTuple dt : bestLosses.keySet()) {
      System.out.println("\n" + dt);
      for (int i = 0; i < bestLosses.get(dt).length; i++) {
        for (int j = 0; j < bestLosses.get(dt)[0].length; j++) {
          System.out.print(" " + bestLosses.get(dt)[i][j]);
        }
        System.out.println("");
      }
    }*/
  }
}
