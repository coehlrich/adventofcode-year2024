package com.coehlrich.adventofcode.day18;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        int size = 71;
        List<Point2> bytes = input.lines().map(line -> {
            String[] split = line.split(",");
            return new Point2(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }).toList();
        
        boolean[][] map = new boolean[size][size];
        for (int i = 0; i < 1024; i++) {
            Point2 byteLoc = bytes.get(i);
            map[byteLoc.y()][byteLoc.x()] = true;
        }
        
        Set<Point2> part1 = getSteps(map, size);
        Set<Point2> currentPath = part1;
        int i = 1024;
        while (!currentPath.isEmpty()) {
            i++;
            Point2 next = bytes.get(i);
            map[next.y()][next.x()] = true;
            if (currentPath.contains(next)) {
                currentPath = getSteps(map, size);
            }
        }
        Point2 part2 = bytes.get(i);
        return new Result(part1.size() - 1, part2.x() + "," + part2.y());
    }

    public Set<Point2> getSteps(boolean[][] map, int size) {
        boolean[][] visited = new boolean[size][size];
        Map<Point2, Point2> previous = new HashMap<>();

        Queue<Point2> queue = new ArrayDeque<>();
        queue.add(new Point2(0, 0));
        visited[0][0] = true;
        while (!queue.isEmpty()) {
            Point2 next = queue.poll();
            for (Direction direction : Direction.values()) {
                Point2 moved = direction.offset(next);
                if (moved.x() >= 0 && moved.x() < size
                        && moved.y() >= 0 && moved.y() < size
                        && !map[moved.y()][moved.x()] && !visited[moved.y()][moved.x()]) {
                    if (moved.x() == size - 1 && moved.y() == size - 1) {
                        Set<Point2> path = new HashSet<>();
                        path.add(moved);
                        Point2 current = next;
                        while (current != null) {
                            path.add(current);
                            current = previous.get(current);
                        }
                        return path;
                    } else {
                        visited[moved.y()][moved.x()] = true;
                        previous.put(moved, next);
                        queue.add(moved);
                    }
                }
            }
        }
        return Set.of();
    }

    public static record State(Point2 pos, Set<Point2> steps) {
    }

}
