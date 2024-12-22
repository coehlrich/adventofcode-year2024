package com.coehlrich.adventofcode.day22;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        LongList numbers = new LongArrayList(input.lines().mapToLong(Long::parseLong).toArray());
        long part1 = 0;
        Int2IntMap part2 = new Int2IntOpenHashMap();
        for (long number : numbers) {
            int changes = 0;
            long previous = -10;
            IntSet found = new IntOpenHashSet();
            for (int i = 0; i < 2000 * 3; i++) {
                long newNumber = switch (i % 3) {
                    case 0 -> number * 64;
                    case 1 -> number / 32;
                    case 2 -> number * 2048;
                    default -> throw new IllegalArgumentException("Unexpected value: " + i % 3);
                };
                number ^= newNumber;
                number %= 16777216;
                if (i % 3 == 2) {
                    if (previous != -10) {
                        int change = (int) (number % 10 - previous) + 50;
                        if (changes >= 1_00_00_00) {
                            changes %= 1_00_00_00;
                        }
                        changes = changes * 100 + change;
                        if (changes >= 1_00_00_00 && found.add(changes)) {
                            part2.put(changes, (int) (number % 10) + part2.get(changes));
                        }
                    }
                    previous = number % 10;
                }
            }
            part1 += number;
        }
        return new Result(part1, part2.values().intStream().max().getAsInt());
    }

}
