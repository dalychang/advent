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
    List<List<Integer>> ends = new ArrayList<>();
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
        if (seenList.get(i).contains(currentPosition) && firstSeenList.get(i) == 0) {
          firstSeenList.set(i, (int)steps);
          seenList.get(i).clear();
        }
        if (firstSeenList.get(i) != 0 && cycleSize.get(i) == 0) {
          System.out.println(currentPosition);
          if (currentPosition.endsWith("Z")) {
            ends.get(i).add(seenList.get(i).size());
          }
        }
        if (seenList.get(i).contains(currentPosition) && firstSeenList.get(i) != 0 && cycleSize.get(i) == 0) {
          cycleSize.set(i, seenList.get(i).size());
        }
        seenList.get(i).add(currentPosition);
      }

      int dataCollected = 0;
      for (int j = 0; j < currentPositions.size(); j++) {
        if (cycleSize.get(j) != 0) {
          dataCollected++;
        }
      }
      if (dataCollected == currentPositions.size()) {
        done = true;
      }
      if (steps % 10000000 == 0) {
        System.out.println(String.format("\tpos=%d d=%s s=%d", currentPositions.size(), direction, steps));
        for (int j = 0; j < currentPositions.size(); j++) {
          System.out.println(String.format("\t\t%d - seen=%d firstSeen=%d", j, seenList.get(j).size(), firstSeenList.get(j)));
        }
      }
      directionPosition = (directionPosition + 1) % directions.length();
    }
    
    // Phase 2
    List<Integer> initial = new ArrayList<>();
    for (int i = 0; i < currentPositions.size(); i++) {
      initial.add(firstSeenList.get(i) - cycleSize.get(i));
    }
    
    System.out.println("initials=" + initial);
    System.out.println("cycleSize=" + cycleSize);
    System.out.println("ends=" + ends);
    
    System.out.println("steps=" + steps);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
