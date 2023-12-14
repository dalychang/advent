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
  
  private static long calculateWeight(String s) {
    int weight = s.length();
    long totalWeight = 0;
    long rocks = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == 'O') {
        totalWeight += weight;
        weight--;
      } else if (s.charAt(i) == '#') {
        weight = s.length() - i - 1;
      }
    }

    return totalWeight;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p14/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    
    List<String> rotatedLines = new ArrayList<>();
    for (int i = 0; i < lines.get(0).length(); i++) {
      rotatedLines.add("");
    }
    for (String line : lines) {
      for (int i = 0; i < lines.get(0).length(); i++) {
        rotatedLines.set(i, rotatedLines.get(i) + line.charAt(i));
      }
    }
    
    long weight = 0;
    for (String line : rotatedLines) {
      //System.out.println(line);
      weight += calculateWeight(line);
    }
    System.out.println("weight is " + weight);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
