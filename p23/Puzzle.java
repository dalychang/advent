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
  public record Position(int x, int y) {}
  public record Path(Position position, Set<Position> traveled) {}
  
  public static enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0),
    SOUTH(0, 1);
    
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
          return EAST;
        default:
          throw new RuntimeException("Invalid direction");
      }
    }
  };
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static Direction getDirection(char c) {
    switch (c) {
      case 'v':
        return Direction.SOUTH;
      case '^':
        return Direction.NORTH;
      case '<':
        return Direction.WEST;
      case '>':
        return Direction.EAST;
      default:
        throw new RuntimeException("Invalid character " + c);
    }
  }
  
  public static Path walk(List<String> terrain, boolean[][] walkable, Path rootPath, Direction direction) {
    int x = rootPath.position().x() + direction.dx;
    int y = rootPath.position().y() + direction.dy;
    if (x < 0 || x >= walkable[0].length) return null;
    if (y < 0 || y >= walkable.length) return null;
    if (!walkable[y][x]) return null;
    
    Position position = new Position(x, y);
    if (rootPath.traveled().contains(position)) return null;
    char c = terrain.get(y).charAt(x);
    if (c != '.') {
      Direction slopeDirection = getDirection(c);
      Position slopePosition = new Position(x + slopeDirection.dx, y + slopeDirection.dy);
      if (slopeDirection.equals(rootPath.position())) return null;
      if (rootPath.traveled().contains(slopePosition)) return null;
      if (!walkable[slopePosition.y()][slopePosition.x()]) return null;
      
      Set<Position> traveledSet = new HashSet<>(rootPath.traveled());
      traveledSet.add(position);
      traveledSet.add(slopePosition);
      return new Path(slopePosition, traveledSet);
    }
    
    Set<Position> traveledSet = new HashSet<>(rootPath.traveled());
    traveledSet.add(position);
    return new Path(position, traveledSet);
  }
  
  public static List<Path> walk(List<String> terrain, boolean[][] walkable, Path rootPath) {
    List<Path> paths = new ArrayList<>();
    for (Direction direction : Direction.values()) {
      Path path = walk(terrain, walkable, rootPath, direction);
      if (path != null) {
        paths.add(path);
      }
    }
    return paths;
  }
  
  public static List<Position> findLongestPath(List<String> terrain, boolean[][] walkable, Position startingPoint, Position endingPoint) {
    LinkedList<Path> paths = new LinkedList<>();
    paths.add(new Path(startingPoint, Set.of(startingPoint)));
    Path bestPath = null;
    
    while (!paths.isEmpty()) {
      Path currentPath = paths.poll();
      List<Path> newPaths = walk(terrain, walkable, currentPath);
      for (Path path : newPaths) {
        if (path.position().equals(endingPoint)) {
          if (bestPath == null) {
            bestPath = path;
          } else if (path.traveled().size() > bestPath.traveled().size()) {
            bestPath = path;
          }
        } else {
          paths.add(path);
        }
      }
    }
    
    return new ArrayList<>(bestPath.traveled());
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p23/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    boolean walkable[][] = new boolean[lines.size()][lines.get(0).length()];
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        walkable[i][j] = (line.charAt(j) != '#');
      }
    }
    Helper.printBitmap(walkable, '.', '#');
    
    Position startingPoint = new Position(1, 0);
    Position endingPoint = new Position(lines.get(0).length() - 2, lines.size() - 1);
    System.out.println("startingPoint is " + startingPoint);    
    System.out.println("endingPoint is " + endingPoint);    
    
    List<Position> path = findLongestPath(lines, walkable, startingPoint, endingPoint);
    System.out.println("answer is " + (path.size() - 1));     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
