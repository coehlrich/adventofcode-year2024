package com.coehlrich.adventofcode.day22;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.HashSet;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        LongList numbers = new LongArrayList(input.lines().mapToLong(Long::parseLong).toArray());
        long part1 = 0;
        Object2IntMap<IntList> part2 = new Object2IntOpenHashMap<>();
        for (long number : numbers) {
            IntList changes = new IntArrayList();
            long previous = -10;
            Set<IntList> found = new HashSet<>();
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
                        int change = (int) (number % 10 - previous);
                        changes.add(change);
                        if (changes.size() > 4) {
                            changes.removeInt(0);
                            if (found.add(new IntArrayList(changes))) {
                                part2.put(new IntArrayList(changes), (int) (number % 10) + part2.getInt(changes));
                            }
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
