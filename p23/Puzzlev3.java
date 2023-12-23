package dev.advent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public class Puzzlev3 {
  public record Position(int x, int y) implements Comparable<Position> {
    public int compareTo(Position p) {
      if (this.y == p.y) {
        return this.x - p.x;
      }
      return this.y - p.y;
    }
  }
  public record Path(Position startPosition, Position position, Set<Position> traveled) {}
  public record ChokePath(Position position, Set<Position> traveled, long distance) {}
  public record ChokePointPair(Position p1, Position p2) {}
  
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
    
    Set<Position> traveledSet = new HashSet<>(rootPath.traveled());
    traveledSet.add(position);

    return new Path(rootPath.startPosition(), position, traveledSet);
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
  
  public static ChokePointPair buildChokePointPair(Position p1, Position p2) {
    if (p1.compareTo(p2) > 0) {
      return new ChokePointPair(p1, p2);
    } else {
      return new ChokePointPair(p2, p1);
    }
  }
  
  public static Map<ChokePointPair, Integer> findLongestPathBetweenChokePoints(List<String> terrain, boolean[][] walkable, Position startingPoint, Position endingPoint, Set<Position> chokePoints) {
    Map<ChokePointPair, Integer> chokeMap = new HashMap<>();
    LinkedList<Path> paths = new LinkedList<>();
    for (Position position : chokePoints) {
      paths.add(new Path(position, position, Set.of(position)));
    }
    
    while (!paths.isEmpty()) {
      Path currentPath = paths.poll();
      List<Path> newPaths = walk(terrain, walkable, currentPath);
      for (Path path : newPaths) {
        if (chokePoints.contains(path.position())) {
          ChokePointPair cpp = buildChokePointPair(path.position(), path.startPosition());
          if (!chokeMap.containsKey(cpp)) {
            chokeMap.put(cpp, 0);
          }
          chokeMap.put(cpp, Math.max(path.traveled().size() - 1, chokeMap.get(cpp)));
        } else {
          paths.add(path);
        }
      }
    }
    
    return chokeMap;
  }
  
  public static boolean isWalkable(boolean walkable[][], Position position, Direction direction) {
    int x = position.x() + direction.dx;
    int y = position.y() + direction.dy;
    if (x < 0 || x >= walkable[0].length) return false;
    if (y < 0 || y >= walkable.length) return false;
    return walkable[y][x];
  }
  
  public static Set<Position> findChokePoints(boolean walkable[][]) {
    Set<Position> chokePoints = new HashSet<>();
    for (int i = 0; i < walkable.length; i++) {
      for (int j = 0; j < walkable[0].length; j++) {
        if (!walkable[i][j]) continue;
        int count = 0;
        Position position = new Position(j, i);
        for (Direction direction : Direction.values()) {
          if (isWalkable(walkable, position, direction)) {
            count++;
          }
        }
        if (count > 2) {
          chokePoints.add(position);
        }
      }
    }
    return chokePoints;
  }
  
  private static long doFindLongestPathRecursive(Map<ChokePointPair, Integer> chokeMap, Position currentPosition, Set<Position> traveled, Set<Position> chokePoints, HashMultimap<Position, Position> travelPoints, Position endingPoint, long distanceSoFar) {
    if (currentPosition.equals(endingPoint)) return distanceSoFar;

    long longest = 0;
    for (Position nextPosition : travelPoints.get(currentPosition)) {
      if (traveled.contains(nextPosition)) continue;
      
      ChokePointPair cpp = buildChokePointPair(nextPosition, currentPosition);
      Set<Position> nextTraveled = new HashSet<>(traveled);
      nextTraveled.add(nextPosition);
      long distance = doFindLongestPathRecursive(chokeMap, nextPosition, nextTraveled, chokePoints, travelPoints, endingPoint, distanceSoFar + chokeMap.get(cpp));
      if (distance > longest) {
        longest = distance;
      }
    }
    return longest;
  }
  
  private static long findLongestPathRecursive(Map<ChokePointPair, Integer> chokeMap, Position startingPoint, Position endingPoint, Set<Position> chokePoints, HashMultimap<Position, Position> travelPoints) {
    Set<Position> traveled = new HashSet<>();
    traveled.add(startingPoint);
    return doFindLongestPathRecursive(chokeMap, startingPoint, traveled, chokePoints, travelPoints, endingPoint, 0L);
  }
  
  private static long findLongestPath(Map<ChokePointPair, Integer> chokeMap, Position startingPoint, Position endingPoint, Set<Position> chokePoints, HashMultimap<Position, Position> travelPoints) {
    LinkedList<ChokePath> paths = new LinkedList<>();
    paths.add(new ChokePath(startingPoint, Set.of(startingPoint), 0L));
    ChokePath bestPath = null;
    
    while (!paths.isEmpty()) {
      ChokePath currentPath = paths.poll();
      for (Position nextPosition : travelPoints.get(currentPath.position())) {
        if (currentPath.traveled().contains(nextPosition)) continue;
        
        ChokePointPair cpp = buildChokePointPair(nextPosition, currentPath.position());
        long distance = currentPath.distance() + chokeMap.get(cpp);
        Set<Position> traveled = new HashSet<>(currentPath.traveled());
        traveled.add(nextPosition);
        ChokePath nextPath = new ChokePath(nextPosition, traveled, distance);
        
        if (nextPosition.equals(endingPoint)) {
          if (bestPath == null || bestPath.distance() < nextPath.distance()) {
            bestPath = nextPath;
          }
        } else {
          paths.addFirst(nextPath);
        }
      }
    }
    return bestPath.distance();
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
    
    long midStartTime = clock.millis();
    Set<Position> chokePoints = findChokePoints(walkable);
    System.out.println("findChokePoints " + (clock.millis() - midStartTime) + "ms");
    //System.out.println("chokePoints is " + chokePoints);
    chokePoints.add(startingPoint);
    chokePoints.add(endingPoint);
    midStartTime = clock.millis();
    Map<ChokePointPair, Integer> chokeMap = findLongestPathBetweenChokePoints(lines, walkable, startingPoint, endingPoint, chokePoints);
    System.out.println("findLongestPathBetweenChokePoints " + (clock.millis() - midStartTime) + "ms");
    //System.out.println("chokeMap is " + chokeMap);
    
    midStartTime = clock.millis();
    HashMultimap<Position, Position> travelPoints = HashMultimap.create();
    for (ChokePointPair cpp : chokeMap.keySet()) {
      travelPoints.put(cpp.p1(), cpp.p2());
      travelPoints.put(cpp.p2(), cpp.p1());
    }
    System.out.println("buildPairs " + (clock.millis() - midStartTime) + "ms");
    
    midStartTime = clock.millis();
    long answer = findLongestPathRecursive(chokeMap, startingPoint, endingPoint, chokePoints, travelPoints);
    System.out.println("findLongestPathRecursive " + (clock.millis() - midStartTime) + "ms");
        
    System.out.println("findLongestPathRecursive answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
