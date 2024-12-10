package com.coehlrich.adventofcode.day10;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Point2;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        int[][] map = input.lines()
                .map(String::chars)
                .map(intstream -> intstream
                        .map(character -> Character.digit(character, 10))
                        .toArray())
                .toArray(int[][]::new);

        int part1 = 0;
        int part2 = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 0) {
                    Set<Point2> found = new HashSet<>();
                    Queue<Point2> queue = new ArrayDeque<>();
                    queue.add(new Point2(x, y));
                    while (!queue.isEmpty()) {
                        Point2 next = queue.poll();
                        int cx = next.x();
                        int cy = next.y();
                        int current = map[cy][cx];
                        if (current == 9) {
                            if (found.add(next)) {
                                part1++;
                            }
                            part2++;
                        } else {
                            if (cx > 0 && current == map[cy][cx - 1] - 1) {
                                queue.add(new Point2(cx - 1, cy));
                            }

                            if (cx < map.length - 1 && current == map[cy][cx + 1] - 1) {
                                queue.add(new Point2(cx + 1, cy));
                            }

                            if (cy > 0 && current == map[cy - 1][cx] - 1) {
                                queue.add(new Point2(cx, cy - 1));
                            }

                            if (cy < map[0].length - 1 && current == map[cy + 1][cx] - 1) {
                                queue.add(new Point2(cx, cy + 1));
                            }
                        }
                    }
                }
            }
        }
        return new Result(part1, part2);
    }

}
