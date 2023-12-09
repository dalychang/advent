package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  
  static void goUpForward(List<List<Long>> totals, int level) {
    if (level == 0) return;
    goUpForward(totals, level - 1);
    List<Long> previousLevel = totals.get(level - 1);
    List<Long> currentLevel = totals.get(level);
    totals.get(level).add(previousLevel.get(currentLevel.size() + 1) - previousLevel.get(currentLevel.size()));
  }
  
  static void goUp(List<List<Long>> totals, int level) {
    if (level == 0) return;
    goUp(totals, level - 1);
    List<Long> previousLevel = totals.get(level - 1);
    List<Long> currentLevel = totals.get(level);
    totals.get(level).add(previousLevel.get(currentLevel.size()) - previousLevel.get(currentLevel.size() + 1));
  }
  
  static void doFindNextValue(List<List<Long>> totals, int level) {
    if (totals.size() < level + 1) {
      totals.add(new ArrayList<>());
    }
    
    if (totals.get(level).isEmpty()) {
      goUp(totals, level);
    }
    
    if (totals.get(level).get(0) != 0) {
      doFindNextValue(totals, level + 1);
    }
  }
  
  static long findNextValue(List<Long> numbers, int levels) {
    List<List<Long>> totals = new ArrayList<>();
    totals.add(numbers);
    
    for (int i = 0; i < levels; i++) {
      totals.add(new ArrayList<>());
      goUpForward(totals, i);
    }
    
    long number = 0;
    for (int i = totals.size() - 1; i >= 0; i--) {
      if (totals.get(i).isEmpty()) continue;
      number = totals.get(i).get(0) - number;
    }
    
    return number;
  }
  
  static long findLevels(List<Long> numbers) {
    List<List<Long>> totals = new ArrayList<>();
    totals.add(numbers);
    
    doFindNextValue(totals, 1);
    
    long number = 0;
    for (int i = totals.size() - 1; i >= 0; i--) {
      number = totals.get(i).get(0) - number;
    }
    
    return totals.size();
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p9/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<List<Long>> forwardNumbersList = new ArrayList<>();
    List<List<Long>> reverseNumbersList = new ArrayList<>();
    
    for (String line : lines) {
      String[] splitString = line.split("\\s+");
      List<Long> numbers = new ArrayList<>();
      for (String s : splitString) {
        numbers.add(Long.parseLong(s));
      }
      forwardNumbersList.add(List.copyOf(numbers));
      Collections.reverse(numbers);
      reverseNumbersList.add(numbers);
    }
    
    List<Integer> levels = new ArrayList<>();
    for (List<Long> numbers : reverseNumbersList) {
      levels.add((int)findLevels(numbers));
    }
    
    long total = 0;
    for (int i = 0; i < forwardNumbersList.size(); i++) {
      List<Long> forwardNumbers = forwardNumbersList.get(i);
      long num = findNextValue(forwardNumbers, levels.get(i));
      total += num;
    }
    System.out.println("total is " + total);
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
