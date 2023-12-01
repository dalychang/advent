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

public class Puzzlev2 {
  
  static final Map<String, Integer> NUMBERS_MAP = Map.of(
    "one", 1,
    "two", 2,
    "three", 3,
    "four", 4,
    "five", 5,
    "six", 6,
    "seven", 7,
    "eight", 8,
    "nine", 9,
    "zero", 0
  );
  
  public static String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
  }
  
  public static int findNumber(String value, boolean reverse) {
    int numberPosition = value.length() - 1;
    int number = 0;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (Character.isDigit(c)) {
        numberPosition = i;
        number = Integer.parseInt("" + c);
        break;
      }
    }
    
    for (String word : NUMBERS_MAP.keySet()) {
      String wordToFind = reverse ? reverse(word) : word;
      
      int pos = value.indexOf(wordToFind);
      if (pos >= 0 && pos < numberPosition) {
        numberPosition = pos;
        number = NUMBERS_MAP.get(word);
      }
    }

    return number;
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
      int number = findNumber(line, false) * 10 + findNumber(reverse(line), true);
      System.out.println(number + "");
      sum += number;
    }
    System.out.println("Sum is " + sum);

  }
}
