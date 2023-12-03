package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Puzzlev2 {
  static boolean isDigit(int y,  int x, char[][] schem) {
    if (y < 0 || y >= schem.length) return false;
    if (x < 0 || x >= schem[0].length) return false;
    return Character.isDigit(schem[y][x]);
  }
  
  static int getNumber(int y,  int x, char[][] schem) {
    if (!isDigit(y, x, schem)) return 1;
    
    int startX = x;
    while (isDigit(y, startX - 1, schem)) {
      startX--;
    }
    String number = "";
    while (isDigit(y, startX, schem)) {
      number += schem[y][startX];
      startX++;
    }
    return Integer.parseInt(number);
  }
  
  static boolean toInclude(int y,  int x, char[][] schem) {
    if (schem[y][x] != '*') return false;
    int adjNumbers = 0;
    if (isDigit(y, x - 1, schem)) {
      adjNumbers++;
    }
    if (isDigit(y, x + 1, schem)) {
      adjNumbers++;
    }
    if (isDigit(y - 1, x, schem)) {
      adjNumbers++;
    } else {
      if (isDigit(y - 1, x - 1, schem)) {
        adjNumbers++;
      }
      if (isDigit(y - 1, x + 1, schem)) {
        adjNumbers++;
      }
    }
    if (isDigit(y + 1, x, schem)) {
      adjNumbers++;
    } else {
      if (isDigit(y + 1, x - 1, schem)) {
        adjNumbers++;
      }
      if (isDigit(y + 1, x + 1, schem)) {
        adjNumbers++;
      }
    }
    return adjNumbers == 2;
  }
    
  public static void main(String[] args) throws Exception {
    final Runfiles runfiles = Runfiles.create();
    String filePath = runfiles.rlocation("dev_advent/p3/input2.txt");
    
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    char[][] schem = new char[lines.size()][lines.get(0).length()];
    boolean[][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        schem[i][j] = line.charAt(j);
      }
    }
    
    int sum = 0;
    for (int i = 0; i < schem.length; i++) {
      //printBitmap(bitmap);
      for (int j = 0; j < schem[0].length; j++) {
        if (toInclude(i, j, schem)) {
          int number = 1;
          number *= getNumber(i, j - 1, schem);
          number *= getNumber(i, j + 1, schem);
          if (isDigit(i - 1, j, schem)) {
            number *= getNumber(i - 1, j, schem);
          } else {
            number *= getNumber(i - 1, j - 1, schem);
            number *= getNumber(i - 1, j + 1, schem);
          }
          if (isDigit(i + 1, j, schem)) {
            number *= getNumber(i + 1, j, schem);
          } else {
            number *= getNumber(i + 1, j - 1, schem);
            number *= getNumber(i + 1, j + 1, schem);
          }
          sum += number;
        }
      }
    }
    
    System.out.println("Sum is " + sum);
  }
}
