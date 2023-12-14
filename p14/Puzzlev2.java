package dev.advent;

import com.google.common.base.Joiner;
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
  
  private static String makeKey(List<String> list) {
    return Joiner.on(';').join(list);
  }
  
  private static List<String> rotateAndRoll(Map<String, List<String>> cache, List<String> list) {
    final String key = makeKey(list);
    if (cache.containsKey(key)) {
      return cache.get(key);
    } 

    List<String> rotatedLines = rotateRight(list);
    for (int j = 0; j < rotatedLines.size(); j++) {
      rotatedLines.set(j, roll(rotatedLines.get(j)));
    }

    cache.put(key, rotatedLines);
    return rotatedLines;
  }
  
  private static List<String> rotateLeft(List<String> list) {
    List<String> rotatedLines = new ArrayList<>();
    for (int i = 0; i < list.get(0).length(); i++) {
      rotatedLines.add("");
    }
    for (String line : list) {
      for (int i = 0; i < list.get(0).length(); i++) {
        rotatedLines.set(i, rotatedLines.get(i) + line.charAt(list.get(0).length() - i - 1));
      }
    }
    return rotatedLines;
  }
  
  private static List<String> rotateRight(List<String> list) {
    List<String> rotatedLines = new ArrayList<>();
    for (int i = 0; i < list.get(0).length(); i++) {
      rotatedLines.add("");
    }
    for (int i = 0; i < list.get(0).length(); i++) {
      for (int j = list.size() - 1; j >= 0; j--) {
        rotatedLines.set(i, rotatedLines.get(i) + list.get(j).charAt(i));
      }
    }

    return rotatedLines;
  }
  
  private static String roll(String s) {
    StringBuilder sb = new StringBuilder();
    int rocks = 0;
    int spaces = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == 'O') {
        rocks++;
      } else if (c == '.') {
        spaces++;
      } else if (c == '#') {
        sb.append("O".repeat(rocks));
        sb.append(".".repeat(spaces));
        sb.append("#");
        rocks = 0;
        spaces = 0;
      }
    }
    sb.append("O".repeat(rocks));
    sb.append(".".repeat(spaces));
    return sb.toString();
  }
  
  private static long calculateWeight(String s) {
    int weight = s.length();
    long totalWeight = 0;
    long rocks = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == 'O') {
        totalWeight += (s.length() - i);
        weight--;
      } else if (s.charAt(i) == '#') {
        weight = s.length() - i - 1;
      }
    }
    return totalWeight;
  }
  
  private static void printLines(List<String> lines) {
    for (String line : lines) {
      System.out.println(line);
    }
    System.out.println("");
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p14/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<String> rotatedLines = rotateLeft(rotateLeft(lines));
    Map<String, List<String>> cache = new HashMap<>();
    final int ITERATIONS = 1000000000;

    int previousCacheSize = 0;
    boolean stableCache = false;
    String previousKey = "";
    int previousIndex = 0;
    
    for (int i = 0; i < ITERATIONS; i++) {
      for (int j = 0; j < 4; j++) {
        rotatedLines = rotateAndRoll(cache, rotatedLines);
      }
      
      if (stableCache) {
        if (previousKey.equals(makeKey(rotatedLines))) {
          int delta = i - previousIndex;
          int extraCycles = (ITERATIONS - i) / delta;
          i += (extraCycles * delta);
        }
      }
      
      if (previousCacheSize == cache.size() && !stableCache) {
        stableCache = true;
        previousIndex = i;
        previousKey = makeKey(rotatedLines);
      } else {
        previousCacheSize = cache.size();
      }
    }

    long weight = determineWeight(rotatedLines);

    System.out.println("weight is " + weight);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  private static long determineWeight(List<String> list) {
    List<String> rotatedLines = rotateRight(list);
    long weight = 0;
    for (String line : rotatedLines) {
      //System.out.println(line);
      weight += calculateWeight(line);
    }
    return weight;
  }
}
