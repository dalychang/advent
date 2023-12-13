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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Puzzlev4 {
  
  private static int sumList(List<Integer> list) {
    int total = 0;
    for (Integer value : list) {
      total += value;
    }
    return total;
  }
  
  private static boolean isValidBlock(String springs, int startIndex, int endIndex) {
    for (int k = startIndex; k <= endIndex; k++) {
      if (springs.charAt(k) == '.') {
        return false;
      }
    }
    return true;
  }
  
  private static String makeKey(String springs, List<Integer> groupings) {
    return springs + " " +  Joiner.on(',').join(groupings);
  }
  
  private static int indexOfMax(List<Integer> list) {
    int maxValue = -1;
    int index = -1;
    for (int i = 0; i < list.size(); i++) {
      int value = list.get(i);
      if (value > maxValue) {
        maxValue = value;
        index = i;
      }
    }
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      int value = list.get(i);
      if (value == maxValue) {
        return i;
      }
    }
    return 0;
  }
  
  private static long calculatePermutationsSplit(Map<String, Long> answerCache, String springs, List<Integer> groupings) {
    final String key = makeKey(springs, groupings);
    if (answerCache.containsKey(key)) {
      return answerCache.get(key);
    }

    if (springs.length() <= 5) return calculatePermutations(answerCache, springs, groupings);
    
    int maxIndex = indexOfMax(groupings);
    List<Integer> leftGrouping = groupings.subList(0, maxIndex);
    List<Integer> rightGrouping = maxIndex + 1 < groupings.size() ? groupings.subList(maxIndex + 1, groupings.size()) : List.of();
    int leftSum = sumList(leftGrouping);
    int rightSum = sumList(rightGrouping);
    
    int groupSize = groupings.get(maxIndex);
    
    long permutations = 0;
    for (int i = 0; i <= springs.length() - groupSize; i++) {
      if (i < leftSum + leftGrouping.size()) continue;
      if (springs.length() - i + groupSize < rightSum + rightGrouping.size()) continue;
      
      int startIndex = i;
      int endIndex = i + groupSize - 1;
      if (!isValidBlock(springs, startIndex, endIndex)) {
        continue;
      }
      
      boolean leftOk = i == 0 || (startIndex > 0 && springs.charAt(startIndex - 1) != '#');
      boolean rightOk = endIndex == springs.length() - 1 || (endIndex < springs.length() - 1 && springs.charAt(endIndex + 1) != '#');
      if (!leftOk || !rightOk) {
        continue;
      }
      
      long leftPermutations = 0;
      String leftSprings = startIndex - 1 > 0 ? springs.substring(0, startIndex - 1) : "";
      if (leftGrouping.isEmpty()) {
        leftPermutations = calculatePermutations(answerCache, leftSprings, leftGrouping);
      } else {
        leftPermutations = calculatePermutationsSplit(answerCache, leftSprings, leftGrouping);
      }

      long rightPermutations = 0;
      String rightSprings = endIndex + 2 < springs.length() ? springs.substring(endIndex + 2) : "";
      if (rightGrouping.isEmpty()) {
        rightPermutations = calculatePermutations(answerCache, rightSprings, rightGrouping);
      } else {
        rightPermutations = calculatePermutationsSplit(answerCache, rightSprings, rightGrouping);
      }

      permutations += (leftPermutations * rightPermutations);
    }

    answerCache.put(key, permutations);
    return permutations;
  }

  private static long calculatePermutations(Map<String, Long> answerCache, String springs, List<Integer> groupings) {
    final String key = makeKey(springs, groupings);
    if (answerCache.containsKey(key)) {
      return answerCache.get(key);
    }

    if (groupings.isEmpty()) {
      for (int i = 0; i < springs.length(); i++) {
        if (springs.charAt(i) == '#') {
          answerCache.put(key, 0L);
          return 0;
        }
      }
      answerCache.put(key, 1L);
      return 1;
    }
    
    if (springs.length() < sumList(groupings) + groupings.size() - 1) {
      answerCache.put(key, 0L);
      return 0;
    }

    if (springs.isEmpty()) {
      answerCache.put(key, 0L);
      return 0;
    }
    int groupSize = groupings.get(0);
    if (springs.length() < groupSize) {
      answerCache.put(key, 0L);
      return 0;
    }
    
    boolean cutOk = true;
    for (int i = 0; i < groupSize; i++) {
      char c = springs.charAt(i);
      if (c == '.') {
        cutOk = false;
        break;
      }
    }
    
    long total = 0;
    if (cutOk) {
      if (springs.length() > groupSize) {
        if (springs.charAt(groupSize) != '#') {
          String newSprings = springs.length() >= groupSize + 1 ? springs.substring(groupSize + 1) : "";
          List<Integer> newGroupings = groupings.size() > 1 ? groupings.subList(1, groupings.size()) : List.of();
          total += calculatePermutations(answerCache, newSprings, newGroupings);
        }
      } else {
        total += 1;
      }
    }
    
    if (springs.charAt(0) != '#') {
      String newSprings = springs.length() > 1 ? springs.substring(1) : "";
      total += calculatePermutations(answerCache, newSprings, groupings);
    }

    answerCache.put(key, total);
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
      String spring = split1[0];
      for (int j = 0; j < 4; j++) {
        spring += "?" + split1[0];
      }
      springsList.add(spring);
      List<Integer> grouping = new ArrayList<>();
      for (int j = 0; j < 5; j++) {
        for (String group : split2) {
          grouping.add(Integer.parseInt(group.trim()));
        }
      }
      groupingsList.add(grouping);
    }
    
    ExecutorService executorService = Executors.newFixedThreadPool(30);
    List<Future<Long>> futures = new ArrayList<>();
    
    for (int i = 0; i < springsList.size(); i++) {
      final int index = i;
      futures.add(executorService.submit(new Callable<Long>() {
        @Override
        public Long call() {
          //System.out.println("Start: " + (index + 1) + "/" + springsList.size());
          Map<String, Long> answerCache = new HashMap<>();
          Long a = calculatePermutationsSplit(answerCache, springsList.get(index), groupingsList.get(index));
          //System.out.println("End: " + (index + 1) + "/" + springsList.size());
          return a;
        }
      }));
    }

    long permutations = 0;    
    for (Future<Long> future : futures) {
      try {
        Long p = future.get();
        permutations += p;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    System.out.println("permutations is " + permutations);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
