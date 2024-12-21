package com.coehlrich.adventofcode.day21;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        List<String> codes = input.lines().toList();

        return new Result(getResult(codes, 2), 0);
    }

    public int getResult(List<String> codes, int robots) {
        int part1 = 0;
        Char2ObjectMap<Char2ObjectMap<String>> shortest = new Char2ObjectOpenHashMap<>();
        for (char previous : "^A<v>".toCharArray()) {
            Char2ObjectMap<Set<String>> directional1 = new Char2ObjectOpenHashMap<>();
            for (char character : "^A<v>".toCharArray()) {
                Set<String> results = getDirections(previous, character, true);
//                System.out.println(results);
                directional1.put(character, results);
            }

            Char2ObjectMap<Set<String>> previousMap = directional1;
            for (int i = 0; i < robots - 1; i++) {
                System.out.println(i);
                Char2ObjectMap<Set<String>> directional2 = new Char2ObjectOpenHashMap<>();
                for (char character : "^A<v>".toCharArray()) {
                    Set<String> results = new HashSet<>();
                    Set<String> first = previousMap.get(character);
                    Int2ObjectMap<Set<String>> shortMap = new Int2ObjectOpenHashMap<>();
                    for (String string : first) {
                        Set<String> newDirections = new HashSet<>();
                        newDirections.add("");
                        for (int j = 0; j < string.length(); j++) {
                            Set<String> newSet = getDirections(j == 0 ? 'A' : string.charAt(j - 1), string.charAt(j), true);
                            newDirections = newDirections.stream().<String>mapMulti((direction, consumer) -> {
                                for (String newString : newSet) {
                                    consumer.accept(direction + newString);
                                }
                            }).collect(Collectors.toSet());
                            int minLength = newDirections.stream().mapToInt(String::length).min().getAsInt();
                            newDirections = newDirections.stream().filter(direction -> direction.length() == minLength).collect(Collectors.toSet());
                        }
                        int minLength = newDirections.stream().mapToInt(String::length).min().getAsInt();
                        shortMap.putIfAbsent(minLength, new HashSet<>());
                        shortMap.get(minLength).add(string);
                        results.addAll(newDirections.stream().filter(direction -> direction.length() == minLength).toList());
                    }
                    int minLength = results.stream().mapToInt(String::length).min().getAsInt();
                    System.out.println(shortMap.get(minLength));
                    directional2.put(character, Set.of(results.stream().filter(string -> string.length() == minLength).findFirst().get()));
                }
                previousMap = directional2;
            }
            Char2ObjectMap<String> values = new Char2ObjectOpenHashMap<>();
            for (Char2ObjectMap.Entry<Set<String>> entry : previousMap.char2ObjectEntrySet()) {
                int minLength = entry.getValue().stream().mapToInt(String::length).min().getAsInt();
                values.put(entry.getCharKey(), entry.getValue().stream().filter(string -> string.length() == minLength).findFirst().get());
            }
            shortest.put(previous, values);
        }

        for (String code : codes) {
            int length = 0;
            for (int i = 0; i < code.length(); i++) {
                Set<String> directions = getDirections(i == 0 ? 'A' : code.charAt(i - 1), code.charAt(i), false);
                int min = Integer.MAX_VALUE;
//                Set<String> minStrings = new HashSet<>();
                for (String direction : directions) {
//                    StringBuilder builder = new StringBuilder();
                    int thisLength = 0;
                    for (int j = 0; j < direction.length(); j++) {
                        thisLength += shortest.get(j == 0 ? 'A' : direction.charAt(j - 1)).get(direction.charAt(j)).length();
//                        builder.append(directional2.get(character));
                    }
//                    if (thisLength < min) {
//                        minStrings = new HashSet<>();
//                    }
                    min = Math.min(thisLength, min);
//                    if (min == thisLength) {
//                        minStrings.add(builder.toString());
//                    }
                }
//                System.out.println(minStrings);
                length += min;
            }
//            System.out.println(length);
            part1 += length * Integer.parseInt(code.substring(0, 3));
        }
        return part1;
    }

    public Set<String> getDirections(char previous, char character, boolean directional) {
        Set<String> result = new HashSet<>();
        if (directional) {
            int ox = switch (previous) {
                case '<' -> 0;
                case '^', 'v' -> 1;
                case 'A', '>' -> 2;
                default -> throw new IllegalArgumentException("Unexpected value: " + previous);
            };

            int oy = switch (previous) {
                case '^', 'A' -> 0;
                case '<', 'v', '>' -> 1;
                default -> throw new IllegalArgumentException("Unexpected value: " + previous);
            };

            int nx = switch (character) {
                case '<' -> 0;
                case '^', 'v' -> 1;
                case 'A', '>' -> 2;
                default -> throw new IllegalArgumentException("Unexpected value: " + character);
            };

            int ny = switch (character) {
                case '^', 'A' -> 0;
                case '<', 'v', '>' -> 1;
                default -> throw new IllegalArgumentException("Unexpected value: " + character);
            };
            String vertical = (ny > oy ? "v" : "^").repeat(Math.abs(ny - oy));
            String horizontal = (nx > ox ? ">" : "<").repeat(Math.abs(nx - ox));
            if (!vertical.isEmpty()) {

                result.addAll(getInserts(horizontal, vertical.charAt(0)).stream().map(string -> string + "A").toList());
            } else {
                result.add(horizontal + "A");
            }
        } else {
            int od = previous == 'A' ? -1 : Character.digit(previous, 10);
            int nd = character == 'A' ? -1 : Character.digit(character, 10);

            int ox = od <= 0 ? switch (od) {
                case 0 -> 1;
                case -1 -> 2;
                default -> throw new IllegalArgumentException("Unexpected value: " + od);
            } : (od % 3 == 0 ? 3 : od % 3) - 1;

            int oy = od <= 0 ? 3 : 3 - (od % 3 == 0 ? (od / 3) : (od / 3) + 1);

            int nx = nd <= 0 ? switch (nd) {
                case 0 -> 1;
                case -1 -> 2;
                default -> throw new IllegalArgumentException("Unexpected value: " + od);
            } : (nd % 3 == 0 ? 3 : nd % 3) - 1;

            int ny = nd <= 0 ? 3 : 3 - (nd % 3 == 0 ? (nd / 3) : (nd / 3) + 1);
            String vertical = (ny > oy ? "v" : "^").repeat(Math.abs(ny - oy));
            String horizontal = (nx > ox ? ">" : "<").repeat(Math.abs(nx - ox));
            if (horizontal.length() >= 1) {
                Set<String> inserts = getInserts(vertical, horizontal.charAt(0));
                Set<String> end = inserts;
                if (horizontal.length() >= 2) {
                    end = new HashSet<>();
                    for (String insert : inserts) {
                        end.addAll(getInserts(insert, horizontal.charAt(0)));
                    }
                }
                if (ny == 3 && oy < 3 && ox == 0) {
                    end.remove("v".repeat(ny - oy) + ">".repeat(nx - ox));
                } else if (oy == 3 && ny < 3 && nx == 0) {
                    end.remove("<".repeat(ox - nx) + "^".repeat(oy - ny));
                }
                result.addAll(end.stream().map(string -> string + "A").toList());
            } else {
                result.add(vertical + "A");
            }
        }
        return result;
    }

    public Set<String> getInserts(String text, char character) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i <= text.length(); i++) {
            StringBuilder newText = new StringBuilder();
            newText.append(text.substring(0, i));
            newText.append(character);
            newText.append(text.substring(i, text.length()));
            result.add(newText.toString());
        }
        return result;
    }

}
