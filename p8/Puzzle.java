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
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p8/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    for (String line : lines) {
      System.out.println(line);
    }
    
    String directions = lines.get(0);
    Map<String, String> leftMap = new HashMap<>();
    Map<String, String> rightMap = new HashMap<>();
    Pattern pattern = Pattern.compile("^(\\w{3})\\s+=\\s+\\((\\w{3}),\\s+(\\w{3})\\)$");
    for (int i = 2; i < lines.size(); i++) {
      String line = lines.get(i);
      Matcher m = pattern.matcher(line);
      m.find();
      String key = m.group(1);
      leftMap.put(key, m.group(2));
      rightMap.put(key, m.group(3));
    }
    
    int steps = 0;
    boolean done = false;
    String currentPosition = "AAA";
    int directionPosition = 0;
    while (!done) {
      steps++;
      switch (directions.charAt(directionPosition)) {
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

      if (currentPosition.equals("ZZZ")) {
        done = true;
      }
      directionPosition = (directionPosition + 1) % directions.length();
    }
    System.out.println("steps=" + steps);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
