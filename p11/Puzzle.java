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

public class Puzzle {
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
  
  private static void insertEmpty(List<String> lines, int position) {
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      lines.set(i, line.substring(0, position) + "." + line.substring(position));
    }
  }
  
  private static long computeDistance(Position p1, Position p2) {
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p11/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern allEmptyPattern = Pattern.compile("^(\\.+)$");
    List<String> updatedLines = new ArrayList<>();
    for (String line : lines) {
      updatedLines.add(line);
      if (allEmptyPattern.matcher(line).find()) {
        updatedLines.add(line);
      }
    }
    
    for (int i = updatedLines.get(0).length() - 1; i >= 0; i--) {
      if (isAllEmpty(updatedLines, i)) {
        insertEmpty(updatedLines, i);
      }
    }
    
    List<Position> galaxyPositions = new ArrayList<>();
    for (int j = 0; j < updatedLines.size(); j++) {
      for (int i = 0; i < updatedLines.get(0).length(); i++) {
        if (updatedLines.get(j).charAt(i) == '#') {
          galaxyPositions.add(new Position(i, j));
        }
      }
    }
    
    long distance = 0;
    for (int i = 0; i < galaxyPositions.size(); i++) {
      for (int j = i; j < galaxyPositions.size(); j++) {
        distance += computeDistance(galaxyPositions.get(i), galaxyPositions.get(j));
      }
    }
    
    /*for (String line : updatedLines) {
      System.out.println(line);
    }*/
    System.out.println("distance is " + distance);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
