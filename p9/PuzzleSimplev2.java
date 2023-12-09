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

public class PuzzleSimplev2 {
  
  static void populate(List<List<Long>> totals) {
    while (totals.get(totals.size() - 1).get(totals.get(totals.size() - 1).size() - 1) != 0) {
      List<Long> numbers = new ArrayList<>();
      List<Long> input = totals.get(totals.size() - 1);
      for (int i = 0; i < input.size() - 1; i++) {
        numbers.add(input.get(i + 1) - input.get(i));
      }
      totals.add(numbers);
    }
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
      numbersList.add(numbers);
    }
    
    int total = 0;
    for (List<Long> numbers : numbersList) {
      List<List<Long>> totals = new ArrayList<>();
      totals.add(numbers);
      populate(totals);
      long subtotal = 0;
      for (int i = totals.size() - 1; i >= 0; i--) {
        subtotal = totals.get(i).get(0) - subtotal;
      }
      total += subtotal;
    }
    System.out.println("total is " + total);
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
