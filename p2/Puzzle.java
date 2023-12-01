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
    
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p2/input.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    
    for (String line : lines) {
      System.out.println(line);
    }
    
  }
}
