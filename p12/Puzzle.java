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
  
  private static int sumList(List<Integer> list) {
    int total = 0;
    for (Integer value : list) {
      total += value;
    }
    return total;
  }
  
  private static int calculatePermutations(String springs, List<Integer> groupings) {
    System.out.println(springs + " " + groupings);
    
    if (groupings.isEmpty()) {
      for (int i = 0; i < springs.length(); i++) {
        if (springs.charAt(i) == '#') {
          System.out.println("\t e1 " + 0);
          return 0;
        }
      }
      System.out.println("\t e2 " + 1);
      return 1;
    }
    
    if (springs.length() < sumList(groupings) + groupings.size() - 1) {
      System.out.println("\t e3 " + 0);
      return 0;
    }

    if (springs.isEmpty()) return 0;
    int groupSize = groupings.get(0);
    if (springs.length() < groupSize) return 0;
    
    boolean cutOk = true;
    for (int i = 0; i < groupSize; i++) {
      char c = springs.charAt(i);
      if (c == '.') {
        cutOk = false;
        break;
      }
    }
    
    int total = 0;
    if (cutOk) {
      if (springs.length() > groupSize) {
        if (springs.charAt(groupSize) != '#') {
          String newSprings = springs.length() >= groupSize + 1 ? springs.substring(groupSize + 1) : "";
          List<Integer> newGroupings = groupings.size() > 1 ? groupings.subList(1, groupings.size()) : List.of();
          total += calculatePermutations(newSprings, newGroupings);
        }
      } else {
        total += 1;
      }
    }
    
    if (springs.charAt(0) != '#') {
      String newSprings = springs.length() > 1 ? springs.substring(1) : "";
      total += calculatePermutations(newSprings, groupings);
    }
    
    System.out.println("\t r " + total);
    return total;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p12/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<String> springsList = new ArrayList<>();
    List<List<Integer>> groupingsList = new ArrayList<>();
    for (String line : lines) {
      String[] split1 = line.split("\\s+");
      String[] split2 = split1[1].split(",");
      springsList.add(split1[0]);
      List<Integer> grouping = new ArrayList<>();
      for (String group : split2) {
        grouping.add(Integer.parseInt(group.trim()));
      }
      groupingsList.add(grouping);
    }
    
    int permutations = 0;
    for (int i = 0; i < springsList.size(); i++) {
      permutations += calculatePermutations(springsList.get(i), groupingsList.get(i));
    }
    System.out.println("permutations is " + permutations);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
