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
  public record Point(long x, long y, Instruction instruction, Bend bend) {}

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
      previousPoint = new Point(currentPosition.x(), currentPosition.y(), instruction, bend);
      points.add(previousPoint);
      currentPosition = new Position(x, y);
      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (x > maxY) maxY = y;
    }
    
    public void finalize() {
      if (previousPoint == null) return;
      if (!currentPosition.equals(new Position(0, 0))) {
        throw new RuntimeException("Path is not closed.");
      }
      Point startPoint = points.get(0);
      Bend bend = calculateBend(previousPoint.instruction().direction(), startPoint.instruction.direction());
      Point point = new Point(startPoint.x(), startPoint.y(), startPoint.instruction(), bend);
      points.set(0, point);
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
          relevantPoints.add(point);
        }
      }
      return relevantPoints.stream().sorted(Comparator.comparingLong(Point::x)).toList();
    }
    
    public Integer get(long x, long y) {
      for (Point point : points) {
        if (x == point.x() && y == point.y()) {
          return point.bend().value;
        }
      }
      for (Point point : points) {
        if (point.instruction().direction().isVertical()) {
          if (x == point.x() && inBetween(point.y(), point.y() + point.instruction().direction().dy * point.instruction().meters(), y)) {
            return 2;
          }
        } else if (point.instruction().direction().isHorizontal()) {
          if (y == point.y() && inBetween(point.x(), point.x() + point.instruction().direction().dx * point.instruction().meters(), x)) {
            return 0;
          }
        }
      }
      return null;
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
    final List<String> lines = Helper.loadFile("dev_advent/p18/input.txt");
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
    for (long i = grid.getMinY(); i <= grid.getMaxY(); i++) {
      int edges = 0;
      int dug = 0;
      List<Point> points = grid.getRelevantPoints(i);
      long currentX = 0;
      for (Point point : points) {
        if (edges == 2 && point.x() > currentX) {
          insideSpots += (point.x() - currentX); 
        }
        if (point.instruction().direction().isVertical()) {
          if (point.y() == i) {
            edges = (edges + point.bend().value) % 4;
          } else if (point.y() + point.instruction().direction().dy != i) {
            edges = (edges + 2) % 4;
            dug++;
          }
          currentX = Math.max(currentX, point.x());
        } else if (point.instruction().direction().isHorizontal()) {
          dug += point.instruction().meters();
          edges = (edges + point.bend().value) % 4;
          currentX = Math.max(currentX, point.x() + point.instruction().direction().dx + point.instruction().meters());
        }
      }
      insideSpots += dug;
      //if (i > 1186320) {
      //  System.out.println(i - grid.getMinY() + " / " + grid.getHeight() + " - " + points);
     // }
    }

    //System.out.println(grid);
    System.out.println("insideSpots is " + insideSpots); 
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
//952408144115
//952408289936
//952406273637
//952406273644
//952406273637
//952403901002