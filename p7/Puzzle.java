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
import java.util.Collections;

public class Puzzle {
  private static final Map<Character, Integer> CARD_POINTS = new HashMap<>();
  static {
    CARD_POINTS.put('A', 14);
    CARD_POINTS.put('K', 13);
    CARD_POINTS.put('Q', 12);
    CARD_POINTS.put('J', 11);
    CARD_POINTS.put('T', 10);
    CARD_POINTS.put('9', 9);
    CARD_POINTS.put('8', 8);
    CARD_POINTS.put('7', 7);
    CARD_POINTS.put('6', 6);
    CARD_POINTS.put('5', 5);
    CARD_POINTS.put('4', 4);
    CARD_POINTS.put('3', 3);
    CARD_POINTS.put('2', 2);
  }
  
  public static class Hand implements Comparable<Hand> {
    public Map<Character, Integer> characterCount = new HashMap<>();
    public String rawHand;
    public int bid;
    public int maxCount;
    
    public Hand(String rawHand, int bid) {
      this.rawHand = rawHand;
      this.bid = bid;
      
      for (int i = 0; i < rawHand.length(); i++) {
        char character = rawHand.charAt(i);
        characterCount.put(character, characterCount.getOrDefault(character, 0) + 1);
      }
      maxCount = getMaxCount();
    }
    
    private int getMaxCount() {
      int highCount = 0;
      for (Integer count : characterCount.values()) {
        if (count > highCount) {
          highCount = count;
        }
      }
      return highCount;
    }
    
    @Override
    public int compareTo(Hand hand) {
      if (this.rawHand.equals(hand.rawHand)) return 0;
      
      int cardDelta = hand.characterCount.keySet().size() - this.characterCount.keySet().size();
      if (cardDelta != 0) {
        return cardDelta;
      }
      int maxCountDelta = this.maxCount - hand.maxCount;
      if (maxCountDelta != 0) {
        return maxCountDelta;
      }
      for (int i = 0; i < rawHand.length(); i++) {
        int delta = CARD_POINTS.get(this.rawHand.charAt(i)) - CARD_POINTS.get(hand.rawHand.charAt(i));
        if (delta != 0) {
          return delta;
        }
      }
      return 0;
    }
    
    @Override
    public String toString() {
      return rawHand;
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p7/input2.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Hand> hands = new ArrayList<>();
    for (String line : lines) {
      String[] splitString = line.split("\\s+");
      hands.add(new Hand(splitString[0].trim(), Integer.parseInt(splitString[1].trim())));
    }
    Collections.sort(hands);
    System.out.println("hands " + hands);
    
    long points = 0;
    for (int i = 0; i < hands.size(); i++) {
      points += ((i + 1) * hands.get(i).bid);
    }
    
    System.out.println("points = " + points);
    
    long endTime = clock.millis();
    System.out.println("time taken " + (endTime - startTime) + "ms");
  }
}
