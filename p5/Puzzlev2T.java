package dev.advent;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Jingle bells, jingle bells,
 * Brute force all the way!
 */
public class Puzzlev2T {
  private static class RangeMap {
    private Map<Long, Long> sourceToDestMap = new HashMap<>();
    private Map<Long, Long> sourceToRangeMap = new HashMap<>();
   
    public void put(Long source, Long dest, Long range) {
      sourceToDestMap.put(source, dest);
      sourceToRangeMap.put(source, range);
    }
    
    public Long get(Long source) {
      for (long key : sourceToDestMap.keySet()) {
        if (key <= source && source < key + sourceToRangeMap.get(key)) {
          return sourceToDestMap.get(key) + (source - key);
        }
      }
      return source;
    }
  }
  
  private static void addToMap(RangeMap map, String values) {
    String[] split = values.trim().split(" ");
    long range = Long.parseLong(split[2]);
    long source = Long.parseLong(split[1]);
    long dest = Long.parseLong(split[0]);
    map.put(source, dest, range);
  }
  
  private static int addValuesToMap(RangeMap map, List<String> lines, int index) {
    while (true) {
      String line = lines.get(index);
      if (line.trim().isEmpty()) {
        return index;
      }
      addToMap(map, line);
      index++;
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p5/input.txt");
    lines.add("");
    
    Clock clock = Clock.systemUTC();
    
    long startTime = clock.millis();
    
    Pattern seedsPattern = Pattern.compile("^seeds: ([\\d\\s]+)$");
    
    Map<Long, Long> seedToRangeMap = new HashMap<>();
    RangeMap seedToSoilMap = new RangeMap();
    RangeMap soilToFertilizerMap = new RangeMap();
    RangeMap fertilizerToWaterMap = new RangeMap();
    RangeMap waterToLightMap = new RangeMap();
    RangeMap lightToTemperatureMap = new RangeMap();
    RangeMap temperatureToHumidityMap = new RangeMap();
    RangeMap humidityToLocationMap = new RangeMap();
    
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.indexOf("seeds:") >= 0) {
        Matcher m = seedsPattern.matcher(line);
        m.find();
        String[] seedValues = m.group(1).trim().split(" ");
        
        for (int j = 0; j < seedValues.length; j += 2) {
          String seedValue = seedValues[j];
          if (seedValue.trim().isEmpty()) continue;
          String range = seedValues[j + 1];
          seedToRangeMap.put(Long.parseLong(seedValue), Long.parseLong(range));
        }
        i++;
      } else if (line.indexOf("seed-to-soil map:") >= 0) {
        i++;
        i = addValuesToMap(seedToSoilMap, lines, i);
      } else if (line.indexOf("soil-to-fertilizer map:") >= 0) {
        i++;
        i = addValuesToMap(soilToFertilizerMap, lines, i);
      } else if (line.indexOf("fertilizer-to-water map:") >= 0) {
        i++;
        i = addValuesToMap(fertilizerToWaterMap, lines, i);
      } else if (line.indexOf("water-to-light map:") >= 0) {
        i++;
        i = addValuesToMap(waterToLightMap, lines, i);
      } else if (line.indexOf("light-to-temperature map:") >= 0) {
        i++;
        i = addValuesToMap(lightToTemperatureMap, lines, i);
      } else if (line.indexOf("temperature-to-humidity map:") >= 0) {
        i++;
        i = addValuesToMap(temperatureToHumidityMap, lines, i);
      } else if (line.indexOf("humidity-to-location map:") >= 0) {
        i++;
        i = addValuesToMap(humidityToLocationMap, lines, i);
      } else {
        throw new RuntimeException("Unxpected line: " + line);
      }
    }
    
    ExecutorService executorService = Executors.newFixedThreadPool(30);
    List<Future<Long>> futures = new ArrayList<>();
    for (Long seedBase : seedToRangeMap.keySet()) {
      final long seedBaseFinal = seedBase;
      final long range = seedToRangeMap.get(seedBase);
      for (int j = 0; j < Math.ceil((double)range / 10000000); j++) {
        final long start = j * 10000000;
        final long end = Math.min((j + 1) * 10000000, range);
        futures.add(executorService.submit(new Callable<Long>() {
          @Override
          public Long call() {
            Long minLocation = Long.MAX_VALUE;
            for (long i = start; i < end; i++) {
              long seed = seedBaseFinal + i;
              long soil = seedToSoilMap.get(seed);
              long fertilizer = soilToFertilizerMap.get(soil);
              long water = fertilizerToWaterMap.get(fertilizer);
              long light = waterToLightMap.get(water);
              long temp = lightToTemperatureMap.get(light);
              long humidity = temperatureToHumidityMap.get(temp);
              long location = humidityToLocationMap.get(humidity);
              if (location < minLocation) {
                minLocation = location;
              }
            }
            System.out.println(String.format("Done %d-%d of %d for seed %d", start, end, range, seedBaseFinal));
            return minLocation;
          }
        }));
      }
    }
      
    Long minLocation = Long.MAX_VALUE;
    for (Future<Long> future : futures) {
      try {
        Long location = future.get();
        if (location < minLocation) {
          minLocation = location;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    System.out.println("minLocation is " + minLocation);
    
    long endTime = clock.millis();
    
    System.out.println("time taken " + (endTime - startTime) + "ms");
  }
}
