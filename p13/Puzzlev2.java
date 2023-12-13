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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Puzzlev2 {
  
  private static int findHorizontal(List<String> lines, int notThis) {
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
      if (ok && i != notThis) return i;
    }
    return -1;
  }
  
  private static int findVertical(List<String> lines, int notThis) {
    for (int i = 1; i < lines.get(0).length(); i++) {
      boolean ok = true;
      for (int j = 0; j < Math.min(lines.get(0).length() - i, i); j++) {
        int before = i - j - 1;
        int after = i + j;
       // if (debug) System.out.println(String.format("i %d before %d after %d", i, before, after));
        for (int k = 0; k < lines.size(); k++) {
          if (lines.get(k).charAt(before) != lines.get(k).charAt(after)) {
            ok = false;
            break;
          }
        }
        if (!ok) break;
      }
      if (ok && i != notThis) return i;
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
    
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    List<Future<Long>> futures = new ArrayList<>();
    
    for (List<String> batch : lineBatches) {
      futures.add(executorService.submit(new Callable<Long>() {
        @Override
        public Long call() {
          return findNewTotal(batch);
        }
      }));
    }
    
    long total = 0;
    for (Future<Long> future : futures) {
      try {
        Long p = future.get();
        total += p;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    System.out.println("total = " + total);
    
    executorService.shutdown();
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  private static Long findNewTotal(List<String> lines) {
    int originalVertical = findVertical(lines, -1);
    int originalHorizontal = findHorizontal(lines, -1);
    
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        List<String> updatedLines = new ArrayList<>(lines);
        String updatedLine = lines.get(i);
        char updatedChar = updatedLine.charAt(j) == '#' ? '.' : '#';
        updatedLine = updatedLine.substring(0, j) + updatedChar + updatedLine.substring(j + 1);
        updatedLines.set(i, updatedLine);
        
        int vertical = findVertical(updatedLines, originalVertical);
        if (vertical > 0) {
          return (long)vertical;
        }
        int horizontal = findHorizontal(updatedLines, originalHorizontal);
        if (horizontal > 0) {
          return (long)(horizontal * 100);
        }
      }
    }

    System.out.println("Nothing found.");
    printBatch(lines);
    return 0L;
  }
  
  private static void printBatch(List<String> lines) {
    for (String line : lines) {
      System.out.println(line);
    }
    System.out.println("");
  }
}
