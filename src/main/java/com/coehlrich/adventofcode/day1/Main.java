package com.coehlrich.adventofcode.day1;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        int[] left = input.lines().map(line -> line.split("   ")[0]).mapToInt(Integer::parseInt).sorted().toArray();
        int[] right = input.lines().map(line -> line.split("   ")[1]).mapToInt(Integer::parseInt).sorted().toArray();
        Map<Integer, Integer> rightMap = IntStream.of(right).mapToObj(Integer::valueOf).collect(Collectors.toMap(Function.identity(), value -> 1, (value1, value2) -> value1 + value2));
        int part1 = 0;
        int part2 = 0;
        for (int i = 0; i < left.length; i++) {
            part1 += Math.abs(right[i] - left[i]);
            part2 += left[i] * rightMap.getOrDefault(left[i], 0);
        }
        return new Result(part1, part2);
    }

}
