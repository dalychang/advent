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
  
  static long findUpperBound(long time, long distance) {
    long lowerBound = 0;
    long upperBound = time;
    
    while (true) {
      if (upperBound - lowerBound <= 3) {
        for (long i = upperBound; i >= lowerBound; i--) {
          if ((time - i) * i > distance) {
            return i;
          }
        }
      }
      
      long currentTry = (upperBound - lowerBound) / 2 + lowerBound;
      //System.out.println(String.format("findUpperBound() currentTry %d lowerBound %d upperBound %d", currentTry, lowerBound, upperBound));
      long currentDistance = (time - currentTry) * currentTry;
      if (currentDistance < distance) {
        upperBound = (long)Math.ceil(((double)upperBound - lowerBound) / 2) + lowerBound;
      } else {
        lowerBound = (long)Math.floor(((double)upperBound - lowerBound) / 2) + lowerBound;       
      }
    }
  }
  
  static long findLowerBound(long time, long distance) {
    long lowerBound = 0;
    long upperBound = time;
        
    while (true) {
      if (upperBound - lowerBound <= 3) {
        for (long i = lowerBound; i <= upperBound; i++) {
          if ((time - i) * i > distance) {
            return i;
          }
        }
      }
      
      long currentTry = (upperBound - lowerBound) / 2 + lowerBound;
      //System.out.println(String.format("findLowerBound() currentTry %d lowerBound %d upperBound %d", currentTry, lowerBound, upperBound));
      long currentDistance = (time - currentTry) * currentTry;
      if (currentDistance < distance) {
        lowerBound = (long)Math.floor(((double)upperBound - lowerBound) / 2) + lowerBound;
      } else {
        upperBound = (long)Math.ceil(((double)upperBound - lowerBound) / 2) + lowerBound;
      }
    }
  }
  
  static long findWays(long time, long distance) {
    long upper = findUpperBound(time, distance);
    long lower = findLowerBound(time, distance);
    long delta = upper - lower + 1;
    System.out.println(String.format("lower %d upper %d delta %d", lower, upper, delta));
    return delta;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p6/input2.txt");
    
    Clock clock = Clock.systemUTC();
    
    long startTime = clock.millis();
    
    List<Integer> times = new ArrayList<>();
    List<Integer> distances = new ArrayList<>();
    
    String timeString = lines.get(0).trim().replace("Time:", "").replace(" ", "");
    String distanceString = lines.get(1).trim().replace("Distance:", "").replace(" ", "");
    
    long time = Long.parseLong(timeString);
    long distance = Long.parseLong(distanceString);
    
    long total = findWays(time, distance);
    System.out.println("total is " + total);
    
    long endTime = clock.millis();
    
    System.out.println("time taken " + (endTime - startTime) + "ms");
  }
}
