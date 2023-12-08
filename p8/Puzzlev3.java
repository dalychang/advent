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

public class Puzzlev3 {
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p8/input4.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    String directions = lines.get(0);
    Map<String, String> leftMap = new HashMap<>();
    Map<String, String> rightMap = new HashMap<>();
    List<String> currentPositions = new ArrayList<>();
    Pattern pattern = Pattern.compile("^(\\w{3})\\s+=\\s+\\((\\w{3}),\\s+(\\w{3})\\)$");
    for (int i = 2; i < lines.size(); i++) {
      String line = lines.get(i);
      Matcher m = pattern.matcher(line);
      m.find();
      String key = m.group(1);
      leftMap.put(key, m.group(2));
      rightMap.put(key, m.group(3));
      if (key.endsWith("A")) {
        currentPositions.add(key);
      }
    }
    
    System.out.println(String.format("Starting positions = %s", currentPositions));
    
    List<Set<String>> seenList = new ArrayList<>();
    List<Integer> firstSeenList = new ArrayList<>();
    List<Integer> cycleSize = new ArrayList<>();
    List<List<Long>> ends = new ArrayList<>();
    for (String p : currentPositions) {
      Set<String> seenSet = new HashSet<>();
      seenSet.add(p);
      seenList.add(seenSet);
      firstSeenList.add(0);
      cycleSize.add(0);
      ends.add(new ArrayList<>());
    }
    
    long steps = 0;
    boolean done = false;
    int directionPosition = 0;
    while (!done) {
      steps++;
      char direction = directions.charAt(directionPosition);
      for (int i = 0; i < currentPositions.size(); i++) {
        String currentPosition = currentPositions.get(i);
        switch (direction) {
          case 'R':
            currentPosition = rightMap.get(currentPosition);
            break;
          case 'L':
            currentPosition = leftMap.get(currentPosition);
            break;
          default:
            System.out.println("Error!");
            break;
        }
        currentPositions.set(i, currentPosition);
        if (currentPosition.endsWith("Z")) {
          ends.get(i).add(steps);
        }
        
        if (seenList.get(i).contains(currentPosition) && firstSeenList.get(i) == 0) {
          firstSeenList.set(i, (int)steps);
          seenList.get(i).clear();
        }
        if (seenList.get(i).contains(currentPosition) && firstSeenList.get(i) != 0 && cycleSize.get(i) == 0) {
          cycleSize.set(i, seenList.get(i).size());
        }
        seenList.get(i).add(currentPosition);
      }

      if (steps > 100000) {
        done = true;
      }
      directionPosition = (directionPosition + 1) % directions.length();
    }
    
    System.out.println("cycleSize=" + cycleSize);
    System.out.println("ends=" + ends);
    
    // Phase 2 (manually computed from previous output)
    // initial value can be the last number in ends for each position. increment can be last number minus the previous number.
    // Too lazy to update code to do that automatically since the question is solved.
    List<Long> values = new ArrayList<>(List.<Long>of(80884L, 78114L, 78668L, 88086L, 74236L, 84485L));
    List<Long> increments = List.of(20221L, 13019L, 19667L, 14681L, 18559L, 16897L);
    
    boolean found = false;
    while (!found) {
      int position = findMin(values);
      values.set(position, values.get(position) + increments.get(position));
      
      if (allSame(values)) {
        found = true;
      }
    }

    System.out.println("value=" + values.get(0));
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  static boolean allSame(List<Long> values) {
    long number = values.get(0);
    for (Long value : values) {
      if (value != number) {
        return false;
      }
    }
    return true;
  }
  
  static int findMin(List<Long> values) {
    long number = values.get(0);
    int position = 0;
    for (int i = 1; i < values.size(); i++) {
      if (values.get(i) < number) {
        position = i;
        number = values.get(i);
      }
    }
    return position;
  }
}
