package com.coehlrich.adventofcode.day2;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import java.util.stream.Stream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        int[][] reports = input.lines()
                .map(line -> 
                    Stream.of(line.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray())
                .toArray(int[][]::new);
        int part1 = 0;
        int part2 = 0;
        for (int[] report : reports) {
            if (checkReport(report, -1)) {
                part1++;
                part2++;
                continue;
            }

            for (int i = 0; i < report.length; i++) {
                if (checkReport(report, i)) {
                    part2++;
                    break;
                }
            }
        }
        return new Result(part1, part2);
    }

    public boolean checkReport(int[] report, int remove) {
        int direction = 0;
        for (int i = 1; i < report.length; i++) {
            if (i == remove
                    || i == 1 && remove == 0
                    || i == report.length - 1 && remove == report.length - 1) {
                continue;
            }
            int previous = i - 1 == remove ? i - 2 : i - 1;
            int d = report[i] - report[previous];
            if (direction == 0) {
                direction = Math.clamp(d, -1, 1);
                if (direction == 0) {
                    return false;
                }
            }
            int currentDirection = Math.clamp(d, -1, 1);
            if (direction != currentDirection) {
                return false;
            }

            if (Math.abs(d) > 3) {
                return false;
            }
        }
        return true;
    }

}
