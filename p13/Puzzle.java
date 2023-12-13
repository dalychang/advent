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
  
  private static int findHorizontal(List<String> lines) {
    for (int i = 1; i < lines.size(); i++) {
      boolean ok = true;
      for (int j = 0; j < Math.min(lines.size() - i, i); j++) {
        int beforeLine = i - j - 1;
        int afterLine = i + j;
        //System.out.println(String.format("i %d beforeLine %d afterLine %d", i, beforeLine, afterLine));
        if (!lines.get(beforeLine).equals(lines.get(afterLine))) {
          ok = false;
          break;
        }
      }
      if (ok) return i;
    }
    return -1;
  }
  
  private static int findVertical(List<String> lines) {
    for (int i = 1; i < lines.get(0).length(); i++) {
      boolean ok = true;
      for (int j = 0; j < Math.min(lines.get(0).length() - i, i); j++) {
        int before = i - j - 1;
        int after = i + j;
        for (int k = 0; k < lines.size(); k++) {
          if (lines.get(k).charAt(before) != lines.get(k).charAt(after)) {
            ok = false;
            break;
          }
        }
        if (!ok) break;
      }
      if (ok) return i;
    }
    return -1;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p13/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<List<String>> lineBatches = new ArrayList<>();
    List<String> lineBatch = new ArrayList<>();
    for (String line : lines) {
      if (line.isEmpty()) {
        lineBatches.add(lineBatch);
        lineBatch = new ArrayList<>();
        continue;
      }
      lineBatch.add(line);
    }
    lineBatches.add(lineBatch);
    
    long total = 0;
    for (List<String> batch : lineBatches) {
      int vertical = findVertical(batch);
      int horizontal = findHorizontal(batch);
      if (vertical > 0) {
        total += vertical;
      }
      if (horizontal > 0) {
        total += (horizontal * 100);
      }
    }
    
    System.out.println("total = " + total);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
