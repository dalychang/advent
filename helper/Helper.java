package dev.advent;

import com.google.devtools.build.runfiles.Runfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}
