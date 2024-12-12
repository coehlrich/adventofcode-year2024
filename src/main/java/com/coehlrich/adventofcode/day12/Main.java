package com.coehlrich.adventofcode.day12;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        Set<Point2> visited = new HashSet<>();
        int part1 = 0;
        int part2 = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Point2 c = new Point2(x, y);
                if (visited.add(c)) {
                    char region = map[y][x];
                    Queue<Point2> visit = new ArrayDeque<>();
                    visit.add(c);
                    int perimeter = 0;
                    int sides = 0;
                    int area = 0;
                    Set<Edge> edges = new HashSet<>();
                    while (!visit.isEmpty()) {
                        Point2 next = visit.poll();
                        area++;
                        for (Direction direction : Direction.values()) {
                            Point2 neighbour = direction.offset(next);
                            if (isSame(neighbour, region, map)) {
                                if (visited.add(neighbour)) {
                                    visit.add(neighbour);
                                }
                            } else {
                                perimeter++;
                                edges.add(new Edge(neighbour, direction));
                            }
                        }
                    }
                    Set<Edge> found = new HashSet<>();
                    for (Edge edge : edges) {
                        if (found.add(edge)) {
                            sides++;
                            Direction left = edge.direction.left();
                            Point2 toLeft = left.offset(edge.point());
                            while (edges.contains(new Edge(toLeft, edge.direction))) {
                                found.add(new Edge(toLeft, edge.direction));
                                toLeft = left.offset(toLeft);
                            }

                            Direction right = edge.direction.right();
                            Point2 toRight = right.offset(edge.point());
                            while (edges.contains(new Edge(toRight, edge.direction))) {
                                found.add(new Edge(toRight, edge.direction));
                                toRight = right.offset(toRight);
                            }
                        }
                    }

//                    Direction direction = Direction.UP;
//                    Point2 sideCheck = c;
//                    boolean singleTile = false;
//                    if (isSame(direction.offset(sideCheck), region, map)) {
//                        direction = direction.left();
//                        sides++;
//                    } else if (!isSame(direction.right().offset(sideCheck), region, map)) {
//                        while (!isSame(direction.right().offset(sideCheck), region, map)) {
////                            System.out.println("Right turn at: " + sideCheck);
//                            direction = direction.right();
//                            sides++;
//                            if (direction == Direction.UP) {
//                                singleTile = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (!singleTile) {
//                        do {
//                            sideCheck = direction.right().offset(sideCheck);
//
//                            if (isSame(direction.offset(sideCheck), region, map)) {
//                                direction = direction.left();
//                                sides++;
//                            } else if (!isSame(direction.right().offset(sideCheck), region, map)) {
//                                while (!isSame(direction.right().offset(sideCheck), region, map) && (!sideCheck.equals(c) || direction != Direction.UP)) {
////                                    System.out.println("Right turn at: " + sideCheck);
//                                    direction = direction.right();
//                                    sides++;
//                                }
//                            }
//                        } while (!sideCheck.equals(c) || direction != Direction.UP);
//                    }
                    part1 += perimeter * area;
                    part2 += sides * area;
                }
            }
        }
        return new Result(part1, part2);
    }

    private boolean isSame(Point2 neighbour, char plant, char[][] map) {
        return neighbour.x() >= 0 && neighbour.x() < map[0].length
                && neighbour.y() >= 0 && neighbour.y() < map.length
                && map[neighbour.y()][neighbour.x()] == plant;
    }

    private record Edge(Point2 point, Direction direction) {
    }

}
