package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev3 {
  
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
  
  public static boolean check(List<List<Character>> grid, boolean bitmap[][], Position fromPosition, int toX, int toY) {
    if (toX < 0 || toX >= bitmap[0].length) return false;
    if (toY < 0 || toY >= bitmap.length) return false;
    if (bitmap[toY][toX]) return false;
    
    Character letter = grid.get(toY).get(toX);
    if (fromPosition.x > toX) {
      return letter == 'L' || letter == 'F' || letter == '-';
    } else if (fromPosition.x < toX) {
      return letter == 'J' || letter == '7' || letter == '-';
    } else if (fromPosition.y > toY) {
      return letter == '7' || letter == 'F' || letter == '|';
    } else if (fromPosition.y < toY) {
      return letter == 'J' || letter == 'L' || letter == '|';
    } else {
      return false;
    }
  }
  
  public static void process(List<List<Character>> grid, boolean bitmap[][], List<Position> newPositions, Position position, int x, int y) {
    if (check(grid, bitmap, position, x, y)) {
      bitmap[y][x] = true;
      newPositions.add(new Position(x, y));
    }
  }
  
  public static int traverse(List<List<Character>> grid, boolean bitmap[][], List<Position> positions) {
    List<Position> originalPositions = positions;
    List<Position> newPositions = new ArrayList<>();

    int distance = 0;    
    while (!originalPositions.isEmpty()) {
      distance++;
      for (Position op : originalPositions) {
        switch (grid.get(op.y).get(op.x)) {
          case '|':
            process(grid, bitmap, newPositions, op, op.x, op.y - 1);
            process(grid, bitmap, newPositions, op, op.x, op.y + 1);
            break;
          case '-':
            process(grid, bitmap, newPositions, op, op.x - 1, op.y);
            process(grid, bitmap, newPositions, op, op.x + 1, op.y);
            break;
          case '7':
            process(grid, bitmap, newPositions, op, op.x - 1, op.y);
            process(grid, bitmap, newPositions, op, op.x, op.y + 1);
            break;  
          case 'L':
            process(grid, bitmap, newPositions, op, op.x + 1, op.y);
            process(grid, bitmap, newPositions, op, op.x, op.y - 1);
            break;
          case 'J':
            process(grid, bitmap, newPositions, op, op.x - 1, op.y);
            process(grid, bitmap, newPositions, op, op.x, op.y - 1);
            break;
          case 'F':
            process(grid, bitmap, newPositions, op, op.x + 1, op.y);
            process(grid, bitmap, newPositions, op, op.x, op.y + 1);
            break;
          case 'S':
            process(grid, bitmap, newPositions, op, op.x + 1, op.y);
            process(grid, bitmap, newPositions, op, op.x - 1, op.y);
            process(grid, bitmap, newPositions, op, op.x, op.y - 1);
            process(grid, bitmap, newPositions, op, op.x, op.y + 1);
            break;  
          default:
            break;
        }
      }
      originalPositions = newPositions;
      newPositions = new ArrayList<>();
    }

    return distance - 1;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p10/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int startX = 0;
    int startY = 0;
    List<List<Character>> grid = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      List<Character> charLine = new ArrayList<>();
      for (int j = 0; j < line.length(); j++) {
        Character c = line.charAt(j);
        charLine.add(c);
        if (c == 'S') {
          startX = j;
          startY = i;
        }
      }
      grid.add(charLine);
    }
    
    // Replace s.
    boolean west = startX > 0 ? grid.get(startY).get(startX - 1) == 'F' || grid.get(startY).get(startX - 1) == 'L' || grid.get(startY).get(startX - 1) == '-' : false;
    boolean east = startX < grid.get(startY).size() - 1 ? grid.get(startY).get(startX + 1) == 'J' || grid.get(startY).get(startX + 1) == '7' || grid.get(startY).get(startX + 1) == '-' : false;
    boolean north = startY > 0 ? grid.get(startY - 1).get(startX) == 'F' || grid.get(startY - 1).get(startX) == '7' || grid.get(startY - 1).get(startX) == '|' : false;
    boolean south = startY < grid.size() ? grid.get(startY + 1).get(startX) == 'F' || grid.get(startY + 1).get(startX) == '7' || grid.get(startY + 1).get(startX) == '|' : false;
    char startChar = 'S';
    if (west && east) {
      startChar = '-';
    } else if (north && south) {
      startChar = '|';
    } else if (north && east) {
      startChar = 'L';
    } else if (north && west) {
      startChar = 'J';
    } else if (south && east) {
      startChar = 'F';
    } else if (south && west) {
      startChar = '7';
    }
    grid.get(startY).set(startX, startChar);
    System.out.println("startChar is " + startChar);

    boolean bitmap[][] = new boolean[grid.size()][grid.get(0).size()];
    List<Position> positions = new ArrayList<>();
    positions.add(new Position(startX, startY));
    
    bitmap[startY][startX] = true;
    int distance = traverse(grid, bitmap, positions);
    
    //Helper.printBitmap(bitmap, 'X', '.');
    
    long insideSpots = 0;
    for (int i = 0; i < bitmap.length; i++) {
      int pipes = 0;
      for (int j = 0; j < bitmap[0].length; j++) {
        if (bitmap[i][j]) {
          Character c = grid.get(i).get(j);
          switch (c) {
            case 'L':
            case '7':
              pipes += 1;
              break;
            case 'J':
            case 'F':
              pipes = (pipes + 3) % 4;
              break;
            case '|':
              pipes += 2;
              break;
            case '-':
            default:
              break;
          }
        } else {
          int r = pipes % 4;
          if (r == 2) {
            insideSpots++;
          }
        }
      }
    }
    
    System.out.println("insideSpots is " + insideSpots);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
