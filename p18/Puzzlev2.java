package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  public static enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    LEFT(-1, 0),
    DOWN(0, 1);
    
    public int dx;
    public int dy;
    
    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
    
    public boolean isHorizontal() {
      return this == LEFT || this == RIGHT;
    }
    
    public boolean isVertical() {
      return this == UP || this == DOWN;
    }
    
    public Direction reverse() {
      switch (this) {
        case UP:
          return DOWN;
        case DOWN:
          return UP;
        case RIGHT:
          return LEFT;
        case LEFT:
        default:
          return RIGHT;
      }
    }
  };
  
  public static enum Bend {
    BL(1),
    B7(1),
    BJ(3),
    BF(3),
    UNKNOWN(0);
    
    public final int value;
    
    Bend(int value) {
      this.value = value;
    }
  }
  
  public record Instruction(Direction direction, long meters) {}
  public record Position(long x, long y) {}
  public record Point(long x, long y, Instruction instruction, Bend bendBefore, Bend bendAfter) {}

  public static class GrowingGrid {
    List<Point> points = new ArrayList<>();
    Point previousPoint = null;
    Position currentPosition = new Position(0, 0);
    
    long minX = 0;
    long minY = 0;
    long maxX = 0;
    long maxY = 0;
    
    public GrowingGrid() {
    }
    
    private Bend calculateBend(Direction previousDirection, Direction nextDirection) {
      if (previousDirection == null) {
        return Bend.UNKNOWN;
      }
      if (previousDirection == Direction.UP && nextDirection == Direction.LEFT) {
        return Bend.B7;
      } else if (previousDirection == Direction.LEFT && nextDirection == Direction.UP) {
        return Bend.BL;
      } else if (previousDirection == Direction.UP && nextDirection == Direction.RIGHT) {
        return Bend.BF;
      } else if (previousDirection == Direction.RIGHT && nextDirection == Direction.UP) {
        return Bend.BJ;
      } else if (previousDirection == Direction.DOWN && nextDirection == Direction.LEFT) {
        return Bend.BJ;
      } else if (previousDirection == Direction.LEFT && nextDirection == Direction.DOWN) {
        return Bend.BF;
      } else if (previousDirection == Direction.DOWN && nextDirection == Direction.RIGHT) {
        return Bend.BL;
      } else if (previousDirection == Direction.RIGHT && nextDirection == Direction.DOWN) {
        return Bend.B7;
      } else {
        throw new RuntimeException("Unknown bend " + previousDirection + " " + nextDirection);
      }
    }
    
    public void add(Instruction instruction) {
      long x = currentPosition.x() + instruction.direction().dx * instruction.meters();
      long y = currentPosition.y() + instruction.direction().dy * instruction.meters();
      Bend bend = calculateBend(previousPoint == null ? null : previousPoint.instruction().direction(), instruction.direction());
      
      if (previousPoint != null) {
        Point lastPoint = points.get(points.size() - 1);
        Point updatedPoint = new Point(lastPoint.x(), lastPoint.y(), lastPoint.instruction(), lastPoint.bendBefore(), bend);
        points.set(points.size() - 1, updatedPoint);
      }
      
      previousPoint = new Point(currentPosition.x(), currentPosition.y(), instruction, bend, Bend.UNKNOWN);
      points.add(previousPoint);
      currentPosition = new Position(x, y);
      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
    }
    
    public void finalize() {
      if (previousPoint == null) return;
      if (!currentPosition.equals(new Position(0, 0))) {
        throw new RuntimeException("Path is not closed.");
      }
      Point startPoint = points.get(0);
      Bend bend = calculateBend(previousPoint.instruction().direction(), startPoint.instruction.direction());
      Point point = new Point(startPoint.x(), startPoint.y(), startPoint.instruction(), bend, startPoint.bendAfter());
      points.set(0, point);
      Point lastPoint = new Point(previousPoint.x(), previousPoint.y(), previousPoint.instruction(), previousPoint.bendBefore(), bend);
      points.set(points.size() - 1, lastPoint);
    }
    
    private boolean inBetween(long p1, long p2, long value) {
      long min = Math.min(p1, p2);
      long max = Math.max(p1, p2);
      return value >= min && value <= max;
    }
    
    public List<Point> getRelevantPoints(long y) {
      List<Point> relevantPoints = new ArrayList<>();
      for (Point point : points) {
        if ((point.instruction().direction().isHorizontal() && point.y() == y)
            || (point.instruction().direction().isVertical() && inBetween(point.y(), point.y() + point.instruction().direction().dy * point.instruction().meters(), y))) {
          if (point.instruction().direction() == Direction.LEFT) {
            // Convert to right.
            Instruction newInstruction = new Instruction(point.instruction().direction().reverse(), point.instruction().meters());
            Point updatedPoint = new Point(point.x() - point.instruction().meters(), point.y(), newInstruction, point.bendAfter(), point.bendBefore());
            relevantPoints.add(updatedPoint);
          } else {
            relevantPoints.add(point);
          }
        }
      }
      return relevantPoints.stream().sorted(Comparator.comparingLong(Point::x)).toList();
    }
    
    public long getMinX() {
      return minX;
    }
    
    public long getMinY() {
      return minY;
    }
    
    public long getMaxX() {
      return maxX;
    }
    
    public long getMaxY() {
      return maxY;
    }
    
    public long getHeight() {
      return maxY - minY;
    }
    
    public long getWidth() {
      return maxX - minX;
    }
    
    public String toString() {
      return points.toString();
    }
  }
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static Direction toDirection(String s) {
    switch (s) {
      case "3":
        return Direction.UP;
      case "0":
        return Direction.RIGHT;
      case "1":
        return Direction.DOWN;
      case "2":
        return Direction.LEFT;
      default:
        throw new RuntimeException("Unexpected character" + s);
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p18/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    GrowingGrid grid = new GrowingGrid();
    Pattern pattern = Pattern.compile("^(\\w) (\\d+) \\(#([\\da-f]{5})([\\da-f]{1})\\)$");
    
    List<Instruction> instructions = new ArrayList<>();
    for (String line : lines) {
      Matcher m = pattern.matcher(line);
      m.find();
      Direction direction = toDirection(m.group(4));
      int meters = Integer.parseInt(m.group(3), 16);
      instructions.add(new Instruction(direction, meters));
    }

    for (Instruction instruction : instructions) {
      grid.add(instruction);
    }
    grid.finalize();
    
    System.out.println("width=" + grid.getWidth());
    System.out.println("height=" + grid.getHeight());
    
    System.out.println("Calculating");
    long insideSpots = 0;
    long dugTotal = 0;
    for (long i = grid.getMinY(); i <= grid.getMaxY(); i++) {
      int edges = 0;
      int dug = 0;
      List<Point> points = grid.getRelevantPoints(i);
      long currentX = grid.getMinY();
      for (Point point : points) {
        if (i % 100000 == 0) {
          System.out.println((i - grid.getMinY()) + " / " + (grid.getMaxY() - grid.getMinY()));
        }
        if (edges == 2 && point.x() > currentX) {
          insideSpots += (point.x() - currentX); 
        }
        if (point.instruction().direction().isVertical()) {
          if (point.y() != i && point.y() + point.instruction().direction().dy * point.instruction().meters() != i) {
            edges = (edges + 2) % 4;
            dug++;
          }
          currentX = Math.max(currentX, point.x() + 1);
        } else if (point.instruction().direction().isHorizontal()) {
          dug += point.instruction().meters() + 1;
          edges = (edges + point.bendBefore().value + point.bendAfter().value) % 4;
          currentX = Math.max(currentX, point.x() + point.instruction().direction().dx * point.instruction().meters() + 1);
        }
      }
      insideSpots += dug;
    }

    System.out.println("insideSpots is " + insideSpots); 
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
