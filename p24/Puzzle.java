package dev.advent;

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
  public record Position(long x, long y, long z) {}
  public record Velocity(long x, long y, long z) {}
  public static class Hail {
    private final Position position;
    private final Velocity velocity;
    
    private final double slope;
    
    public Hail(Position position, Velocity velocity) {
      this.position = position;
      this.velocity = velocity;
      
      slope = (double) velocity.y() / velocity.x();
    }
    
    private boolean pointInFuture(double x, double y) {
      return (position.x() < x && velocity.x() > 0) || (position.x() > x && velocity.x() < 0)
          && (position.y() < y && velocity.y() > 0) || (position.y() > y && velocity.y() < 0);
    }
    
    private double A() {
      return slope;
    }
    
    private double B() {
      return -1;
    }
    
    private double C() {
      return position.y() - slope * position.x();
    }
    
    public boolean collidesWith(Hail hail, long minBound, long maxBound) {
      double xIntersect = (hail.B()*C() - B()*hail.C()) / (hail.A()*B() - A()*hail.B());
      double yIntersect = (hail.C()*A() - C()*hail.A()) / (hail.A()*B() - A()*hail.B());
      //System.out.println(position() + " x " + hail.position() + " = (" + xIntersect + ", " + yIntersect + ")");
      boolean intersectionWithinBounds = (xIntersect >= minBound && xIntersect <= maxBound && yIntersect >= minBound && yIntersect <= maxBound);
      return intersectionWithinBounds && pointInFuture(xIntersect, yIntersect) && hail.pointInFuture(xIntersect, yIntersect);
    }
    
    public Position position() { return position; }
    public Velocity velocity() { return velocity; }
    public double slope() { return slope; }
    
    public String toString() {
      return "Hail(position=" + position + " velocity=" + velocity + " slope=" + slope + ")";
    }
  }
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static long countCollisions(List<Hail> hailList, long minBound, long maxBound) {
    int count = 0;
    for (int i = 0; i < hailList.size(); i++) {
      for (int j = i + 1; j < hailList.size(); j++) {
        Hail h1 = hailList.get(i);
        Hail h2 = hailList.get(j);
        if (h1.collidesWith(h2, minBound, maxBound)) {
          count++;
        }
      }
    }
    return count;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p24/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern hailPattern = Pattern.compile("^(\\-?\\d+),\\s+(\\-?\\d+),\\s+(\\-?\\d+)\\s+@\\s+(\\-?\\d+),\\s+(\\-?\\d+),\\s+(\\-?\\d+)$");
    
    List<Hail> hailList = new ArrayList<>();
    
    for (String line : lines) {
      Matcher m = hailPattern.matcher(line);
      m.find();
      hailList.add(new Hail(new Position(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)), Long.parseLong(m.group(3))),
          new Velocity(Long.parseLong(m.group(4)), Long.parseLong(m.group(5)), Long.parseLong(m.group(6)))));
    }

    long answer = countCollisions(hailList, 200000000000000L, 400000000000000L);
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
