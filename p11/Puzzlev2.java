package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  private static class Position {
    public int x;
    public int y;
    
    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    public String toString() {
      return "P(" + x + ", " + y + ")";
    }
  }

  private static boolean isAllEmpty(List<String> lines, int position) {
    for (String line : lines) {
      if (line.charAt(position) != '.') {
        return false;
      }
    }
    return true;
  }
  
  private static long findBetween(List<Integer> values, int start, int end) {
    long total = 0;
    for (Integer value : values) {
      if (value > start && value < end) {
        total++;
      }
    }
    return total;
  }
  
  private static long computeDistance(Position p1, Position p2, List<Integer> emptyRows, List<Integer> emptyCols) {
    long rows = findBetween(emptyRows, Math.min(p1.y, p2.y), Math.max(p1.y, p2.y));
    long cols = findBetween(emptyCols, Math.min(p1.x, p2.x), Math.max(p1.x, p2.x));
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + (rows + cols) * (1000000 - 1);
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p11/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern allEmptyPattern = Pattern.compile("^(\\.+)$");
    List<Integer> emptyRows = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (allEmptyPattern.matcher(line).find()) {
        emptyRows.add(i);
      }
    }
    
    List<Integer> emptyCols = new ArrayList<>();
    for (int i = 0; i < lines.get(0).length(); i++) {
      if (isAllEmpty(lines, i)) {
        emptyCols.add(i);
      }
    }
    
    List<Position> galaxyPositions = new ArrayList<>();
    for (int j = 0; j < lines.size(); j++) {
      for (int i = 0; i < lines.get(0).length(); i++) {
        if (lines.get(j).charAt(i) == '#') {
          galaxyPositions.add(new Position(i, j));
        }
      }
    }
    
    long distance = 0;
    for (int i = 0; i < galaxyPositions.size(); i++) {
      for (int j = i; j < galaxyPositions.size(); j++) {
        distance += computeDistance(galaxyPositions.get(i), galaxyPositions.get(j), emptyRows, emptyCols);
      }
    }
    
    System.out.println("distance is " + distance);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
