package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev3 {
  public static int WIDTH = 0;
  public static int HEIGHT = 0;
  
  public record Position(int x, int y) {}
  public record MapPosition(int x, int y) {}
  public record GridPosition(int x, int y) {}
  
  public static enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0),
    SOUTH(0, 1);
    
    public int dx;
    public int dy;
    
    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
  };
  
  public static class BorderGrid {
    private final int width;
    private final int height;
    
    private Long[][] horizontal = null;
    private Long[][] vertical = null;
    
    BorderGrid(int width, int height) {
      this.width = width;
      this.height = height;
      
      horizontal = new Long[2][width];
      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < width; j++) {
          horizontal[i][j] = 0L;
        }
      }
      
      vertical = new Long[2][height];
      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < height; j++) {
          vertical[i][j] = 0L;
        }
      }
    }
    
    public long getMax() {
      long max = 0;
      for (int i = 0; i < 2; i++) {
        for (Long value : vertical[i]) {
          if (value > max) max = value;
        }
        for (Long value : horizontal[i]) {
          if (value > max) max = value;
        }
      }
      return max;
    }
    
    public long getH(int layer, int x) {
      return horizontal[layer][x];
    }
    
    public long getV(int layer, int y) {
      return vertical[layer][y];
    }
    
    public void setH(int layer, int x, long value) {
      horizontal[layer][x] = value;
    }
    
    public void setV(int layer, int y, long value) {
      vertical[layer][y] = value;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (horizontal != null) {
        for (int i = 0; i < 2; i++)
          sb.append("h[" + i + "]=" + Arrays.asList(horizontal[i]).toString() + "\n");
      }
      if (vertical != null) {
        for (int i = 0; i < 2; i++)
          sb.append("v[" + i + "]=" + Arrays.asList(vertical[i]).toString() + "\n");
      }
      return sb.toString();
    }
  }
  
  public static void ensureBitmap(Map<MapPosition, int[][]> bitmapMap, MapPosition mapPosition) {
    if (!bitmapMap.containsKey(mapPosition)) {
      bitmapMap.put(mapPosition, new int[HEIGHT][WIDTH]);
    }
  }
  
  private static long count(int remainder, Map<MapPosition, int[][]> bitmapMap) {
    long counter = 0;
    for (int[][] bitmap : bitmapMap.values()) {
      for (int i = 0; i < bitmap.length; i++) {
        for (int j = 0; j < bitmap[0].length; j++) {
          if (bitmap[i][j] > 0 && bitmap[i][j] % 2 == remainder) counter++;
        }
      }
    }
    return counter;
  }
  
  private static boolean isWalkable(boolean [][] bitmap, Position position) {
    if (position.x() < 0 || position.x() >= bitmap[0].length) return false;
    if (position.y() < 0 || position.y() >= bitmap.length) return false;
    return bitmap[position.y()][position.x()];
  }
  
  private static int[][] getTargetBitmap(Map<MapPosition, int[][]> bitmapMap, Position position) {
    MapPosition mapPosition = new MapPosition(
        position.x() < 0 
            ? position.x() / WIDTH - 1
            : position.x() / WIDTH,
        position.y() < 0 
            ? position.y() / HEIGHT - 1
            : position.y() / HEIGHT);
    ensureBitmap(bitmapMap, mapPosition);
    return bitmapMap.get(mapPosition);
  }
  
  private static boolean runPosition(boolean[][] pathBitmap, Map<MapPosition, int[][]> bitmapMap, Position position, long step) {
    int[][] targetBitmap = getTargetBitmap(bitmapMap, position);
    int x = position.x() < 0 ? WIDTH + (position.x() % WIDTH) - 1 : position.x() % WIDTH;
    int y = position.y() < 0 ? HEIGHT + (position.y() % HEIGHT) - 1 : position.y() % HEIGHT;
    if (y >= HEIGHT || x >= WIDTH) 
    System.out.println("x = " + x + ", y = " + y + " p=" + position + " x%w=" + (position.x() % WIDTH) + " y%h=" + (position.y() % HEIGHT));

    if (pathBitmap[y][x] && targetBitmap[y][x] == 0 || targetBitmap[y][x] > step) {
      targetBitmap[y][x] = (int)step;
      return true;
    } else {
      return false;
    }
  }
  
  private static boolean runPosition2(boolean[][] pathBitmap, int[][] targetBitmap, Position position, long step) {
    int x = position.x();
    int y = position.y();
    if (isWalkable(pathBitmap, position) && pathBitmap[y][x] && (targetBitmap[y][x] == 0 || targetBitmap[y][x] > step)) {
      targetBitmap[y][x] = (int)step;
      return true;
    } else {
      return false;
    }
  }
  
  private static void compute2(boolean[][] pathBitmap, int[][] stepBitmap, Position startingPosition) {
    LinkedList<Position> positions = new LinkedList<>();
    positions.add(startingPosition);
    
    long currentStep = 0;
    while (!positions.isEmpty()) {
      currentStep++;
      LinkedList<Position> positionsToCheck = new LinkedList<>(positions);
      positions.clear();
      while (!positionsToCheck.isEmpty()) {
        Position cp = positionsToCheck.poll();
        Position north = new Position(cp.x(), cp.y() - 1);
        Position south = new Position(cp.x(), cp.y() + 1);
        Position west = new Position(cp.x() - 1, cp.y());
        Position east = new Position(cp.x() + 1, cp.y());
        if (runPosition2(pathBitmap, stepBitmap, north, currentStep)) {
          positions.addLast(north);
        }
        if (runPosition2(pathBitmap, stepBitmap, south, currentStep)) {
          positions.addLast(south);
        }
        if (runPosition2(pathBitmap, stepBitmap, west, currentStep)) {
          positions.addLast(west);
        }
        if (runPosition2(pathBitmap, stepBitmap, east, currentStep)) {
          positions.addLast(east);
        }
      }
    }
    
    stepBitmap[startingPosition.y()][startingPosition.x()] = 0;
  }
  
  private static void compute(boolean[][] pathBitmap, Map<MapPosition, int[][]> bitmapMap, long steps, Position startingPosition) {
    LinkedList<Position> positions = new LinkedList<>();
    positions.add(startingPosition);
    
    long currentStep = 0;
    while (!positions.isEmpty() && currentStep < steps) {
      currentStep++;
      if (currentStep % 10000 == 0) {
        System.out.println("Step " + currentStep); 
      }
      
      Map<MapPosition, int[][]> targetBitmapMap = bitmapMap;
      LinkedList<Position> positionsToCheck = new LinkedList<>(positions);
      positions.clear();
      while (!positionsToCheck.isEmpty()) {
        Position cp = positionsToCheck.poll();
        Position north = new Position(cp.x(), cp.y() - 1);
        Position south = new Position(cp.x(), cp.y() + 1);
        Position west = new Position(cp.x() - 1, cp.y());
        Position east = new Position(cp.x() + 1, cp.y());
        if (runPosition(pathBitmap, targetBitmapMap, north, currentStep)) {
          positions.addLast(north);
        }
        if (runPosition(pathBitmap, targetBitmapMap, south, currentStep)) {
          positions.addLast(south);
        }
        if (runPosition(pathBitmap, targetBitmapMap, west, currentStep)) {
          positions.addLast(west);
        }
        if (runPosition(pathBitmap, targetBitmapMap, east, currentStep)) {
          positions.addLast(east);
        }
      }
    }
  }
  
  public static void main(String[] args) throws Exception {
    // input1 is 11
    // input2 is 131
    final List<String> lines = Helper.loadFile("dev_advent/p21/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Position startingPosition = null;
    WIDTH = lines.get(0).length();
    HEIGHT = lines.size();
    boolean [][] bitmap = new boolean[lines.size()][lines.get(0).length()];
    Map<Position, int[][]> bitmapMap = new HashMap<>();
    int[][] startingBitmap = new int[HEIGHT][WIDTH];
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        if (c == 'S') {
          startingPosition = new Position(j, i);
          bitmap[i][j] = true;
        } else if (c == '.') {
          bitmap[i][j] = true;
        } else if (c == '#') {
          bitmap[i][j] = false;
        } else {
          throw new RuntimeException("Unexpected character " + c);
        }
      }
    }
    
    for (int i = 0; i < WIDTH; i++) {
      Position position = new Position(i, 0);
      bitmapMap.put(position, new int[HEIGHT][WIDTH]);
      compute2(bitmap, bitmapMap.get(position), position);
    }
    for (int i = 0; i < WIDTH; i++) {
      Position position = new Position(i, HEIGHT - 1);
      bitmapMap.put(position, new int[HEIGHT][WIDTH]);
      compute2(bitmap, bitmapMap.get(position), position);
    }
    for (int i = 0; i < HEIGHT; i++) {
      Position position = new Position(0, i);
      bitmapMap.put(position, new int[HEIGHT][WIDTH]);
      compute2(bitmap, bitmapMap.get(position), position);
    }
    for (int i = 0; i < HEIGHT; i++) {
      Position position = new Position(WIDTH - 1, i);
      bitmapMap.put(position, new int[HEIGHT][WIDTH]);
      compute2(bitmap, bitmapMap.get(position), position);
    }
    
    compute2(bitmap, startingBitmap, startingPosition);
    
    ConcurrentMap<GridPosition, BorderGrid> gridMap = new ConcurrentHashMap<>();
    GridPosition startPosition = new GridPosition(0, 0);
    BorderGrid startingGrid = new BorderGrid(WIDTH, HEIGHT);
    gridMap.put(startPosition, startingGrid);
    for (int i = 0; i < HEIGHT; i++) {
      startingGrid.setV(1, i, startingBitmap[i][WIDTH - 1]);
      startingGrid.setV(0, i, startingBitmap[i][0]);
    }
    for (int i = 0; i < WIDTH; i++) {
      startingGrid.setH(1, i, startingBitmap[HEIGHT - 1][i]);
      startingGrid.setH(0, i, startingBitmap[0][i]);
    }

    List<Future> futures = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(12);
    final long TARGET_STEPS = 26501365;
    //final long TARGET_STEPS = 5000;
    
    // Set up the cross
    for (Direction direction : Direction.values()) {
      futures.add(executorService.submit(new Callable<Long>() {
        @Override
        public Long call() {
          for (int n = 1; n < Integer.MAX_VALUE; n++) {
            GridPosition gridPosition = new GridPosition(n * direction.dx, n * direction.dy);
            BorderGrid prevGrid = gridMap.get(new GridPosition(direction.dx == 0 ? 0 : (n - 1) * direction.dx, direction.dy == 0 ? 0 : (n - 1) * direction.dy));
            BorderGrid grid = new BorderGrid(WIDTH, HEIGHT);
            gridMap.put(gridPosition, grid);
            
            if (direction.dx != 0) {
              for (int i = 0; i < HEIGHT; i++) {
                grid.setV(Math.max(0, -direction.dx), i, prevGrid.getV(Math.max(0, direction.dx), i) + 1);
              }
            } else if (direction.dy != 0) {
              for (int i = 0; i < WIDTH; i++) {
                grid.setH(Math.max(0, -direction.dy), i, prevGrid.getH(Math.max(0, direction.dy), i) + 1);
              }
            }
            
            if (direction.dx != 0) {
              for (int i = 0; i < HEIGHT; i++) {
                int x = direction.dx > 0 ? 0 : WIDTH - 1;
                int mapX = direction.dx > 0 ? WIDTH - 1 : 0;
                Position p = new Position(x, i);
                int[][] map = bitmapMap.get(p);
                long offset = grid.getV(Math.max(0, -direction.dx), i);
                for (int j = 0; j < HEIGHT; j++) {
                  long delta = offset + map[j][mapX];
                  if (delta < grid.getV(Math.max(0, direction.dx), j) || grid.getV(Math.max(0, direction.dx), j) == 0) {
                    grid.setV(Math.max(0, direction.dx), j, delta);
                  }
                }
                for (int j = 0; j < WIDTH; j++) {
                  long delta = offset + map[0][j];
                  if (delta < grid.getH(0, j) || grid.getH(0, j) == 0) {
                    grid.setH(0, j, delta);
                  }
                }
                for (int j = 0; j < WIDTH; j++) {
                  long delta = offset + map[HEIGHT - 1][j];
                  if (delta < grid.getH(1, j) || grid.getH(1, j) == 0) {
                    grid.setH(1, j, delta);
                  }
                }
              }
            } else if (direction.dy != 0) {
              for (int i = 0; i < WIDTH; i++) {
                int y = direction.dy > 0 ? 0 : HEIGHT - 1;
                int mapY = direction.dy > 0 ? HEIGHT - 1 : 0;
                Position p = new Position(i, y);
                int[][] map = bitmapMap.get(p);
                long offset = grid.getH(Math.max(0, -direction.dy), i);
                for (int j = 0; j < WIDTH; j++) {
                  long delta = offset + map[mapY][j];
                  if (delta < grid.getH(Math.max(0, direction.dy), j) || grid.getH(Math.max(0, direction.dy), j) == 0) {
                    grid.setH(Math.max(0, direction.dy), j, delta);
                  }
                }
                for (int j = 0; j < HEIGHT; j++) {
                  long delta = offset + map[j][0];
                  if (delta < grid.getV(0, j) || grid.getV(0, j) == 0) {
                    grid.setH(0, j, delta);
                  }
                }
                for (int j = 0; j < HEIGHT; j++) {
                  long delta = offset + map[j][WIDTH - 1];
                  if (delta < grid.getH(1, j) || grid.getH(1, j) == 0) {
                    grid.setH(1, j, delta);
                  }
                }
              }
            }
            
            if (grid.getMax() >= TARGET_STEPS) {
              break;
            }
          }
          return 0L;
        }
      }));
    }

    for (Future<Long> future : futures) {
      try {
        Long location = future.get();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("Completed cross at " + (clock.millis() - startTime) + "ms");
    
    // Find the max distance.
    int gridDistance = 0;
    for (GridPosition p : gridMap.keySet()) {
      int distance = Math.max(Math.abs(p.x()), Math.abs(p.y()));
      if (distance > gridDistance) {
        gridDistance = distance;
      }
    }
    System.out.println("gridDistance is " + gridDistance);
    System.out.println(gridMap.get(new GridPosition(gridDistance, 0)));
    
    // Fill in the diagonals.
    for (int d = 1; d <= 1; d++) {
      for (Direction direction : Direction.values()) {
        final int k = d;
        futures.add(executorService.submit(new Callable<Long>() {
          @Override
          public Long call() {
            for (int n = 1; n < Integer.MAX_VALUE; n++) {
              int gridX = n * direction.dx;
              int gridY = n * direction.dy;
              GridPosition gridPosition = new GridPosition(gridX == 0 ? k * direction.dy : gridX, gridY == 0 ? k * direction.dx : gridY);
              BorderGrid prevGrid = gridMap.get(new GridPosition(direction.dx == 0 ? k * direction.dy : (n - 1) * direction.dx, direction.dy == 0 ? k * direction.dx : (n - 1) * direction.dy));
              BorderGrid prevSideGrid = gridMap.get(new GridPosition(direction.dx == 0 ? (k - 1) * direction.dy : gridX, direction.dy == 0 ? (k - 1) * direction.dx : gridY));
              if (prevGrid == null || prevSideGrid == null) {
              //  System.out.println("Missing previous grids for k=" + k + " n=" + n);
                break;
              }
              BorderGrid grid = new BorderGrid(WIDTH, HEIGHT);
              gridMap.put(gridPosition, grid);
              
              if (direction.dx != 0) {
                for (int i = 0; i < HEIGHT; i++) {
                  grid.setV(Math.max(0, -direction.dx), i, prevGrid.getV(Math.max(0, direction.dx), i) + 1);
                }
                for (int i = 0; i < WIDTH; i++) {
                  grid.setH(Math.max(0, direction.dx), i, prevSideGrid.getH(Math.max(0, -direction.dx), i) + 1);
                }
              } else if (direction.dy != 0) {
                for (int i = 0; i < WIDTH; i++) {
                  grid.setH(Math.max(0, -direction.dy), i, prevGrid.getH(Math.max(0, direction.dy), i) + 1);
                }
                for (int i = 0; i < HEIGHT; i++) {
                  grid.setV(Math.max(0, direction.dy), i, prevGrid.getV(Math.max(0, -direction.dy), i) + 1);
                }
              }
              
              if (direction.dx != 0) {
                for (int i = 0; i < HEIGHT; i++) {
                  int x = direction.dx > 0 ? 0 : WIDTH - 1;
                  int mapX = direction.dx > 0 ? WIDTH - 1 : 0;
                  Position p = new Position(x, i);
                  int[][] map = bitmapMap.get(p);
                  long offset = grid.getV(Math.max(0, -direction.dx), i);
                  for (int j = 0; j < HEIGHT; j++) {
                    long delta = offset + map[j][mapX];
                    if (delta < grid.getV(Math.max(0, direction.dx), j) || grid.getV(Math.max(0, direction.dx), j) == 0) {
                      grid.setV(Math.max(0, direction.dx), j, delta);
                    }
                  }
                  for (int j = 0; j < WIDTH; j++) {
                    long delta = offset + map[0][j];
                    if (delta < grid.getH(0, j) || grid.getH(0, j) == 0) {
                      grid.setH(0, j, delta);
                    }
                  }
                  for (int j = 0; j < WIDTH; j++) {
                    long delta = offset + map[HEIGHT - 1][j];
                    if (delta < grid.getH(1, j) || grid.getH(1, j) == 0) {
                      grid.setH(1, j, delta);
                    }
                  }
                }
              } else if (direction.dy != 0) {
                for (int i = 0; i < WIDTH; i++) {
                  int y = direction.dy > 0 ? 0 : HEIGHT - 1;
                  int mapY = direction.dy > 0 ? HEIGHT - 1 : 0;
                  Position p = new Position(i, y);
                  int[][] map = bitmapMap.get(p);
                  long offset = grid.getH(Math.max(0, -direction.dy), i);
                  for (int j = 0; j < WIDTH; j++) {
                    long delta = offset + map[mapY][j];
                    if (delta < grid.getH(Math.max(0, direction.dy), j) || grid.getH(Math.max(0, direction.dy), j) == 0) {
                      grid.setH(Math.max(0, direction.dy), j, delta);
                    }
                  }
                  for (int j = 0; j < HEIGHT; j++) {
                    long delta = offset + map[j][0];
                    if (delta < grid.getV(0, j) || grid.getV(0, j) == 0) {
                      grid.setH(0, j, delta);
                    }
                  }
                  for (int j = 0; j < HEIGHT; j++) {
                    long delta = offset + map[j][WIDTH - 1];
                    if (delta < grid.getH(1, j) || grid.getH(1, j) == 0) {
                      grid.setH(1, j, delta);
                    }
                  }
                }
              }
              
              if (grid.getMax() >= TARGET_STEPS) {
                break;
              }
            }
            return 0L;
          }
        }));
      }

      for (Future<Long> future : futures) {
        try {
          Long location = future.get();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }      
    System.out.println("Completed diagonals at " + (clock.millis() - startTime) + "ms");
    System.out.println(gridMap.get(new GridPosition(gridDistance, 1)));
    System.out.println(gridMap.get(new GridPosition(gridDistance - 1, 1)));
    
    
    executorService.shutdown();

    System.out.println("gridMap size=" + gridMap.size());
    System.out.println(gridMap.get(new GridPosition(202300 - 1, 0)));
//    compute(bitmap, bitmapMap, steps, startingPosition);
//    long answer = count((int)(steps % 2), bitmapMap);
    //Helper.printBitmap(evenBitmapMap.get(new MapPosition(0, 0)), 'o', '.');
    /*
    long answer = lines.stream()
        .map(Puzzle::calculate)
        .reduce(0L, Long::sum);
        */
   /* for (Position mp : bitmapMap.keySet()) {
      System.out.println(mp);
      Helper.printIntMap(bitmapMap.get(mp));
   }*/
   
    System.out.println("Start");
  //    Helper.printIntMap(startingBitmap);
      
   /* int gridSteps = 202300;
      
    int[][] right2 = new int[HEIGHT][gridSteps];
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < gridSteps; j++) {
        right2[i][j] = Integer.MAX_VALUE;
      }
    }
    for (int i = 0; i < HEIGHT; i++) {
      right2[i][0] = startingBitmap[0][i];
    }
    
    for (int k = 1; k < 10; k++) {
      for (int i = 0; i < HEIGHT; i++) {
        Position p = new Position(0, i);
        int[][] map = bitmapMap.get(p);
        int offset = right2[i][k - 1];
        for (int j = 0; j < HEIGHT; j++) {
          int delta = offset + map[j][WIDTH - 1] + 1;
          if (delta < right2[j][k]) {
            right2[j][k] = delta;
          }
        }
      }
    }*/
    //Helper.printIntMap(right2);
    //Helper.printIntMap(bitmapMap.get(new MapPosition(0, 0)));
    //System.out.println("evenBitmapMap is " + evenBitmapMap.keySet());     
    //System.out.println("answer is " + answer);     
    
    
   /* int[][] up = new int[gridSteps][WIDTH];
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < gridSteps; j++) {
        up[j][i] = Integer.MAX_VALUE;
      }
    }
    for (int i = 0; i < WIDTH; i++) {
      up[gridSteps - 1][i] = startingBitmap[0][i];
    }
    
    for (int k = gridSteps - 2; k >= 0; k--) {
      for (int i = 0; i < WIDTH; i++) {
        Position p = new Position(i, HEIGHT - 1);
        int[][] map = bitmapMap.get(p);
        int offset = up[k + 1][i];
        for (int j = 0; j < WIDTH; j++) {
          int delta = offset + map[0][j] + 1;
          if (delta < up[k][j]) {
            up[k][j] = delta;
          }
        }
      }
    }*/
    //Helper.printIntMap(up);
        
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
