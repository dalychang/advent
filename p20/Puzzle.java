package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
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

public class Puzzle {
  public static boolean log = false;
  
  public static abstract class Module {
    private final String label;
    private final List<Module> downstreamModules;
    private final List<Module> upstreamModules;
    private final LinkedList<Boolean> pendingPropagations;
    
    private long lowPulses;
    private long highPulses;
    
    public Module(String label) {
      this.label = label;
      downstreamModules = new ArrayList<>();
      upstreamModules = new ArrayList<>();
      pendingPropagations = new LinkedList<>();
    }
    
    public void attachModule(Module m) {
      downstreamModules.add(m);
      m.attachUpModule(this);
    }
    
    void attachUpModule(Module m) {
      upstreamModules.add(m);
    }
    
    public abstract void trigger(Module source, boolean pulse);
    
    void propagate(boolean pulse) {
      pendingPropagations.addLast(pulse);
    }
    
    boolean run() {
      if (pendingPropagations.isEmpty()) return false;
    
      boolean pulse = pendingPropagations.poll();
      if (log) System.out.println(label + ": " + pulse);
      if (pulse) {
        highPulses += downstreamModules.size();
      } else {
        lowPulses += downstreamModules.size();
      }

      for (Module m : downstreamModules) {
        m.trigger(this, pulse);
      }
      return true;
    }
    
    public long getLowPulses() { return lowPulses; }
    public long getHighPulses() { return highPulses; }
  }
  
  public static class FlipFlopModule extends Module {
    boolean state;
    
    public FlipFlopModule(String label) {
      super(label);
      
      this.state = false;
    }

    @Override    
    public void trigger(Module source, boolean pulse) {
      if (!pulse) {
        this.state = !this.state;
        propagate(this.state);
      }
    }
  }
  
  public static class BroadcastModule extends Module {
    
    public BroadcastModule(String label) {
      super(label);
    }

    @Override    
    public void trigger(Module source, boolean pulse) {
      propagate(pulse);
    }
  }
  
  public static class ButtonModule extends Module {
    
    public ButtonModule(String label) {
      super(label);
    }

    @Override    
    public void trigger(Module source, boolean pulse) {
      propagate(pulse);
    }
    
    public void press() {
      trigger(null, false);
    }
  }
  
  public static class ConjunctionModule extends Module {
    private final Map<Module, Boolean> sourceModulesMap;
    
    public ConjunctionModule(String label) {
      super(label);
      
      sourceModulesMap = new HashMap<>();
    }
    
    @Override
    void attachUpModule(Module m) {
      super.attachUpModule(m);
      sourceModulesMap.put(m, false);
    }

    @Override    
    public void trigger(Module source, boolean pulse) {
      sourceModulesMap.put(source, pulse);
      for (boolean b : sourceModulesMap.values()) {
        if (!b) {
          propagate(true);
          return;
        }
      }
      propagate(false);
    }
  }
  
  public static class OutputModule extends Module {
    public OutputModule(String label) {
      super(label);
    }

    @Override    
    public void trigger(Module source, boolean pulse) {
    }
  }
  
  public static long calculate(String s) {
    return 1;
  }
  
  public static void runSystem(Collection<Module> modules) {
    boolean done = false;
    while (!done) {
      done = true;
      for (Module m : modules) {
        if (m.run()) done = false;
      }
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p20/input3.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern configPattern = Pattern.compile("^([%&]?)(\\w+) -> (.*)$");
    
    Map<String, Module> labeledModules = new HashMap<>();
    Map<String, List<String>> connections = new HashMap<>();;
    for (String line : lines) {
      Matcher configMatcher = configPattern.matcher(line);
      configMatcher.find();
      String moduleType = configMatcher.group(1);
      String label = configMatcher.group(2);
      String[] targets = configMatcher.group(3).split(",");
      
      switch (moduleType) {
        case "":
          labeledModules.put(label, new BroadcastModule(label));
          break;
        case "%":
          labeledModules.put(label, new FlipFlopModule(label));
          break;
        case "&":
          labeledModules.put(label, new ConjunctionModule(label));
          break;
        default:
          throw new RuntimeException("Unexpected module type " + moduleType);
      }
      
      List<String> targetList = new ArrayList<>();
      for (String s : targets) {
        targetList.add(s.trim());
      }
      connections.put(label, targetList);
    }
    ButtonModule button = new ButtonModule("button");
    labeledModules.put("button", button);
    button.attachModule(labeledModules.get("broadcaster"));
    labeledModules.put("output", new OutputModule("output"));
    
    for (String label : connections.keySet()) {
      Module source = labeledModules.get(label);
      List<String> targetList = connections.get(label);
      for (String target : targetList) {
        Module dest = labeledModules.get(target);
        if (dest == null) {
          dest = new OutputModule(target);
          labeledModules.put(target, dest);
          //throw new RuntimeException(target + " module is missing.");
        }
        source.attachModule(dest);
      }
    }
    
    for (int i = 0; i < 1000; i++) {
      button.press();
      runSystem(labeledModules.values());
      if (log) System.out.println("");
    }
    
    
    long low = labeledModules.values().stream()
        .map((m) -> m.getLowPulses())
        .reduce(0L, Long::sum);
    long high = labeledModules.values().stream()
        .map((m) -> m.getHighPulses())
        .reduce(0L, Long::sum);    
   
    long answer = low * high;
    System.out.println("low is " + low); 
    System.out.println("high is " + high); 
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
