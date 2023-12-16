package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
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
  };
  
  public static class Beam {
    public int x;
    public int y;
    public Direction direction;
    
    public Beam(int x, int y, Direction direction) {
      this.x = x;
      this.y = y;
      this.direction = direction;
    }
    
    public String toString() {
      return "Beam(" + x + ", " + y + ", " + direction.name() + ")";
    }
    
    @Override 
    public boolean equals(Object o) {
      if (o == this)
        return true;
      if (!(o instanceof Beam))
        return false;
      Beam b = (Beam)o;
      return b.x == x && b.y == y
          && b.direction == direction;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(x, y, direction);
    }
  }
    
  
   public static long calculate(String s) {
    return 1;
  }
  
  public static void simulate(LinkedList<Beam> beams, boolean[][] bitmap, List<String> map, Set<Beam> seenBeams) {
    while (!beams.isEmpty()) {
      Beam beam = beams.removeFirst();
      if (seenBeams.contains(beam)) continue;
      seenBeams.add(beam);
      //System.out.println(beam);
      if (beam.x < 0 || beam.x >= map.get(0).length()) continue;
      if (beam.y < 0 || beam.y >= map.size()) continue;
      
      char c = map.get(beam.y).charAt(beam.x);
      bitmap[beam.y][beam.x] = true;
      switch (c) {
        case '|':
          if (beam.direction == Direction.NORTH || beam.direction == Direction.SOUTH) {
            beams.addLast(new Beam(beam.x, beam.y + beam.direction.dy, beam.direction));
          } else if (beam.direction == Direction.EAST || beam.direction == Direction.WEST) {
            beams.addLast(new Beam(beam.x, beam.y + 1, Direction.SOUTH));
            beams.addLast(new Beam(beam.x, beam.y - 1, Direction.NORTH));
          }
          break;
        case '-':
          if (beam.direction == Direction.NORTH || beam.direction == Direction.SOUTH) {
            beams.addLast(new Beam(beam.x + 1, beam.y, Direction.EAST));
            beams.addLast(new Beam(beam.x - 1, beam.y, Direction.WEST));
          } else if (beam.direction == Direction.EAST || beam.direction == Direction.WEST) {
            beams.addLast(new Beam(beam.x + beam.direction.dx, beam.y, beam.direction));
          }
          break;
        case '\\':
          {
            Direction newBeamDirection = beam.direction;
            switch (beam.direction) {
              case NORTH:
                newBeamDirection = Direction.WEST;
                break;
              case WEST:
                newBeamDirection = Direction.NORTH;
                break;  
              case SOUTH:
                newBeamDirection = Direction.EAST;
                break;
              case EAST:
                newBeamDirection = Direction.SOUTH;
                break;
            }
            beams.addLast(new Beam(beam.x + newBeamDirection.dx, beam.y + newBeamDirection.dy, newBeamDirection));
          }
          break;
        case '/':
          {
            Direction newBeamDirection = beam.direction;
            switch (beam.direction) {
              case NORTH:
                newBeamDirection = Direction.EAST;
                break;
              case EAST:
                newBeamDirection = Direction.NORTH;
                break;  
              case SOUTH:
                newBeamDirection = Direction.WEST;
                break;
              case WEST:
                newBeamDirection = Direction.SOUTH;
                break;
            }
            beams.addLast(new Beam(beam.x + newBeamDirection.dx, beam.y + newBeamDirection.dy, newBeamDirection));
          }
          break;
        case '.':
        default:
          beams.addLast(new Beam(beam.x + beam.direction.dx, beam.y + beam.direction.dy, beam.direction));
          break;
      }
      
      //printBitmap(bitmap);
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p16/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    for (String line : lines) {
      System.out.println(line);
    }
    
    boolean[][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    Set<Beam> seenBeams = new HashSet<>();
    
    LinkedList<Beam> beams = new LinkedList<>();
    beams.add(new Beam(0, 0, Direction.EAST));
    simulate(beams, bitmap, lines, seenBeams);
    
    long total = 0;
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        if (bitmap[i][j]) {
          total++;
          System.out.print("X");
        } else {
          System.out.print(".");
        }
      }
      System.out.println("");
    }
        
    System.out.println("total is " + total);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  private static void printBitmap(boolean[][] bitmap) {
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        if (bitmap[i][j]) {
          System.out.print("X");
        } else {
          System.out.print(".");
        }
      }
      System.out.println("");
    }
    System.out.println("");
  }
}
