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

public class Puzzlev2 {
  
  public record Data(int x, int m, int a, int s) {}
  public record ValueRange(int min, int max) {
    public long delta() {
      return (long) max() - min() + 1;
    }
  }
  public record DataRange(ValueRange x, ValueRange m, ValueRange a, ValueRange s) {}
  public record Rule(char variable, char op, int value, String dest) {}
  public record RuleSet(String label, List<Rule> rules) {}
  
  public static long calculate(DataRange dataRange) {
    return dataRange.x().delta() * dataRange.m().delta() * dataRange.a().delta() * dataRange.s().delta();
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
    
    Map<String, Set<DataRange>> buckets = new HashMap<>();
    Map<String, Set<DataRange>> seenMap = new HashMap<>();
    for (String label : ruleSetMap.keySet()) {
      buckets.put(label, new HashSet<>());
      seenMap.put(label, new HashSet<>());
    }
    buckets.put("A", new HashSet<>());
    seenMap.put("A", new HashSet<>());
    
    Set<DataRange> inSet = buckets.get("in");
    ValueRange maxRange = new ValueRange(1, 4000);
    inSet.add(new DataRange(maxRange, maxRange, maxRange, maxRange));
    
    boolean processedData = false;
    do {
      processedData = false;
      
      for (String key : buckets.keySet()) {
        if (key == "A") {
          continue;
        }
        Set<DataRange> dataSet = buckets.get(key);
        if (!dataSet.isEmpty()) {
          RuleSet ruleSet = ruleSetMap.get(key);
          process(dataSet, ruleSet, buckets, seenMap);
          processedData = true;
        }
      }
    } while (processedData);
    
    long answer = buckets.get("A").stream()
        .map(Puzzlev2::calculate)
        .reduce(0L, Long::sum);
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
  
  private static int getMinValue(DataRange data, char variable) {
    switch (variable) {
      case 'x':
        return data.x().min();
      case 'm':
        return data.m().min();
      case 'a':
        return data.a().min();
      case 's':
        return data.s().min();
      default:
        throw new RuntimeException("Unexpected " + variable);
    }
  }
  
  private static int getMaxValue(DataRange data, char variable) {
    switch (variable) {
      case 'x':
        return data.x().max();
      case 'm':
        return data.m().max();
      case 'a':
        return data.a().max();
      case 's':
        return data.s().max();
      default:
        throw new RuntimeException("Unexpected " + variable);
    }
  }
  
  private static DataRange replaceRange(DataRange data, ValueRange range, char variable) {
    switch (variable) {
      case 'x':
        return new DataRange(range, data.m(), data.a(), data.s());
      case 'm':
        return new DataRange(data.x(), range, data.a(), data.s());
      case 'a':
        return new DataRange(data.x(), data.m(), range, data.s());
      case 's':
        return new DataRange(data.x(), data.m(), data.a(), range);
      default:
        throw new RuntimeException("Unexpected " + variable);
    }
  }
  
  private static void process(Set<DataRange> dataSet, RuleSet ruleSet, Map<String, Set<DataRange>> buckets, Map<String, Set<DataRange>> seenMap) {
    Set<DataRange> toProcessSet = new HashSet<>(dataSet);
    for (DataRange dataRange : toProcessSet) {
      buckets.get(ruleSet.label()).remove(dataRange);
      for (Rule rule : ruleSet.rules()) {
        if (rule.variable() == 'T') {
          if (rule.dest().equals("R")) {
            break;
          }
          if (seenMap.get(rule.dest()).contains(dataRange)) {
            // Drop it.
            break;
          }
          seenMap.get(rule.dest()).add(dataRange);
          buckets.get(rule.dest()).add(dataRange);
          break;
        } else {
          int minValue = getMinValue(dataRange, rule.variable());
          int maxValue = getMaxValue(dataRange, rule.variable());
          if (rule.op() == '>') {
            if (rule.value() < minValue) {
              if (seenMap.get(rule.dest()).contains(dataRange)) {
                // Drop it.
                break;
              }
              seenMap.get(rule.dest()).add(dataRange);
              
              if (!rule.dest().equals("R")) {
                buckets.get(rule.dest()).add(dataRange);
              }
              break;
            } else if (rule.value() >= maxValue) {
              // Does not match this rule at all.
              continue;
            } else {
              // Split
              ValueRange matchRange = new ValueRange(Math.min(rule.value() + 1, maxValue), maxValue);
              ValueRange unmatchRange = new ValueRange(minValue, rule.value());
              DataRange matchData = replaceRange(dataRange, matchRange, rule.variable());
              DataRange unmatchData = replaceRange(dataRange, unmatchRange, rule.variable());
              
              if (!rule.dest().equals("R")) {
                buckets.get(rule.dest()).add(matchData);
              }
              buckets.get(ruleSet.label()).add(unmatchData);
              break;
            }
          } else if (rule.op() == '<') {
            if (rule.value() > maxValue) {
              if (seenMap.get(rule.dest()).contains(dataRange)) {
                // Drop it.
                break;
              }
              seenMap.get(rule.dest()).add(dataRange);
          
              if (!rule.dest().equals("R")) {
                buckets.get(rule.dest()).add(dataRange);
              }
              break;
            } else if (rule.value() <= minValue) {
              // Does not match this rule at all.
              continue;
            } else {
              // Split
              ValueRange matchRange = new ValueRange(minValue, Math.max(rule.value() - 1, minValue));
              ValueRange unmatchRange = new ValueRange(rule.value(), maxValue);
              DataRange matchData = replaceRange(dataRange, matchRange, rule.variable());
              DataRange unmatchData = replaceRange(dataRange, unmatchRange, rule.variable());
              
              if (!rule.dest().equals("R")) {
                buckets.get(rule.dest()).add(matchData);
              }
              buckets.get(ruleSet.label()).add(unmatchData);
              break;
            }
          }
        }
      }
    }
  }
}
