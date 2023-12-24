package dev.advent;

import com.microsoft.z3.*;
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

public class Puzzlev3 {
  public record Position(long x, long y, long z) {}
  public record Velocity(long x, long y, long z) {}
  public static class Hail {
    private final Position position;
    private final Velocity velocity;
    
    private final double slope;
    
    public Hail(Position position, Velocity velocity) {
      this.position = position;
      this.velocity = velocity;
      
      slope = (double) velocity.y() / velocity.x();
    }
    
    private boolean pointInFuture(double x, double y) {
      return (position.x() < x && velocity.x() > 0) || (position.x() > x && velocity.x() < 0)
          && (position.y() < y && velocity.y() > 0) || (position.y() > y && velocity.y() < 0);
    }
    
    private double A() {
      return slope;
    }
    
    private double B() {
      return -1;
    }
    
    private double C() {
      return position.y() - slope * position.x();
    }
    
    public Position position() { return position; }
    public Velocity velocity() { return velocity; }
    public double slope() { return slope; }
    
    public String toString() {
      return "Hail(position=" + position + " velocity=" + velocity + " slope=" + slope + ")";
    }
  }

  public static List<String> generateZ3Commands(List<Hail> hailList) {
    List<String> commands = new ArrayList<>();
    commands.add("(declare-const p0x Int)");
    commands.add("(declare-const p0y Int)");
    commands.add("(declare-const p0z Int)");
    commands.add("(declare-const v0x Int)");
    commands.add("(declare-const v0y Int)");
    commands.add("(declare-const v0z Int)");
    
    for (int i = 0; i < hailList.size(); i++) {
      Hail hail = hailList.get(i);
      commands.add(String.format("(declare-const t%d Int)", i));
      commands.add(String.format("(assert (= (+ p0x (* t%d v0x)) (+ %d (* t%d %d))))", i, hail.position().x(), i, hail.velocity().x()));
      commands.add(String.format("(assert (= (+ p0y (* t%d v0y)) (+ %d (* t%d %d))))", i, hail.position().y(), i, hail.velocity().y()));
      commands.add(String.format("(assert (= (+ p0z (* t%d v0z)) (+ %d (* t%d %d))))", i, hail.position().z(), i, hail.velocity().z()));
    }
    commands.add("(declare-const answer Int)");
    commands.add("(assert (= answer (+ p0x p0y p0z)))");
    commands.add("(check-sat)");
    commands.add("(get-model)");
    return commands;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p24/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Pattern hailPattern = Pattern.compile("^(\\-?\\d+),\\s+(\\-?\\d+),\\s+(\\-?\\d+)\\s+@\\s+(\\-?\\d+),\\s+(\\-?\\d+),\\s+(\\-?\\d+)$");
    
    List<Hail> hailList = new ArrayList<>();
    
    for (String line : lines) {
      Matcher m = hailPattern.matcher(line);
      m.find();
      hailList.add(new Hail(new Position(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)), Long.parseLong(m.group(3))),
          new Velocity(Long.parseLong(m.group(4)), Long.parseLong(m.group(5)), Long.parseLong(m.group(6)))));
    }
    
    Context ctx = new Context(new HashMap<>());
    Solver solver = ctx.mkSolver();
    IntExpr p0x = ctx.mkIntConst("p0x");
    IntExpr p0y = ctx.mkIntConst("p0y");
    IntExpr p0z = ctx.mkIntConst("p0z");
    
    IntExpr v0x = ctx.mkIntConst("v0x");
    IntExpr v0y = ctx.mkIntConst("v0y");
    IntExpr v0z = ctx.mkIntConst("v0z");
    
    for (int i = 0; i < hailList.size(); i++) {
      Hail hail = hailList.get(i);
      IntExpr t = ctx.mkIntConst(String.format("t%d", i));
      solver.add(ctx.mkEq(ctx.mkAdd(p0x, ctx.mkMul(t, v0x)), ctx.mkAdd(ctx.mkInt(hail.position().x()), ctx.mkMul(t, ctx.mkInt(hail.velocity().x())))));
      solver.add(ctx.mkEq(ctx.mkAdd(p0y, ctx.mkMul(t, v0y)), ctx.mkAdd(ctx.mkInt(hail.position().y()), ctx.mkMul(t, ctx.mkInt(hail.velocity().y())))));
      solver.add(ctx.mkEq(ctx.mkAdd(p0z, ctx.mkMul(t, v0z)), ctx.mkAdd(ctx.mkInt(hail.position().z()), ctx.mkMul(t, ctx.mkInt(hail.velocity().z())))));
    }
    
    IntExpr answer = ctx.mkIntConst("answer");
    solver.add(ctx.mkEq(answer, ctx.mkAdd(p0x, p0y, p0z)));
    Status status = solver.check();
    System.out.println("status is " + status);
    Model model = solver.getModel();
    System.out.println("answer is " + model.getConstInterp(answer));
        
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
