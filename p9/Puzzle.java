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

public class Puzzle {
  
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
  
  static long findNextValue(List<Long> numbers) {
    List<List<Long>> totals = new ArrayList<>();
    totals.add(numbers);
    
    doFindNextValue(totals, 1);
    
    //System.out.println(totals);
    long number = 0;
    for (int i = totals.size() - 1; i >= 0; i--) {
      number += totals.get(i).get(0);
    }
    
    return number;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p9/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<List<Long>> numbersList = new ArrayList<>();
    
    for (String line : lines) {
      String[] splitString = line.split("\\s+");
      List<Long> numbers = new ArrayList<>();
      for (String s : splitString) {
        numbers.add(Long.parseLong(s));
      }
      Collections.reverse(numbers);
      numbersList.add(numbers);
    }
    
    int total = 0;
    for (List<Long> numbers : numbersList) {
      total += findNextValue(numbers);
    }
    System.out.println("total is " + total);
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
