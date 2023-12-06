package dev.advent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
  
  static int findUpperBound(int time, int distance) {
    int lowerBound = 0;
    int upperBound = time;
    
    while (true) {
      if (upperBound - lowerBound <= 3) {
        for (int i = upperBound; i >= lowerBound; i--) {
          if ((time - i) * i > distance) {
            return i;
          }
        }
      }
      
      int currentTry = (upperBound - lowerBound) / 2 + lowerBound;
      //System.out.println(String.format("findUpperBound() currentTry %d lowerBound %d upperBound %d", currentTry, lowerBound, upperBound));
      int currentDistance = (time - currentTry) * currentTry;
      if (currentDistance < distance) {
        upperBound = (int)Math.ceil(((double)upperBound - lowerBound) / 2) + lowerBound;
      } else {
        lowerBound = (int)Math.floor(((double)upperBound - lowerBound) / 2) + lowerBound;       
      }
    }
  }
  
  static int findLowerBound(int time, int distance) {
    int lowerBound = 0;
    int upperBound = time;
        
    while (true) {
      if (upperBound - lowerBound <= 3) {
        for (int i = lowerBound; i <= upperBound; i++) {
          if ((time - i) * i > distance) {
            return i;
          }
        }
      }
      
      int currentTry = (upperBound - lowerBound) / 2 + lowerBound;
      //System.out.println(String.format("findLowerBound() currentTry %d lowerBound %d upperBound %d", currentTry, lowerBound, upperBound));
      int currentDistance = (time - currentTry) * currentTry;
      if (currentDistance < distance) {
        lowerBound = (int)Math.floor(((double)upperBound - lowerBound) / 2) + lowerBound;
      } else {
        upperBound = (int)Math.ceil(((double)upperBound - lowerBound) / 2) + lowerBound;
      }
    }
  }
  
  static int findWays(int time, int distance) {
    int upper = findUpperBound(time, distance);
    int lower = findLowerBound(time, distance);
    int delta = upper - lower + 1;
    System.out.println(String.format("lower %d upper %d delta %d", lower, upper, delta));
    return delta;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p6/input2.txt");
    
    List<Integer> times = new ArrayList<>();
    List<Integer> distances = new ArrayList<>();
    
    String[] timeStrings = lines.get(0).trim().split("\\s+");
    String[] distanceStrings = lines.get(1).trim().split("\\s+");
    for (int i = 1; i < timeStrings.length; i++) {
      times.add(Integer.parseInt(timeStrings[i]));
      distances.add(Integer.parseInt(distanceStrings[i]));
    }
    
    int total = 1;
    for (int i = 0; i < times.size(); i++) {
      total *= findWays(times.get(i), distances.get(i));
    }
    System.out.println("total is " + total);
  }
}
