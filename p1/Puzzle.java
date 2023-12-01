package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Puzzle {
  
  public static int findNumber(String value) {
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (Character.isDigit(c)) {
        return Integer.parseInt("" + c);
      }
    }
    return 0;
  }
  
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p1/input.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    
    for (String line : lines) {
      System.out.println(line);
    }
    
    int sum = 0;
    for (String line : lines) {
      int number = findNumber(line) * 10 + findNumber(new StringBuilder(line).reverse().toString());
      System.out.println(number + "");
      sum += number;
    }
    System.out.println("Sum is " + sum);
  }
}
