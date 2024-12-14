package com.coehlrich.adventofcode.day14;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Point2;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main implements Day {

    private static final Pattern ROBOT = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

    @Override
    public Result execute(String input) {
        int width = 101;
        int height = 103;

        List<Robot> robots = input.lines().map(line -> {
            Matcher matcher = ROBOT.matcher(line);
            matcher.matches();
            Point2 start = new Point2(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            Point2 velocity = new Point2(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
            return new Robot(start, velocity);
        }).toList();

        int nw = 0;
        int ne = 0;
        int sw = 0;
        int se = 0;

        int part2 = 0;
        for (Robot robot : robots) {
            Point2 end = robot.start().offset(robot.velocity().multiply(100));
            int x = Math.floorMod(end.x(), width);
            int y = Math.floorMod(end.y(), height);

            if (x < width / 2 && y < height / 2) {
                nw++;
            } else if (x > width / 2 && y < height / 2) {
                ne++;
            } else if (x < width / 2 && y > height / 2) {
                sw++;
            } else if (x > width / 2 && y > height / 2) {
                se++;
            }
        }

        for (int i = 0; i < 10000; i++) {
            boolean[][] map = new boolean[height][width];
            for (Robot robot : robots) {
                Point2 end = robot.start().offset(robot.velocity().multiply(i));
                int x = Math.floorMod(end.x(), width);
                int y = Math.floorMod(end.y(), height);

                map[y][x] = true;
            }
            int max = 0;

            for (int x = 0; x < map[0].length; x++) {
                int amount = 0;
                for (int y = 0; y < map.length; y++) {
                    if (map[y][x]) {
                        amount++;
                    } else {
                        max = Math.max(max, amount);
                        amount = 0;
                    }
                }
                max = Math.max(max, amount);
            }
//            System.out.println(amount);
            if (max >= 33) {
                part2 = i;
//                System.out.println((i + 1) + ":");
                for (int y = 0; y < map.length; y++) {
                    for (int x = 0; x < map[0].length; x++) {
                        System.out.print(map[y][x] ? '#' : '.');
                    }
                    System.out.println();
                }
                break;
            }
        }

        return new Result(nw * ne * sw * se, part2);
    }

}
