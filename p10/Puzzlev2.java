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
  
  public static void floodFill(boolean bitmap[][], int x, int y) {
    Queue<Position> positions = new LinkedList<>();
    positions.add(new Position(x, y));
    
    while (!positions.isEmpty()) {
      Position p = positions.poll();
      if (p.x < 0 || p.x >= bitmap[0].length) continue;
      if (p.y < 0 || p.y >= bitmap.length) continue;
      if (bitmap[p.y][p.x]) continue;

      bitmap[p.y][p.x] = true;
      positions.add(new Position(p.x - 1, p.y));
      positions.add(new Position(p.x + 1, p.y));
      positions.add(new Position(p.x, p.y - 1));
      positions.add(new Position(p.x, p.y + 1));
    }
  }
  
  public static void floodFillEdges(boolean bitmap[][]) {
    for (int i = 0; i < bitmap.length; i++) {
      floodFill(bitmap, 0, i);
      floodFill(bitmap, bitmap[0].length - 1, i);
    }
    for (int i = 0; i < bitmap[0].length; i++) {
      floodFill(bitmap, i, 0);
      floodFill(bitmap, i, bitmap.length - 1);
    }
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
    
    boolean bitmap[][] = new boolean[grid.size()][grid.get(0).size()];
    List<Position> positions = new ArrayList<>();
    positions.add(new Position(startX, startY));
    
    bitmap[startY][startX] = true;
    int distance = traverse(grid, bitmap, positions);
    
    // The exact starting character is not important, just how it connects down and right.
    Character startChar = 'F';
    boolean connectsRight = false;
    boolean connectsDown = false;
    if (startX + 1 < bitmap[0].length && bitmap[startY][startX + 1]) {
      Character c = grid.get(startY).get(startX + 1);
      if (c == '-' || c == '7' || c == 'J') {
        connectsRight = true;
      }
    }
    if (startY + 1 < bitmap.length && bitmap[startY + 1][startX]) {
      Character c = grid.get(startY + 1).get(startX);
      if (c == '|' || c == 'L' || c == 'J') {
        connectsDown = true;
      }
    }
    if (connectsRight && connectsDown) {
      startChar = 'F';
    } else if (connectsRight) {
      startChar = '-';
    } else if (connectsDown) {
      startChar = '|';
    } else {
      startChar = 'J';
    }
    
    boolean expandedBitmap[][] = new boolean[grid.size() * 2][grid.get(0).size() * 2];
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(0).size(); j++) {
        if (bitmap[i][j]) {
          expandedBitmap[i * 2][j * 2] = true;
          Character c = grid.get(i).get(j);
          if (c == 'S') {
            c = startChar;
          }
          switch (c) {
            case '-':
            case 'L':
              expandedBitmap[i * 2][j * 2 + 1] = true;
              break;
            case '|':
            case '7':
              expandedBitmap[i * 2 + 1][j * 2] = true;
              break;
            case 'F':
              expandedBitmap[i * 2][j * 2 + 1] = true;
              expandedBitmap[i * 2 + 1][j * 2] = true;
              break;
            case 'J':
            default:
              break;
          }
        }
      }
    }
    
    floodFillEdges(expandedBitmap);
    for (int i = 0; i < expandedBitmap.length; i++) {
      for (int j = 0; j < expandedBitmap[i].length; j++) {
        System.out.print(expandedBitmap[i][j] ? "X" : ".");
      }
      System.out.println("");
    }
    
    int insideSpots = 0;
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[i].length; j++) {
        if (!bitmap[i][j] && !expandedBitmap[i * 2][j * 2]) {
          insideSpots++;
        }
      }
    }
    
    System.out.println("insideSpots is " + insideSpots);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
