package com.coehlrich.adventofcode.day7;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.stream.Stream;

public record Equation(long value, LongList values) {
    public static Equation parse(String line) {
        String[] parts = line.split(": ");
        long testValue = Long.parseLong(parts[0]);
        long[] values = Stream.of(parts[1].split(" ")).mapToLong(Long::parseLong).toArray();
        return new Equation(testValue, new LongArrayList(values));
    }
}
