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

public class Puzzlev2 {
  public static int WIDTH = 0;
  public static int HEIGHT = 0;
  
  public record Position(int x, int y) {}
  public record MapPosition(int x, int y) {}
  
  public static void ensureBitmap(Map<MapPosition, boolean[][]> bitmapMap, MapPosition mapPosition) {
    if (!bitmapMap.containsKey(mapPosition)) {
      bitmapMap.put(mapPosition, new boolean[HEIGHT][WIDTH]);
    }
  }
  
  private static long count(Map<MapPosition, boolean[][]> bitmapMap) {
    long counter = 0;
    for (boolean[][] bitmap : bitmapMap.values()) {
      for (int i = 0; i < bitmap.length; i++) {
        for (int j = 0; j < bitmap[0].length; j++) {
          if (bitmap[i][j]) counter++;
        }
      }
    }
    return counter;
  }
  
  private static boolean isWalkable(boolean [][] bitmap, Position position) {
    return bitmap[position.y() % HEIGHT][position.x() % WIDTH];
  }
  
  private static boolean[][] getTargetBitmap(Map<MapPosition, boolean[][]> bitmapMap, Position position) {
    MapPosition mapPosition = new MapPosition(
        position.x() < 0 
            ? position.x() / WIDTH - 1
            : position.x() / WIDTH,
        position.y() < 0 
            ? position.y() / HEIGHT - 1
            : position.y() / HEIGHT);
    ensureBitmap(bitmapMap, mapPosition);
    return bitmapMap.get(mapPosition);
  }
  
  private static boolean runPosition(boolean [][] pathBitmap, Map<MapPosition, boolean[][]> bitmapMap, Position position) {
    boolean [][] targetBitmap = getTargetBitmap(bitmapMap, position);
    Position mp = new Position(((position.x() % WIDTH) + WIDTH) % WIDTH, ((position.y() % HEIGHT) + HEIGHT) % HEIGHT);
    if (pathBitmap[mp.y()][mp.x()] && !targetBitmap[mp.y()][mp.x()]) {
      targetBitmap[mp.y()][mp.x()] = true;
      return true;
    } else {
      return false;
    }
  }
  
  private static Map<Long, Long> compute(boolean[][] pathBitmap, Map<MapPosition, boolean[][]> oddBitmapMap, Map<MapPosition, boolean[][]> evenBitmapMap, long steps, Position startingPosition) {
    Map<Long, Long> answerMap = new HashMap<>();
    LinkedList<Position> positions = new LinkedList<>();
    positions.add(startingPosition);
    
    long currentStep = 0;
    while (!positions.isEmpty() && currentStep < steps) {
      currentStep++;
      
      Map<MapPosition, boolean[][]> targetBitmapMap = (currentStep % 2 == 0) ? evenBitmapMap : oddBitmapMap;
      LinkedList<Position> positionsToCheck = new LinkedList<>(positions);
      positions.clear();
      while (!positionsToCheck.isEmpty()) {
        Position cp = positionsToCheck.poll();
        Position north = new Position(cp.x(), cp.y() - 1);
        Position south = new Position(cp.x(), cp.y() + 1);
        Position west = new Position(cp.x() - 1, cp.y());
        Position east = new Position(cp.x() + 1, cp.y());
        if (runPosition(pathBitmap, targetBitmapMap, north)) {
          positions.addLast(north);
        }
        if (runPosition(pathBitmap, targetBitmapMap, south)) {
          positions.addLast(south);
        }
        if (runPosition(pathBitmap, targetBitmapMap, west)) {
          positions.addLast(west);
        }
        if (runPosition(pathBitmap, targetBitmapMap, east)) {
          positions.addLast(east);
        }
      }
      
      long answer = count(currentStep % 2 == 0 ? evenBitmapMap : oddBitmapMap);
      answerMap.put(currentStep, answer);
    }
    return answerMap;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p21/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Position startingPosition = null;
    WIDTH = lines.get(0).length();
    HEIGHT = lines.size();
    boolean [][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    Map<MapPosition, boolean[][]> oddBitmapMap = new HashMap<>();
    Map<MapPosition, boolean[][]> evenBitmapMap = new HashMap<>();
    
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        if (c == 'S') {
          startingPosition = new Position(j, i);
          bitmap[i][j] = true;
        } else if (c == '.') {
          bitmap[i][j] = true;
        } else if (c == '#') {
          bitmap[i][j] = false;
        } else {
          throw new RuntimeException("Unexpected character " + c);
        }
      }
    }
    
    //Helper.printBitmap(bitmap, '.', '#');
    long steps = 26501365;
    long cycle = lines.get(0).length();
    long stepsNeeded = cycle * 2 + cycle / 2;
    Map<Long, Long> answerMap = compute(bitmap, oddBitmapMap, evenBitmapMap, stepsNeeded, startingPosition);
    //System.out.println(answerMap);
    
    long p[] = new long[3];
    for (int i = 0; i < 3; i++) {
      p[i] = answerMap.get(cycle / 2 + i * cycle);
      System.out.println("p[" + i + "]=" + p[i]);
    }
    
    // Ax^2 + Bx + C = p[0] for x = 0
    // for x = 0: 0A + 0B + C = p[0]
    //            C = p[0]
    // for x = 1: 1A + 1B + p[0] = p[1]
    //            B = p[1] - p[0] - A
    // for x = 2: 4A + 2B + p[0] = p[2]
    //            A = (p[2] - 2B - p[0]) / 4
    //
    //            B = p[1] - p[0] - ((p[2] - 2B - p[0]) / 4)
    //           4B = 4p[1] - 4p[0] - p[2] + 2B + p[0]
    //           2B = 4p[1] - p[2] - 3p[0]
    //            B = (4p[1] - p[2] - 3p[0]) / 2
    //
    //            A = (p[2] - (4p[1] - p[2] - 3p[0]) - p[0]) / 4
    //              = (p[2] - 4p[1] + p[2] +3p[0] - p[0]) / 4
    //              = (2p[2] - 4p[1] + 2p[0]) / 4
    //              = (p[2] - 2p[1] + p[0]) / 2
    long A = (p[2] - 2*p[1] + p[0]) / 2; 
    long B = (4*p[1] - p[2] -3*p[0]) / 2;
    long C = p[0];
    
    long s = (steps - (cycle / 2)) / cycle;
    
    System.out.println("y = " + A + "x^2 + " + B + "x + " + C);
    long answer = s * s * A + B * s + C;
 
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
