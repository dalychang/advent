package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Helper {
  
  public static List<String> loadFile(String path) {
    try {
      final Runfiles runfiles = Runfiles.create();
      String filePath = runfiles.rlocation(path);
    
      return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  public static void writeFile(String path, List<String> data) {
    try {
      final Runfiles runfiles = Runfiles.create();
      String filePath = runfiles.rlocation(path);
    
      Files.write(Paths.get(filePath), data, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  public static void printBitmap(boolean[][] bitmap, char yesChar, char noChar) {
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        if (bitmap[i][j]) {
          System.out.print(yesChar);
        } else {
          System.out.print(noChar);
        }
      }
      System.out.println("");
    }
    System.out.println("");    
  }
  
  public static void printIntMap(int[][] bitmap) {
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        System.out.print("\t" + bitmap[i][j]);
      }
      System.out.println("");
    }
    System.out.println("");    
  }
  
  public static void printLines(List<String> lines) {
    for (String line : lines) {
      System.out.println(line);
    }
    System.out.println("");    
  }
}
