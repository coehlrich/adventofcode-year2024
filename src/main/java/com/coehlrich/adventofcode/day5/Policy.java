package com.coehlrich.adventofcode.day5;

import java.util.stream.Stream;

public record Policy(int before, int after) {
    public static Policy parse(String line) {
        int[] array = Stream.of(line.split("\\|")).mapToInt(Integer::parseInt).toArray();
        return new Policy(array[0], array[1]);
    }
}
