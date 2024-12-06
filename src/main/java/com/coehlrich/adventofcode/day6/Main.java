package com.coehlrich.adventofcode.day6;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        Point2 startGuard = findGuard(map);
        Direction direction = Direction.UP;
        Map<Point2, Direction> positions = new HashMap<>();
        Point2 guard = startGuard;
        while (guard.y() >= 0 && guard.y() < map.length
                && guard.x() >= 0 && guard.x() < map[0].length) {
            positions.putIfAbsent(guard, direction);
            Point2 newPos = direction.offset(guard);
            while (newPos.y() >= 0 && newPos.y() < map.length
                    && newPos.x() >= 0 && newPos.x() < map[0].length
                    && map[newPos.y()][newPos.x()] == '#') {
                direction = direction.right();
                newPos = direction.offset(guard);
            }
            guard = newPos;
        }
        int part2 = 0;
        for (Map.Entry<Point2, Direction> extra : positions.entrySet()) {
            if (isLoop(map, extra.getValue().opposite().offset(extra.getKey()), extra.getValue(), extra.getKey())) {
                part2++;
            }
        }
        return new Result(positions.size(), part2);
    }

    private Point2 findGuard(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == '^') {
                    map[y][x] = '.';
                    return new Point2(x, y);

                }
            }
        }
        return null;
    }

    private boolean isLoop(char[][] map, Point2 guard, Direction direction, Point2 extra) {
        Set<State> positions = new HashSet<>();
        while (guard.y() >= 0 && guard.y() < map.length
                && guard.x() >= 0 && guard.x() < map[0].length) {
            if (!positions.add(new State(guard, direction))) {
                return true;
            }
            Point2 newPos = direction.offset(guard);
            while (newPos.y() >= 0 && newPos.y() < map.length
                    && newPos.x() >= 0 && newPos.x() < map[0].length
                    && (map[newPos.y()][newPos.x()] == '#' || newPos.equals(extra))) {
                direction = direction.right();
                newPos = direction.offset(guard);
            }
            if (guard.equals(newPos)) {
                System.out.println("guard = newPos");
            }
            guard = newPos;
        }
        return false;
    }

}
