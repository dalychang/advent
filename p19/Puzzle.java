package dev.advent;

import java.time.Clock;
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
  
  public record Data(int x, int m, int a, int s) {}
  public record Rule(char variable, char op, int value, String dest) {}
  public record RuleSet(String label, List<Rule> rules) {}
  
   public static long calculate(Data data) {
    return data.x() + data.m() + data.a() + data.s();
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p19/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern topPattern = Pattern.compile("^(\\w+)\\{(.*)\\}$");
    Pattern rulePattern = Pattern.compile("^(\\w)([<>])(\\d+):(\\w+)$");
    Pattern termPattern = Pattern.compile("^(\\w+)$");
    Pattern dataPattern = Pattern.compile("^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}$");
    
    List<Data> dataList = new ArrayList<>();
    Map<String, RuleSet> ruleSetMap = new HashMap<>();
    boolean processingData = false;
    for (String line : lines) {
      if (line.isEmpty()) {
        processingData = true;
        continue;
      }
      
      if (processingData) {
        Matcher m = dataPattern.matcher(line);
        m.find();
        dataList.add(new Data(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
      } else {
        Matcher mTop = topPattern.matcher(line);
        mTop.find();
        String label = mTop.group(1);
        String[] split = mTop.group(2).split(",");
        
        List<Rule> rules = new ArrayList<>();
        for (String s : split) {
          Matcher tm = termPattern.matcher(s);
          if (tm.find()) {
            rules.add(new Rule('T', '=', 0, tm.group(1)));
          } else {
            Matcher rm = rulePattern.matcher(s);
            rm.find();
            rules.add(new Rule(rm.group(1).charAt(0), rm.group(2).charAt(0), Integer.parseInt(rm.group(3)), rm.group(4)));
          }
        }
        ruleSetMap.put(label, new RuleSet(label, rules));
      }
    }
    
    Map<String, Set<Data>> buckets = new HashMap<>();
    for (String label : ruleSetMap.keySet()) {
      buckets.put(label, new HashSet<>());
    }
    buckets.put("A", new HashSet<>());
    
    Set<Data> inSet = buckets.get("in");
    inSet.addAll(dataList);
    
    boolean procssedData = false;
    do {
      procssedData = false;
      
      for (String key : buckets.keySet()) {
        if (key == "A") {
          continue;
        }
        Set<Data> dataSet = buckets.get(key);
        if (!dataSet.isEmpty()) {
          RuleSet ruleSet = ruleSetMap.get(key);
          process(dataSet, ruleSet, buckets);
          procssedData = true;
        }
      }
    } while (procssedData);
    
    //System.out.println(buckets.get("A"));
    //System.out.println("");
    
    long answer = buckets.get("A").stream()
        .map(Puzzle::calculate)
        .reduce(0L, Long::sum);
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  private static int getValue(Data data, char variable) {
    switch (variable) {
      case 'x':
        return data.x();
      case 'm':
        return data.m();
      case 'a':
        return data.a();
      case 's':
        return data.s();
      default:
        throw new RuntimeException("Unexpected " + variable);
    }
  }
  
  private static void process(Set<Data> dataSet, RuleSet ruleSet, Map<String, Set<Data>> buckets) {
    Set<Data> toProcessSet = new HashSet<>(dataSet);
    for (Data data : toProcessSet) {
      dataSet.remove(data);
      for (Rule rule : ruleSet.rules()) {
        if (rule.variable() == 'T') {
          if (rule.dest().equals("R")) {
            break;
          }
          buckets.get(rule.dest()).add(data);
          break;
        } else {
          int value = getValue(data, rule.variable());
          boolean result = (rule.op() == '>')
              ? value > rule.value()
              : value < rule.value();
          if (result) {
            if (!rule.dest().equals("R")) {
              buckets.get(rule.dest()).add(data);
            }
            break;
          }
        }
      }
    }
  }
}
