package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  
  public static class Lens {
    public String label;
    public int focalLength;
    
    public Lens(String label, int focalLength) {
      this.label = label;
      this.focalLength = focalLength;
    }
    
    public String toString() {
      return label + " " + focalLength;
    }
  }
  
  public static int hash(String s) {
    int currentValue = 0;
    for (int i = 0; i < s.length(); i++) {
      currentValue += (int) s.charAt(i);
      currentValue = (currentValue * 17) % 256;
    }
    return currentValue;
  }
  
  public static void init(List<LinkedList<Lens>> boxes, String s) {
    if (s.charAt(s.length() - 1) == '-') {
      String label = s.substring(0, s.length() - 1);
      int hash = hash(label);
      LinkedList<Lens> lensList = boxes.get(hash);
      for (Lens lens : lensList) {
        if (lens.label.equals(label)) {
          lensList.remove(lens);
          break;
        }
      }
    } else {
      String[] split = s.split("=");
      String label = split[0];
      int hash = hash(label);
      LinkedList<Lens> lensList = boxes.get(hash);
      int focalLength = Integer.parseInt(split[1]);
      boolean lensFound = false;
      for (Lens lens : lensList) {
        if (lens.label.equals(label)) {
          lensFound = true;
          lens.focalLength = focalLength;
          break;
        }
      }
      if (!lensFound) {
        lensList.addLast(new Lens(label, focalLength));
      }
    }
  }
  
  public static long calculate(LinkedList<Lens> lensList, int index) {
    long total = 0;
    int multiple = (index + 1);
    int lensIndex = 1;
    for (Lens lens : lensList) {
      total += lens.focalLength * lensIndex * multiple;
      lensIndex++;
    }
    return total;
  }
    
  public static void main(String[] args) throws Exception {
    List<String> lines = Helper.loadFile("dev_advent/p15/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    lines = new ArrayList<>(Arrays.asList(lines.get(0).split(",")));
    
    List<LinkedList<Lens>> boxes = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      boxes.add(new LinkedList<>());
    }
    
    for (String line : lines) {
      init(boxes, line);
    }

    long answer = 0;
    for (int i = 0; i < 256; i++) {
      answer += calculate(boxes.get(i), i);
    }

    System.out.println("answer is " + answer);    
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
