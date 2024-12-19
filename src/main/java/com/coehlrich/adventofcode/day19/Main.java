package com.coehlrich.adventofcode.day19;

import java.util.ArrayList;
import java.util.List;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        String[] parts = input.split("\n\n");
        Char2ObjectMap<List<String>> towels = new Char2ObjectOpenHashMap<>();
        for (String towel : parts[0].split(", ")) {
            towels.putIfAbsent(towel.charAt(0), new ArrayList<>());
            towels.get(towel.charAt(0)).add(towel);
        }

        int part1 = 0;
        long part2 = 0;
        for (String line : parts[1].split("\n")) {
            long possible = isPossible(line.replace("\n", ""), towels, new Int2LongOpenHashMap());
            if (possible > 0) {
                part1++;
                part2 += possible;
            }
        }
        return new Result(part1, part2);
    }

    public long isPossible(String pattern, Char2ObjectMap<List<String>> towels, Int2LongMap cache) {
        if (pattern.length() == 0) {
            return 1;
        } else if (cache.containsKey(pattern.length())) {
            return cache.get(pattern.length());
        }
        long total = 0;
        if (towels.containsKey(pattern.charAt(0))) {
            for (String towel : towels.get(pattern.charAt(0))) {
                if (pattern.startsWith(towel)) {
                    total += isPossible(pattern.substring(towel.length()), towels, cache);
                }
            }
        }
        cache.put(pattern.length(), total);
        return total;
    }

}
