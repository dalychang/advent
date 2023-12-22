package dev.advent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
  public record Coordinate(int x, int y, int z) {}
  public record Brick(int id, Coordinate start, Coordinate end) implements Comparable<Brick> {
    public int compareTo(Brick b) {
      int thisBottomZ = Math.min(start().z(), end().z());
      int bBottomZ = Math.min(b.start().z(), b.end().z());
      return thisBottomZ - bBottomZ;
    }
  }
  
  public static class Tower {
    private final int maxX;
    private final int maxY;
    private final List<Brick[][]> brickLayers;
    
    private int maxZ;
    
    public Tower(int maxX, int maxY) {
      this.maxX = maxX;
      this.maxY = maxY;
      brickLayers = new ArrayList<>();
      maxZ = 0;
    }
    
    private void ensureZ(int z) {
      if (brickLayers.size() < z) {
        maxZ = z;
        for (int i = brickLayers.size(); i <= z; i++) {
          brickLayers.add(new Brick[maxX + 1][maxY + 1]);
        }
      }
    }
    
    public Brick get(int x, int y, int z) {
      ensureZ(z);
      return brickLayers.get(z - 1)[x][y];
    }
    
    public boolean has(int x, int y, int z) {
      ensureZ(z);
      return brickLayers.get(z - 1)[x][y] != null;
    }
    
    public Brick set(int x, int y, int z, Brick brick) {
      ensureZ(z);
      return brickLayers.get(z - 1)[x][y] = brick;
    }

    public int maxX() { return maxX; }
    public int maxY() { return maxY; }
    public int maxZ() { return maxZ; }
  }
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static int findZLevel(Tower tower, Brick brick) {
    int bottomZ = Math.min(brick.start().z(), brick.end().z());
    int z = bottomZ;
    if (z == 1) return z;
    
    boolean collision = false;
    while (!collision && z > 1) {
      z--;
      for (int x = Math.min(brick.start().x(), brick.end().x());
          x <= Math.max(brick.start().x(), brick.end().x());
          x++) {
        for (int y = Math.min(brick.start().y(), brick.end().y());
            y <= Math.max(brick.start().y(), brick.end().y());
            y++) {
          if (tower.has(x, y, z)) {
            collision = true;
            break;
          }
        }
      }
    }
    if (collision) {
      return z + 1;
    }
    return z;
  }
  
  private static Coordinate reduceZ(Coordinate c, int deltaZ) {
    return new Coordinate(c.x(), c.y(), c.z() - deltaZ);
  }
  
  public static Brick populate(Tower tower, Brick brick, int insertZ) {
    int bottomZ = Math.min(brick.start().z(), brick.end().z());
    int deltaZ = bottomZ - insertZ;
    Brick updatedBrick = new Brick(brick.id(), reduceZ(brick.start(), deltaZ), reduceZ(brick.end(), deltaZ));
    
    for (int x = Math.min(updatedBrick.start().x(), updatedBrick.end().x());
        x <= Math.max(updatedBrick.start().x(), updatedBrick.end().x());
        x++) {
      for (int y = Math.min(updatedBrick.start().y(), updatedBrick.end().y());
          y <= Math.max(updatedBrick.start().y(), updatedBrick.end().y());
          y++) {
        for (int z = Math.min(updatedBrick.start().z(), updatedBrick.end().z());
            z <= Math.max(updatedBrick.start().z(), updatedBrick.end().z());
            z++) {
          tower.set(x, y, z, updatedBrick);
        }
      }
    }
    return updatedBrick;
  }
  
  public static void findSupportsUpper(HashMultimap<Brick, Brick> brickSupportMap, List<Brick> bricks, Tower tower) {
    for (Brick brick : bricks) {
      int topZ = Math.max(brick.start().z(), brick.end().z());
      if (topZ == tower.maxZ()) continue;
      
      for (int x = Math.min(brick.start().x(), brick.end().x());
          x <= Math.max(brick.start().x(), brick.end().x());
          x++) {
        for (int y = Math.min(brick.start().y(), brick.end().y());
            y <= Math.max(brick.start().y(), brick.end().y());
            y++) {
          Brick upperBrick = tower.get(x, y, topZ + 1);
          if (upperBrick != null) {
            brickSupportMap.put(brick, upperBrick);
          }
        }
      }
    }
  }
  
  public static void findSupportsLower(HashMultimap<Brick, Brick> brickSupportMap, List<Brick> bricks, Tower tower) {
    for (Brick brick : bricks) {
      int bottomZ = Math.min(brick.start().z(), brick.end().z());
      if (bottomZ == 1) continue;
      
      for (int x = Math.min(brick.start().x(), brick.end().x());
          x <= Math.max(brick.start().x(), brick.end().x());
          x++) {
        for (int y = Math.min(brick.start().y(), brick.end().y());
            y <= Math.max(brick.start().y(), brick.end().y());
            y++) {
          Brick lowerBrick = tower.get(x, y, bottomZ - 1);
          if (lowerBrick != null) {
            brickSupportMap.put(brick, lowerBrick);
          }
        }
      }
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p22/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int maxX = 0;
    int maxY = 0;
    List<Brick> bricks = new ArrayList<>();
    Pattern brickPattern = Pattern.compile("^(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)$");
    int id = 1;
    for (String line : lines) {
      Matcher m = brickPattern.matcher(line);
      m.find();
      Brick brick = new Brick(id++,
          new Coordinate(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))),
          new Coordinate(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))));
      bricks.add(brick);
      int bmx = Math.max(brick.start().x(), brick.end().x());
      maxX = Math.max(maxX, bmx);
      int bmy = Math.max(brick.start().y(), brick.end().y());
      maxY = Math.max(maxY, bmy);
    }
    bricks = bricks.stream().sorted().toList();
    
    Tower tower = new Tower(maxX, maxY);
    List<Brick> updatedBricks = new ArrayList<>();
    for (Brick brick : bricks) {
      int zLevel = findZLevel(tower, brick);
      updatedBricks.add(populate(tower, brick, zLevel));
    }
    bricks = updatedBricks;
    
    // Key brick supports value bricks.
    HashMultimap<Brick, Brick> brickSupportsValueMap = HashMultimap.create();
    HashMultimap<Brick, Brick> brickSupportedByValueMap = HashMultimap.create();
    findSupportsUpper(brickSupportsValueMap, bricks, tower);
    findSupportsLower(brickSupportedByValueMap, bricks, tower);

    System.out.println("maxX = " + maxX);
    System.out.println("maxY = " + maxY);
    
    long answer = 0;
    for (Brick brick : bricks) {
      Set<Brick> bricksSupportedByThis = brickSupportsValueMap.get(brick);
      if (bricksSupportedByThis.isEmpty()) {
        answer++;
        continue;
      }
      
      boolean removable = true;
      for (Brick supportedBrick : bricksSupportedByThis) {
        Set<Brick> supportingBricks = brickSupportedByValueMap.get(supportedBrick);
        if (supportingBricks.size() <= 1) {
          removable = false;
          break;
        }
      }
      if (removable) {
        answer++;
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
