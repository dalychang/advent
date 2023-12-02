package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  
  private static Map<String, Integer> TARGET_MAP = Map.of(
      "red", 12,
      "green", 13,
      "blue", 14);
      
  private static boolean check(Map<String, Integer> maxMap) {
    for (String colorKey : maxMap.keySet()) {
      if (maxMap.get(colorKey) > TARGET_MAP.getOrDefault(colorKey, 0)) {
        return false;
      }
    }
    return true;
  }
  
  private static int power(Map<String, Integer> maxMap) {
    int total = 1;
    for (String colorKey : maxMap.keySet()) {
      total *= maxMap.get(colorKey);
    }
    return total;
  }
    
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p2/input2.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    Map<Integer, Map<String, Integer>> gameMap = new HashMap<>();
    int sum = 0;
    
    Pattern p = Pattern.compile("^Game (\\d+): (.*)$");
    Pattern cubeP = Pattern.compile("^(\\d+) (.*)$");
    for (String line : lines) {
      Map<String, Integer> maxMap = new HashMap<>();
      Matcher m = p.matcher(line);
      m.find();
      int gameId = Integer.parseInt(m.group(1));
      String data = m.group(2);
      String[] draws = data.split(";");
      for (String draw : draws) {
        String[] cubes = draw.split(",");
        for (String cube : cubes) {
          Matcher cubeM = cubeP.matcher(cube.trim());
          cubeM.find();
          String color = cubeM.group(2);
          int count = Integer.parseInt(cubeM.group(1));
          maxMap.put(color, Math.max(maxMap.getOrDefault(color, 0), count));
        }
      }
      
      sum += power(maxMap);
    }
    System.out.println("Sum is " + sum);
  }
}
