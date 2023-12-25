package dev.advent;

import com.google.common.base.Joiner;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  private static final String COMMA_SEPARATOR = ", ";
  
  public record Path(String label, String location, List<String> places) {}
  public record Edge(String c1, String c2) {
    public boolean matches(Component c1, Component c2) {
      return equals(makeEdge(c1.getLabel(), c2.getLabel()));
    }
  }
  public record EdgeCostPair(Edge edge, Long cost) implements Comparable<EdgeCostPair> {
    @Override
    public int compareTo(EdgeCostPair ecp) {
      return (int)(ecp.cost() - this.cost());
    }
  }
  
  public static class Component {
    private final String label;
    private final Set<Component> connections;
    
    public Component(String label) {
      this.label = label;
      this.connections = new HashSet<>();
    }
    
    private void addInternal(Component component) {
      this.connections.add(component);
    }
    
    public void add(Component component) {
      this.connections.add(component);
      component.addInternal(this);
    }
    
    public void remove(Component component) {
      this.connections.remove(component);
    }
    
    public Set<Component> getConnections() {
      return connections;
    }
    
    public String getLabel() {
      return label;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder("Component(");
      sb.append(label);
      sb.append(COMMA_SEPARATOR);
      sb.append("[");
      List<String> cList = new ArrayList<>();
      for (Component c : connections) {
        cList.add(c.getLabel());
      }
      sb.append(Joiner.on(COMMA_SEPARATOR).join(cList));
      sb.append("])");
      return sb.toString();
    }
  }
  
  public static long calculate(Component c) {
    return c.getConnections().size();
  }
  
  public static long countPaths(Map<String, Component> componentMap, Component source, Component destination) {
    LinkedList<Path> paths = new LinkedList<>();
    paths.add(new Path(source.getLabel(), source.getLabel(), List.of(source.getLabel())));

    List<Path> completedPaths = new ArrayList<>();
    
    while (!paths.isEmpty()) {
      Path path = paths.poll();
      if (path.location().equals(destination.getLabel())) {
        completedPaths.add(path);
        continue;
      }
      for (Component next : componentMap.get(path.location()).getConnections()) {
        if (path.places.contains(next.getLabel())) {
          continue;
        }
        List<String> places = new ArrayList<>(path.places());
        places.add(next.getLabel());
        Path nextPath = new Path(path.label(), next.getLabel(), places);
        paths.add(nextPath);
      }
    }
    
    return completedPaths.size();
  }
  
  public static long countCluster(Map<String, Component> componentMap, Component start) {
    Set<Component> visited = new HashSet<>();
    visited.add(start);
    
    LinkedList<Component> pending = new LinkedList<>();
    pending.add(start);
    
    while (!pending.isEmpty()) {
      Component c = pending.poll();
      for (Component d : c.getConnections()) {
        if (!visited.contains(d)) {
          visited.add(d);
          pending.add(d);
        }
      }
    }
    return visited.size();
  }
  
  public static Map<Component, Long> findCostMap(Map<String, Component> componentMap, Component start, Edge removedEdge) {
    Set<Component> pending = new HashSet<>();
    pending.add(start);
    Map<Component, Long> costs = new HashMap<>();
    costs.put(start, 0L);
    
    long cost = 0;
    while (!pending.isEmpty()) {
      Set<Component> toProcess = pending;
      pending = new HashSet<>();
      cost++;
      for (Component c : toProcess) {
        for (Component d : c.getConnections()) {
          if (costs.containsKey(d)) continue;
          if (removedEdge != null && removedEdge.matches(c, d)) {
            continue;
          }
          costs.put(d, cost);
          pending.add(d);
        }
      }
    }
    return costs;
  }
  
  public static Edge makeEdge(String c1, String c2) {
    String s1 = c1.compareTo(c2) == 1 ? c1 : c2;
    String s2 = c1.compareTo(c2) == 1 ? c2 : c1;
    return new Edge(s1, s2);
  }
  
  public static Set<Edge> buildEdges(List<String> componentNames) {
    Set<Edge> edges = new HashSet<>();
    for (int i = 0; i < componentNames.size(); i++) {
      String c1 = componentNames.get(i);
      for (int j = i + 1; j < componentNames.size(); j++) {
        String c2 = componentNames.get(j);
        edges.add(makeEdge(c1, c2));
      }
    }
    return edges;
  }

    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p25/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern pattern = Pattern.compile("^(\\w+): (.*)$");
    Map<String, Component> componentMap = new HashMap<>();
    for (String line : lines) {
      Matcher m = pattern.matcher(line);
      m.find();
      
      String label = m.group(1);
      if (!componentMap.containsKey(label)) {
        componentMap.put(label, new Component(label)); 
      }
      Component component = componentMap.get(label);
      String[] split = m.group(2).trim().split("\\s");
      for (String s : split) {
        if (!componentMap.containsKey(s)) {
          componentMap.put(s, new Component(s)); 
        }
        component.add(componentMap.get(s));
      }
    }
    
    List<String> componentNames = new ArrayList<>(componentMap.keySet());
    Set<Edge> edges = buildEdges(componentNames);
    Component startComponent = componentMap.get(componentNames.get(2));  // Randomly picked.
    
    // Read off of output.svg
    List<Edge> candidateEdges = List.of(
      makeEdge("rsm", "bvc"),
      makeEdge("bkm", "ldk"),
      makeEdge("pgh", "zmq")
    );
    Set<Component> componentsToCount = new HashSet<>();
    for (Edge edge : candidateEdges) {
      Component c1 = componentMap.get(edge.c1());
      Component c2 = componentMap.get(edge.c2());
      c1.remove(c2);
      c2.remove(c1);
      componentsToCount.add(c1);
      componentsToCount.add(c2);
    }      
    
    Set<Long> numbers = new HashSet<>();
    for (Component c : componentsToCount) {
      numbers.add(countCluster(componentMap, c));
    }
    
    long answer = numbers.stream()
        .reduce(1L, (a, b) -> a * b);
        
    System.out.println("answer is " + answer);  
    
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
