package com.coehlrich.adventofcode.day20;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

import java.util.ArrayDeque;
import java.util.Queue;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        Tile[][] map = input
                .lines()
                .map(line -> line
                        .codePoints()
                        .mapToObj(character -> switch ((char) character) {
                            case '#' -> Tile.WALL;
                            case '.' -> Tile.EMPTY;
                            case 'S' -> Tile.START;
                            case 'E' -> Tile.END;
                            default -> throw new IllegalArgumentException("Unexpected value: " + (char) character);
                        })
                        .toArray(Tile[]::new))
                .toArray(Tile[][]::new);

        Point2 start = getPos(Tile.START, map);
        Point2 end = getPos(Tile.END, map);

        int[][] steps = getStepsRequired(map, end);


//        for (int y = 0; y < map.length; y++) {
//            for (int x = 0; x < map[0].length; x++) {
//                if (map[y][x] == Tile.WALL) {
//                    System.out.print('#');
//                } else if (start50.contains(new Point2(x, y))) {
//                    System.out.print('S');
//                } else if (end50.contains(new Point2(x, y))) {
//                    System.out.print('E');
//                } else {
//                    System.out.print('.');
//                }
//            }
//            System.out.println();
//        }
//        for (Int2IntMap.Entry cheats : count.int2IntEntrySet().stream().sorted((e1, e2) -> e1.getIntKey() - e2.getIntKey()).toList()) {
//            System.out.println(cheats);
//        }

        return new Result(getCheats(map, steps, 2), getCheats(map, steps, 20));
    }

    public int getCheats(Tile[][] map, int[][] steps, int limit) {
        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == Tile.EMPTY) {
                    Point2 p1 = new Point2(x, y);
                    for (int dy = -limit; dy <= limit; dy++) {
                        int startX = limit - Math.abs(dy);
                        for (int dx = -startX; dx <= startX; dx++) {
                            Point2 p2 = p1.offset(new Point2(dx, dy));
                            if (p2.x() >= 0 && p2.x() < map[0].length
                                    && p2.y() >= 0 && p2.y() < map.length
                                    && map[p2.y()][p2.x()] == Tile.EMPTY
                                    && steps[p1.y()][p1.x()] > steps[p2.y()][p2.x()]) {
                                int value = steps[p1.y()][p1.x()] - steps[p2.y()][p2.x()] - (Math.abs(dy) + Math.abs(dx));
//                                if (value == 50) {
//                                    start50.add(p1);
//                                    end50.add(p2);
//                                }
                                if (value >= 100) {
                                    result++;
                                }

//                                count.put(value, count.get(value) + 1);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public int[][] getStepsRequired(Tile[][] map, Point2 end) {
        int[][] visited = new int[map.length][map[0].length];

        Queue<State> queue = new ArrayDeque<>();
        queue.add(new State(end, 0));
        visited[end.y()][end.x()] = -1;
        while (!queue.isEmpty()) {
            State next = queue.poll();
            for (Direction direction : Direction.values()) {
                Point2 moved = direction.offset(next.pos());
                if (moved.x() >= 0 && moved.x() < map[0].length
                        && moved.y() >= 0 && moved.y() < map.length
                        && map[moved.y()][moved.x()] == Tile.EMPTY && visited[moved.y()][moved.x()] == 0) {
                    visited[moved.y()][moved.x()] = next.steps() + 1;
                    queue.add(new State(moved, next.steps() + 1));
                }
            }
        }
        visited[end.y()][end.x()] = 0;
        return visited;
    }

    public Point2 getPos(Tile type, Tile[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == type) {
                    map[y][x] = Tile.EMPTY;
                    return new Point2(x, y);
                }
            }
        }
        return null;
    }
    
    public record State(Point2 pos, int steps) {
    }

    public enum Tile {
        WALL, EMPTY, START, END
    }

}
