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

public class Puzzlev5 {
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
          return STOPPED;
      }
    }
  };
  
  public record Position(int x, int y) {
    
  }
  
  public record DirectionKey(Direction direction, int distance) {}
  
  public static class Path implements Comparable<Path> {
    DirectionKey directionKey = new DirectionKey(Direction.STOPPED, 0);
    Set<Position> positionsVisited = new HashSet<>();
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
      path.directionKey = this.directionKey;
      path.positionsVisited.addAll(this.positionsVisited);
      path.heatLoss = this.heatLoss;
      return path;
    }
    
    public boolean isDone() {
      return currentPosition.equals(targetPosition);
    }
    
    private boolean isPositionValid(Position position, Integer[][] losses) {
      if (position.x < 0 || position.x >= losses[0].length) return false;
      if (position.y < 0 || position.y >= losses.length) return false;
      if (positionsVisited.contains(position)) return false;
      
      return true;
    }
    
    public boolean move(Integer[][] losses, Direction direction) {
      if (directionKey.direction() == direction) {
        directionKey = new DirectionKey(direction, directionKey.distance() + 1);
      } else {
        directionKey = new DirectionKey(direction, 1);
        Position newMinPosition = new Position(currentPosition.x + direction.dx * 4, currentPosition.y + direction.dy * 4);
        if (!isPositionValid(newMinPosition, losses)) {
          return false;
        }
      }

      currentPosition = new Position(currentPosition.x + direction.dx, currentPosition.y + direction.dy);
      if (!isPositionValid(currentPosition, losses)) {
        return false;
      }
      
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
    
    public int cost(Path p) {
      int thisDistance = 1 + currentPosition.x + currentPosition.y;
      int pDistance = 1 + p.currentPosition.x + p.currentPosition.y;
      int thisCost = this.heatLoss * 1000 / thisDistance;
      int pCost = p.heatLoss * 1000 / pDistance;
      return thisCost - pCost;
    }
    
    public String toString() {
      return "Path(heatloss=" + heatLoss + ", x=" + currentPosition.x + ", y=" + currentPosition.y + ", positionsVisited=" + positionsVisited.size() + ")";
    }
  }
  
  public static boolean isDirectionValid(DirectionKey directionKey, Direction nextDirection, Integer[][] losses) {
    if (directionKey.direction() == nextDirection) {
      return directionKey.distance() < 10;
    }
    
    boolean goingBackwards = directionKey.direction().reverse() == nextDirection;
    return (directionKey.distance() >= 4 && !goingBackwards) || directionKey.direction() == Direction.STOPPED;
  }
  
  public static long calculate(Integer[][] losses, Map<DirectionKey, Integer[][]> bestLosses, Path startingPath, Integer[] minX, Integer[] minY) {
    PriorityQueue<Path> paths = new PriorityQueue<>((p1, p2) -> p1.cost(p2));
    //PriorityQueue<Path> paths = new PriorityQueue<>();
    paths.add(startingPath);
    
    // Seed a path;
  /*  Path seedPath = startingPath.copy();
    for (int i = 1; i < losses.length; i++) {
      for (int j = 0; j < Math.min(8, i - 8); j++)
      {
        seedPath.move(losses, Direction.EAST);
        DirectionTuple dt = new DirectionTuple(seedPath.lastDirections.get(0), seedPath.lastDirections.get(1), seedPath.lastDirections.get(2));
        bestLosses.get(dt)[seedPath.currentPosition.y][seedPath.currentPosition.x] = seedPath.heatLoss;
      }
      for (int j = 0; j < Math.min(8, i - 8); j++)
      {
        DirectionTuple dt = new DirectionTuple(seedPath.lastDirections.get(0), seedPath.lastDirections.get(1), seedPath.lastDirections.get(2));
        seedPath.move(losses, Direction.SOUTH);
        bestLosses.get(dt)[seedPath.currentPosition.y][seedPath.currentPosition.x] = seedPath.heatLoss;
      }
    }
    if (!seedPath.isDone()) {
      throw new RuntimeException("Bad seed path.");
    }*/
    Path bestCompletedPath = null;//seedPath;
    //System.out.println("Seeded heatLoss = " + seedPath.heatLoss);
    
    int iter = 0;
    while (!paths.isEmpty()) {
      iter++;
      Path path = paths.poll();
      int bestHeatLossSoFar = bestCompletedPath == null ? Integer.MAX_VALUE : bestCompletedPath.heatLoss;
      if (iter % 1000000 == 0)
        System.out.println("paths=" + paths.size() + " best=" + bestHeatLossSoFar);
      if (path.heatLoss > bestHeatLossSoFar) {
        continue;
      }
      // Optimization.
      if (path.heatLoss + minX[path.currentPosition.x] + minY[path.currentPosition.y] > bestHeatLossSoFar) {
        continue;
      }

      if (path.isDone()) {
        if (path.directionKey.distance() < 4) continue;

        if (bestCompletedPath == null || path.heatLoss < bestCompletedPath.heatLoss) {
          bestCompletedPath = path;
        }
        continue;
      }

      for (Direction direction : Direction.values()) {
        if (direction == Direction.STOPPED) continue;
        //System.out.println(path.directionKey + " " + direction);
        if (isDirectionValid(path.directionKey, direction, losses)) {
          Path newPath = path.copy();
          if (newPath.move(losses, direction)) {
            if (newPath.heatLoss < bestLosses.get(newPath.directionKey)[newPath.currentPosition.y][newPath.currentPosition.x]) {
              bestLosses.get(newPath.directionKey)[newPath.currentPosition.y][newPath.currentPosition.x] = newPath.heatLoss;
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
    Map<DirectionKey, Integer[][]> bestLosses = new HashMap<>();
    Integer[] minX = new Integer[lines.get(0).length()];
    Integer[] minY = new Integer[lines.size()];
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        losses[i][j] = Integer.parseInt(String.valueOf(line.charAt(j)));
      }
    }
    for (Direction direction : Direction.values()) {
      if (direction == Direction.STOPPED) {
        continue;
      }
      for (int k = 1; k <= 10; k++) {
        DirectionKey dk = new DirectionKey(direction, k);
        Integer[][] bestLossesArray = new Integer[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
          for (int j = 0; j < lines.get(0).length(); j++) {
            bestLossesArray[i][j] = Integer.MAX_VALUE;
          }
        }
        bestLosses.put(dk, bestLossesArray);
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
