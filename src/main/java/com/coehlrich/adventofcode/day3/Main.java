package com.coehlrich.adventofcode.day3;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main implements Day {

    private static final Pattern MUL = Pattern.compile("(do|don't|mul)(?:\\(\\)|\\((\\d{1,3}),(\\d{1,3})\\))");

    @Override
    public Result execute(String input) {
        int part1 = 0;
        int part2 = 0;
        Matcher matcher = MUL.matcher(input);
        boolean enable = true;
        while (matcher.find()) {
            switch (matcher.group(1)) {
                case "do" -> enable = true;
                case "don't" -> enable = false;
                case "mul" -> {
                    int result = Integer.parseInt(matcher.group(2)) * Integer.parseInt(matcher.group(3));
                    part1 += result;
                    if (enable) {
                        part2 += result;
                    }
                }
            }
        }
        return new Result(part1, part2);
    }

}
