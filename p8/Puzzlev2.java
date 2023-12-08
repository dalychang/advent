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
    Map<String, Boolean> endMap = new HashMap<>();
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
      if (key.endsWith("Z")) {
        endMap.put(key, true);
      } else {
        endMap.put(key, false);
      }
    }
    
    System.out.println(String.format("Starting positions = %s", currentPositions));
    
    long steps = 0;
    boolean done = false;
    int directionPosition = 0;
    while (!done) {
      steps++;
      char direction = directions.charAt(directionPosition);
      int ends = 0;
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
        if (endMap.get(currentPosition)) {
          ends++;
        }
      }

      if (ends == currentPositions.size()) {
        done = true;
      }
      if (steps % 10000000 == 0) {
        System.out.println(String.format("\ttime=%d d=%s s=%d", (clock.millis() - startTime), direction, steps));
      }
      directionPosition = (directionPosition + 1) % directions.length();
    }
    
    System.out.println("steps=" + steps);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
