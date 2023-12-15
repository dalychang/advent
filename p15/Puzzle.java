package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
  
  public static long calculate(String s) {
    long currentValue = 0;
    for (int i = 0; i < s.length(); i++) {
      currentValue += (int) s.charAt(i);
      currentValue = (currentValue * 17L) % 256;
    }
    //System.out.println(currentValue);
    return currentValue;
  }
    
  public static void main(String[] args) throws Exception {
    List<String> lines = Helper.loadFile("dev_advent/p15/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    lines = new ArrayList<>(Arrays.asList(lines.get(0).split(",")));
    
    //for (String line : lines) {
    //  System.out.println(line);
    //}
    
    long answer = lines.stream()
        .map(Puzzle::calculate)
        .reduce(0L, Long::sum);
        
    System.out.println("answer is " + answer);    
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
