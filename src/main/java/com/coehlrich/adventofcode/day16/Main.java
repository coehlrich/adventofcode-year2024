package com.coehlrich.adventofcode.day16;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        Type[][] map = input.lines().map(line -> line.chars().mapToObj(character -> switch ((char) character) {
            case '.' -> Type.EMPTY;
            case '#' -> Type.WALL;
            case 'S' -> Type.REINDEER;
            case 'E' -> Type.END;
            default -> throw new IllegalArgumentException("Unexpected value: " + (char) character);
        }).toArray(Type[]::new)).toArray(Type[][]::new);

        Point2 reindeer = findReindeer(map);
        
        Queue<State> queue = new PriorityQueue<>((s1, s2) -> s1.points - s2.points);
        queue.add(new State(new Position(reindeer, Direction.RIGHT), 0, Set.of(reindeer)));
        Object2IntMap<Position> points = new Object2IntOpenHashMap<>();
        Map<Position, Set<Point2>> best = new HashMap<>();
        points.put(new Position(reindeer, Direction.RIGHT), 0);
        int part1 = Integer.MAX_VALUE;
        Set<Point2> bestTiles = new HashSet<>();
        while (!queue.isEmpty()) {
            State next = queue.poll();
            Direction direction = next.pos.direction;
            Point2 pos = next.pos.pos;
            Point2 front = next.pos.direction.offset(next.pos.pos);
            if (map[front.y()][front.x()] == Type.END) {
                if (part1 > next.points + 1) {
                    part1 = next.points + 1;
                    bestTiles.addAll(next.visited);
                } else if (part1 == next.points + 1) {
                    bestTiles.addAll(next.visited);
                }
                continue;
            } else if (map[front.y()][front.x()] == Type.EMPTY && (!points.containsKey(new Position(front, direction)) || points.getInt(new Position(front, direction)) >= next.points + 1)) {
                points.put(new Position(front, direction), next.points + 1);
                queue.add(new State(new Position(front, direction), next.points + 1, Stream.concat(next.visited.stream(), Stream.of(front)).collect(Collectors.toSet())));
            }

            Direction antiClockwise = direction.left();
            if (!points.containsKey(new Position(pos, antiClockwise)) || points.getInt(new Position(pos, antiClockwise)) >= next.points + 1000) {
                points.put(new Position(pos, antiClockwise), next.points + 1000);
                queue.add(new State(new Position(pos, antiClockwise), next.points + 1000, next.visited));
            }

            Direction clockwise = direction.right();
            if (!points.containsKey(new Position(pos, clockwise)) || points.getInt(new Position(pos, clockwise)) >= next.points + 1000) {
                if (points.getInt(new Position(pos, clockwise)) > next.points + 1000) {
                    best.put(new Position(pos, clockwise), new HashSet<>());
                }
                best.computeIfAbsent(new Position(pos, clockwise), p -> new HashSet<>()).addAll(next.visited);
                points.put(new Position(pos, clockwise), next.points + 1000);
                queue.add(new State(new Position(pos, clockwise), next.points + 1000, next.visited));
            }
        }
        
//        Points result = visit(reindeer, Direction.RIGHT, Set.of(new Position(reindeer, Direction.RIGHT)), 0, map, part1);
//        for (int y = 0; y < map.length; y++) {
//            for (int x = 0; x < map[0].length; x++) {
//                if (result.visited.contains(new Point2(x, y))) {
//                    System.out.print('O');
//                } else if (map[y][x] == Type.WALL) {
//                    System.out.print('#');
//                } else {
//                    System.out.print('.');
//                }
//            }
//        }
        return new Result(part1, bestTiles.size() + 1);
    }

    public Points visit(Point2 pos, Direction direction, Set<Position> visited, int points, Type[][] map, int resultMin) {
        Set<Position> best = new HashSet<>();
        Point2 front = direction.offset(pos);
        if (map[front.y()][front.x()] == Type.END) {
            return new Points(points + 1, Stream.concat(visited.stream(), Stream.of(new Position(front, direction))).collect(Collectors.toSet()));
        } else if (map[front.y()][front.x()] == Type.EMPTY && !visited.contains(new Position(front, direction)) && points + 1 < resultMin) {
            Points result = visit(front, direction, Stream.concat(visited.stream(), Stream.of(new Position(front, direction))).collect(Collectors.toSet()), points + 1, map, resultMin);
            if (result.points < resultMin) {
                resultMin = result.points;
                best = new HashSet<>();
                best.addAll(result.visited);
            } else if (result.points == resultMin) {
                best.addAll(result.visited);
            }
        }

        Direction antiClockwise = direction.left();
        if (!visited.contains(new Position(pos, antiClockwise)) && points + 1000 < resultMin) {
            Points antiClockwiseResult = visit(pos, antiClockwise, Stream.concat(visited.stream(), Stream.of(new Position(pos, antiClockwise))).collect(Collectors.toSet()), points + 1000, map, resultMin);
            if (antiClockwiseResult.points < resultMin) {
                resultMin = antiClockwiseResult.points;
                best = new HashSet<>();
                best.addAll(antiClockwiseResult.visited);
            } else if (antiClockwiseResult.points == resultMin) {
                best.addAll(antiClockwiseResult.visited);
            }
        }

        Direction clockwise = direction.right();
        if (!visited.contains(new Position(pos, clockwise)) && points + 1000 < resultMin) {
            Points clockwiseResult = visit(pos, clockwise, Stream.concat(visited.stream(), Stream.of(new Position(pos, clockwise))).collect(Collectors.toSet()), points + 1000, map, resultMin);
            if (clockwiseResult.points < resultMin) {
                resultMin = clockwiseResult.points;
                best = new HashSet<>();
                best.addAll(clockwiseResult.visited);
            } else if (clockwiseResult.points == resultMin) {
                best.addAll(clockwiseResult.visited);
            }
        }
        return new Points(resultMin, best);
    }

    public Point2 findReindeer(Type[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == Type.REINDEER) {
                    map[y][x] = Type.EMPTY;
                    return new Point2(x, y);
                }
            }
        }
        return null;
    }

    public static enum Type {
        EMPTY, WALL, REINDEER, END;
    }

    public static record Position(Point2 pos, Direction direction) {

    }

    public static record State(Position pos, int points, Set<Point2> visited) {

    }

    public static record Points(int points, Set<Position> visited) {
    }

}
