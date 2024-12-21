package com.coehlrich.adventofcode.day21;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        List<String> codes = input.lines().toList();
        int part1 = 0;
        long part2 = 0;
        for (String code : codes) {
            int length1 = 0;
            long length2 = 0;
            long value = Integer.parseInt(code.substring(0, 3));
            for (int i = 0; i < code.length(); i++) {
                char character = code.charAt(i);
                length1 += getResult(i == 0 ? 'A' : code.charAt(i - 1), character, 3, false, new Object2LongOpenHashMap<>());
                length2 += getResult(i == 0 ? 'A' : code.charAt(i - 1), character, 26, false, new Object2LongOpenHashMap<>());
            }
            part1 += length1 * value;
            part2 += length2 * value;
        }
        return new Result(part1, part2);
    }

    public long getResult(char previous, char character, int robots, boolean directional, Object2LongMap<State> cache) {
        if (robots == 0) {
            return 1;
        } else if (cache.containsKey(new State(robots, previous, character))) {
            return cache.getLong(new State(robots, previous, character));
        }

        long minLength = Long.MAX_VALUE;
        for (String direction : getDirections(previous, character, directional)) {
            long length = 0;
            for (int i = 0; i < direction.length(); i++) {
                length += getResult(i == 0 ? 'A' : direction.charAt(i - 1), direction.charAt(i), robots - 1, true, cache);
            }
            minLength = Math.min(length, minLength);
        }
        cache.put(new State(robots, previous, character), minLength);
        return minLength;
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
                Set<String> end = getInserts(horizontal, vertical.charAt(0));
                if (ny == 0 && oy > 0 && ox == 0) {
                    end.remove("^".repeat(oy - ny) + ">".repeat(nx - ox));
                } else if (oy == 0 && ny > 0 && nx == 0) {
                    end.remove("<".repeat(ox - nx) + "v".repeat(ny - oy));
                }
                result.addAll(end.stream().map(string -> string + "A").toList());
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

    public record State(int robots, char previous, char current) {
    }

}
