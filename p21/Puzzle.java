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
  
   public static long calculate(String s) {
    return 1;
  }
  
  private static long count(boolean[][] bitmap) {
    long counter = 0;
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        if (bitmap[i][j]) counter++;
      }
    }
    return counter;
  }
  
  private static boolean isWalkable(boolean [][] bitmap, Position position) {
    if (position.x() < 0 || position.x() >= bitmap[0].length) return false;
    if (position.y() < 0 || position.y() >= bitmap.length) return false;
    return bitmap[position.y()][position.x()];
  }
  
  private static void compute(boolean [][] pathBitmap, boolean [][] oddBitmap, boolean [][] evenBitmap, long steps, Position startingPosition) {
    LinkedList<Position> positions = new LinkedList<>();
    positions.add(startingPosition);
    
    long currentStep = 0;
    while (!positions.isEmpty() && currentStep < steps) {
      currentStep++;
      boolean [][] targetBitmap = (currentStep % 2 == 0) ? evenBitmap : oddBitmap;
      LinkedList<Position> positionsToCheck = new LinkedList<>(positions);
      positions.clear();
      while (!positionsToCheck.isEmpty()) {
        Position cp = positionsToCheck.poll();
        Position north = new Position(cp.x(), cp.y() - 1);
        Position south = new Position(cp.x(), cp.y() + 1);
        Position west = new Position(cp.x() - 1, cp.y());
        Position east = new Position(cp.x() + 1, cp.y());
        if (isWalkable(pathBitmap, north) && !targetBitmap[north.y()][north.x()]) {
          targetBitmap[north.y()][north.x()] = true;
          positions.addLast(north);
        }
        if (isWalkable(pathBitmap, south) && !targetBitmap[south.y()][south.x()]) {
          targetBitmap[south.y()][south.x()] = true;
          positions.addLast(south);
        }
        if (isWalkable(pathBitmap, west) && !targetBitmap[west.y()][west.x()]) {
          targetBitmap[west.y()][west.x()] = true;
          positions.addLast(west);
        }
        if (isWalkable(pathBitmap, east) && !targetBitmap[east.y()][east.x()]) {
          targetBitmap[east.y()][east.x()] = true;
          positions.addLast(east);
        }
      }
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p21/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Position startingPosition = null;
    boolean [][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    boolean [][] oddBitmap = new boolean[lines.size()][lines.get(0).length()];
    boolean [][] evenBitmap = new boolean[lines.size()][lines.get(0).length()];
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
    long steps = 10;
    compute(bitmap, oddBitmap, evenBitmap, steps, startingPosition);
    long answer = count(steps % 2 == 0 ? evenBitmap : oddBitmap);
   // Helper.printBitmap(evenBitmap, 'o', '.');
    /*
    long answer = lines.stream()
        .map(Puzzle::calculate)
        .reduce(0L, Long::sum);
        */
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
