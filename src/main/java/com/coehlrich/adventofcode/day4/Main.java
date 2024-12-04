package com.coehlrich.adventofcode.day4;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

public class Main implements Day {

    private char[][] map = null;

    @Override
    public Result execute(String input) {
        map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        int count = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dy != 0 || dx != 0) {
                            if (check(x, y, dx, dy)) {
                                count++;
                            }
                        }
                    }
                }
            }
        }

        int part2 = 0;
        boolean[][] print = new boolean[map.length][map[0].length];
        for (int y = 1; y < map.length - 1; y++) {
            for (int x = 1; x < map[y].length - 1; x++) {
                for (Direction direction : Direction.values()) {
                    Point2 left = switch (direction) {
                        case DOWN -> new Point2(x - 1, y);
                        case LEFT -> new Point2(x, y + 1);
                        case UP -> new Point2(x + 1, y);
                        case RIGHT -> new Point2(x, y - 1);
                    };

                    Point2 right = switch (direction) {
                        case DOWN -> new Point2(x + 1, y);
                        case LEFT -> new Point2(x, y - 1);
                        case UP -> new Point2(x - 1, y);
                        case RIGHT -> new Point2(x, y + 1);
                    };

                    Point2 bl = direction.offset(left);
                    Point2 br = direction.offset(right);
                    Point2 tl = direction.opposite().offset(left);
                    Point2 tr = direction.opposite().offset(right);
                    if (map[y][x] == 'A'
                            && map[bl.y()][bl.x()] == 'M'
                            && map[br.y()][br.x()] == 'M'
                            && map[tl.y()][tl.x()] == 'S'
                            && map[tr.y()][tr.x()] == 'S') {
//                        for (int dx = -1; dx <= 1; dx++) {
//                            for (int dy = -1; dy <= 1; dy++) {
//                                if (dy != 0 && dx != 0) {
//                                    print[y + dy][x + dx] = true;
//                                }
//                            }
//                        }
//                        print[y][x] = true;
                        part2++;
                    }
                }
            }
        }
        return new Result(count, part2);
    }

    public boolean check(int x, int y, int dx, int dy) {
        if ((x >= 3 || dx > -1)
                && (x <= map[y].length - 4 || dx < 1)
                && (y >= 3 || dy > -1)
                && (y <= map.length - 4 || dy < 1)) {
            return map[y + dy * 0][x + dx * 0] == 'X'
                    && map[y + dy * 1][x + dx * 1] == 'M'
                    && map[y + dy * 2][x + dx * 2] == 'A'
                    && map[y + dy * 3][x + dx * 3] == 'S';
        }
        return false;
    }

}
