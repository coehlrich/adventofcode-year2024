package com.coehlrich.adventofcode.day13;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Point2;
import it.unimi.dsi.fastutil.longs.LongLongPair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main implements Day {

    Pattern BUTTON_A = Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+)");
    Pattern BUTTON_B = Pattern.compile("Button B: X\\+(\\d+), Y\\+(\\d+)");
    Pattern PRIZE = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");

    @Override
    public Result execute(String input) {
        List<Machine> machines = Stream.of(input.split("\n\n")).map(string -> {
            String[] lines = string.lines().toArray(String[]::new);
            Matcher aMatcher = BUTTON_A.matcher(lines[0]);
            aMatcher.matches();
            Point2 a = new Point2(Integer.parseInt(aMatcher.group(1)), Integer.parseInt(aMatcher.group(2)));

            Matcher bMatcher = BUTTON_B.matcher(lines[1]);
            bMatcher.matches();
            Point2 b = new Point2(Integer.parseInt(bMatcher.group(1)), Integer.parseInt(bMatcher.group(2)));

            Matcher prizeMatcher = PRIZE.matcher(lines[2]);
            prizeMatcher.matches();
            Point2 prize = new Point2(Integer.parseInt(prizeMatcher.group(1)), Integer.parseInt(prizeMatcher.group(2)));
            return new Machine(a, b, prize);
        }).toList();

        long part1 = 0;
        long part2 = 0;
        for (Machine machine : machines) {
            long part1Result = solve(machine.a().x(), machine.a().y(), machine.b().x(), machine.b().y(), machine.prize().x(), machine.prize().y());
            if (part1Result >= 0) {
                part1 += part1Result;
            }

            long part2Result = solve(machine.a().x(), machine.a().y(), machine.b().x(), machine.b().y(), machine.prize().x() + 10000000000000L, machine.prize().y() + 10000000000000L);
            if (part2Result >= 0) {
                part2 += part2Result;
            }
        }
        return new Result(part1, part2);
    }

    private long solve(long ax, long ay, long bx, long by, long px, long py) {
//        px = a * ax + b * bx
//        py = a * ay + b * by
//
//        px - b * bx = a * ax
//        (px - b * bx) / ax = a
//
//        py = ((px - b * bx) / ax) * ay + b * by
//        py * ax = (px - b * bx) * ay + b * by * ax
//        py * ax = (px * ay - b * bx * ay) + b * by * ax
//        py * ax = px * ay + (b * by * ax - b * bx * ay)
//        py * ax - px * ay = b * by * ax - b * bx * ay
//        py * ax - px * ay = b * (by * ax - bx * ay)
//        (py * ax - px * ay) / (by * ax - bx * ay) = b
//
//        px = a * ax + b * bx
//        px - b * bx = a * ax
//        (px - b * bx) / ax = a

        long bTop = py * ax - px * ay;
        long demoninator = by * ax - bx * ay;

        if (bTop % demoninator == 0) {
            long b = bTop / demoninator;
            long aTop = px - b * bx;
            if (aTop % ax == 0) {
                long a = aTop / ax;
                return a * 3 + b;
            }
        }
        return -1;
    }

}
