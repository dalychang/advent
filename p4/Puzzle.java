package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
  
  public static List<Integer> toNumbers(String numberString) {
    String[] splitString = numberString.trim().split(" ");
    List<Integer> numbers = new ArrayList<>();
    for (String s : splitString) {
      if (s.trim().isEmpty()) continue;
      numbers.add(Integer.parseInt(s.trim()));
    }
    return numbers;
  }
    
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p4/input.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    Pattern pattern = Pattern.compile("^Card[\\s]+(\\d+): ([\\d\\s]+)\\|([\\d\\s]+)$");
    
    int total = 0;
    for (String line : lines) {
      Matcher matcher = pattern.matcher(line);
      matcher.find();
      String cardNumberString = matcher.group(1);
      String winningNumbersString = matcher.group(2);
      String yourNumbersString = matcher.group(3);
      
      Set<Integer> winningNumbers = new HashSet<>();
      winningNumbers.addAll(toNumbers(winningNumbersString));
      List<Integer> yourNumbers = toNumbers(yourNumbersString);
      
      int number = 0;
      for (Integer yourNumber : yourNumbers) {
        if (winningNumbers.contains(yourNumber)) {
          if (number == 0) {
            number = 1;
          } else {
            number *= 2;
          }
        }
      }
      total += number;
    }
    System.out.println("Total is " + total);
  }
}
