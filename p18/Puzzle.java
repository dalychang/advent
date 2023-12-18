package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
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

public class Puzzle {
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
  
  public record Instruction(Direction direction, int meters, String color) {}
  public record Position(int x, int y) {}

  public static class GrowingArray<T> {
    List<List<T>> normal = new ArrayList<>();
    List<List<T>> invertedY = new ArrayList<>();
    List<List<T>> invertedX = new ArrayList<>();
    List<List<T>> invertedBoth = new ArrayList<>();
    
    int minX = 0;
    int minY = 0;
    int maxX = 0;
    int maxY = 0;
    
    T emptyValue;
    
    public GrowingArray(T emptyValue) {
      this.emptyValue = emptyValue;
      normal.add(createEmptyArray(1));
      invertedX.add(createEmptyArray(0));
    }
    
    private List<T> createEmptyArray(int size) {
      List<T> list = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        list.add(emptyValue);
      }
      return list;
    }
    
    public void set(int x, int y, T value) {
      if (y < minY) {
        for (int i = minY; i > y; i--) {
          invertedY.add(createEmptyArray(maxX + 1));
          invertedBoth.add(createEmptyArray(-minX));
        }
        minY = y;
      }
      if (y > maxY) {
        for (int i = maxY; i < y; i++) {
          normal.add(createEmptyArray(maxX + 1));
          invertedX.add(createEmptyArray(-minX));
        }
        maxY = y;
      }
      if (x < minX) {
        for (int i = 0; i < invertedX.size(); i++) {
          for (int j = minX; j > x; j--) {
            invertedX.get(i).add(emptyValue);
          }
        }
        for (int i = 0; i < invertedBoth.size(); i++) {
          for (int j = minX; j > x; j--) {
            invertedBoth.get(i).add(emptyValue);
          }
        }
        minX = x;
      }
      if (x > maxX) {
        for (int i = 0; i < normal.size(); i++) {
          for (int j = maxX; j < x; j++) {
            normal.get(i).add(emptyValue);
          }
        }
        for (int i = 0; i < invertedY.size(); i++) {
          for (int j = maxX; j < x; j++) {
            invertedY.get(i).add(emptyValue);
          }
        }
        maxX = x;
      }

      if (y < 0 && x < 0) {
        invertedBoth.get(-y - 1).set(-x - 1, value);
      } else if (x < 0) {
        invertedX.get(y).set(-x - 1, value);
      } else if (y < 0) {
        invertedY.get(-y - 1).set(x, value);
      } else {
        normal.get(y).set(x, value);
      }
    }
    
    public T get(int x, int y) {
      if (x < 0 && y < 0) {
        return invertedBoth.get(-y - 1).get(-x - 1);
      } else if (x < 0) {
        return invertedX.get(y).get(-x - 1);
      } else if (y < 0) {
        return invertedY.get(-y - 1).get(x);
      } else {
        return normal.get(y).get(x);
      }
    }
    
    public int getMinX() {
      return minX;
    }
    
    public int getMinY() {
      return minY;
    }
    
    public int getMaxX() {
      return maxX;
    }
    
    public int getMaxY() {
      return maxY;
    }
    
    public int getHeight() {
      return maxY - minY;
    }
    
    public int getWidth() {
      return maxX - minX;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (int i = -minY - 1; i >= 0; i--) {
        for (int j = -minX - 1; j >= 0; j--) {
          sb.append(invertedBoth.get(i).get(j));
        }
        for (int j = 0; j < maxX + 1; j++) {
          sb.append(invertedY.get(i).get(j));
        }
        sb.append("\n");
      }
      for (int i = 0; i < maxY + 1; i++) {
        for (int j = -minX - 1; j >= 0; j--) {
          sb.append(invertedX.get(i).get(j));
        }
        for (int j = 0; j < maxX + 1; j++) {
          sb.append(normal.get(i).get(j));
        }
        sb.append("\n");
      }
      return sb.toString();
    }
  }
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static Direction toDirection(String s) {
    switch (s) {
      case "U":
        return Direction.UP;
      case "R":
        return Direction.RIGHT;
      case "D":
        return Direction.DOWN;
      case "L":
        return Direction.LEFT;
      default:
        throw new RuntimeException("Unexpected character" + s);
    }
  }
  
  public static void flood(GrowingArray<Character> grid, int x, int y, char emptyCharacter, char character) {
    LinkedList<Position> positions = new LinkedList<>();
    positions.add(new Position(x, y));
    
    while (!positions.isEmpty()) {
      Position p = positions.poll();
      if (p.x() < grid.getMinX() || p.x() > grid.getMaxX()) continue;
      if (p.y() < grid.getMinY() || p.y() > grid.getMaxY()) continue;
      if (grid.get(p.x(), p.y()) != emptyCharacter) continue;
      
      grid.set(p.x(), p.y(), character);
      positions.addLast(new Position(p.x() + 1, p.y()));
      positions.addLast(new Position(p.x() - 1, p.y()));
      positions.addLast(new Position(p.x(), p.y() + 1));
      positions.addLast(new Position(p.x(), p.y() - 1));
    }
  }
  
  public static void populate(GrowingArray<Character> grid, List<Instruction> instructions, int startX, int startY, char character) {
    int x = startX;
    int y = startY;
    
    grid.set(x, y, character);
    for (Instruction instruction : instructions) {
      for (int i = 0; i < instruction.meters(); i++) {
        x += instruction.direction().dx;
        y += instruction.direction().dy;
        grid.set(x, y, character);
      }
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p18/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    //for (String line : lines) {
    //  System.out.println(line);
    //}
    
    GrowingArray<Character> grid = new GrowingArray<>('.');
    Pattern pattern = Pattern.compile("^(\\w) (\\d+) \\(#([\\da-f]{6})\\)$");
    
    List<Instruction> instructions = new ArrayList<>();
    for (String line : lines) {
      Matcher m = pattern.matcher(line);
      m.find();
      Direction direction = toDirection(m.group(1));
      int meters = Integer.parseInt(m.group(2));
      String color = m.group(3);
      instructions.add(new Instruction(direction, meters, color));
    }
    
    populate(grid, instructions, 0, 0, '#');
    
    System.out.println(grid);
        
    for (int i = grid.getMinX(); i <= grid.getMaxX(); i++) {
      flood(grid, i, grid.getMinY(), '.', 'o');
      flood(grid, i, grid.getMaxY(), '.', 'o');
    }
    for (int i = grid.getMinY(); i <= grid.getMaxY(); i++) {
      flood(grid, grid.getMinX(), i, '.', 'o');
      flood(grid, grid.getMaxX(), i, '.', 'o');
    }
    
    System.out.println(grid);
    
    int total = 0;
    for (int i = grid.getMinY(); i <= grid.getMaxY(); i++) {
      for (int j = grid.getMinX(); j <= grid.getMaxX(); j++) {
        char c = grid.get(j, i);
        if (c == '.' || c == '#') {
          total++;
        }
      }
    }

    System.out.println("total is " + total);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
