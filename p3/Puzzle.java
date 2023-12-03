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
  
  public static void mark(int y, int x, char[][] schem, boolean[][] bitmap) {
    if (y < 0 || y >= schem.length) return;
    if (x < 0 || x >= schem[0].length) return;
    if (!Character.isDigit(schem[y][x])) return;
    
    bitmap[y][x] = true;
    int left = x - 1;
    int right = x + 1;
    while (left >= 0 && Character.isDigit(schem[y][left])) {
      bitmap[y][left] = true;
      left--;
    }
    while (right < schem[0].length && Character.isDigit(schem[y][right])) {
      bitmap[y][right] = true;
      right++;
    }
  }
    
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p3/input.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    char[][] schem = new char[lines.size()][lines.get(0).length()];
    boolean[][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        schem[i][j] = line.charAt(j);
      }
    }

    for (int i = 0; i < schem.length; i++) {
      //printBitmap(bitmap);
      for (int j = 0; j < schem[0].length; j++) {
        if (schem[i][j] != '.' && !Character.isDigit(schem[i][j])) {
          mark(i - 1, j - 1, schem, bitmap);
          mark(i - 1, j, schem, bitmap);
          mark(i - 1, j + 1, schem, bitmap);
          mark(i + 1, j - 1, schem, bitmap);
          mark(i + 1, j, schem, bitmap);
          mark(i + 1, j + 1, schem, bitmap);
          mark(i, j - 1, schem, bitmap);
          mark(i, j + 1, schem, bitmap);
        }
      }
    }
    
    int sum = 0;
    
    for (int i = 0; i < schem.length; i++) {
      String number = "";
      for (int j = 0; j < schem[0].length; j++) {
        if (bitmap[i][j]) {
          while (j < schem[0].length && Character.isDigit(schem[i][j])) {
            number += schem[i][j];
            j++;
          }
          sum += Integer.parseInt(number);
          number = "";
        }
      }
    }
    
    System.out.println("Sum is " + sum);
  }
  
  private static void printBitmap(boolean[][] bitmap) {
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        System.out.print(bitmap[i][j] ? "X" : ".");
      }
      System.out.print("\n");
    }
    System.out.print("\n");
  }
}
