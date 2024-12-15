package com.coehlrich.adventofcode.day15;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Direction;
import com.coehlrich.adventofcode.util.Point2;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        return new Result(solve(input),
                solve(input.replace("#", "##").replace("O", "[]").replace(".", "..").replace("@", "@.")));
    }

    private int solve(String input) {
        String[] parts = input.split("\n\n");
        Type[][] map = parts[0].lines()
                .map(line -> line
                        .codePoints()
                        .mapToObj(value -> switch ((char) value) {
                            case '.' -> Type.EMPTY;
                            case '#' -> Type.WALL;
                            case 'O' -> Type.BOX;
                            case '@' -> Type.ROBOT;
                            case '[' -> Type.LEFT_BOX;
                            case ']' -> Type.RIGHT_BOX;
                            default -> throw new IllegalArgumentException("Unexpected value: " + (char) value);
                        })
                .toArray(Type[]::new))
                .toArray(Type[][]::new);
        Point2 robot = findRobot(map);

        List<Direction> directions = new ArrayList<>();
        for (char character : String.join("", parts[1].split("\n")).toCharArray()) {
            directions.add(switch (character) {
                case '^' -> Direction.UP;
                case '>' -> Direction.RIGHT;
                case 'v' -> Direction.DOWN;
                case '<' -> Direction.LEFT;
                default -> throw new IllegalArgumentException("Unexpected value: " + character);
            });
        }

        for (Direction direction : directions) {
            Queue<Point2> boxes = new ArrayDeque<>();
            Point2 current = direction.offset(robot);
            boxes.add(current);
            List<Runnable> moveBoxes = new ArrayList<>();
            List<Runnable> removeBoxes = new ArrayList<>();
            boolean wall = false;
            while (!boxes.isEmpty()) {
                Point2 next = boxes.poll();
                Point2 offset = direction.offset(next);
                if (map[next.y()][next.x()] == Type.BOX) {
                    boxes.add(offset);
                    removeBoxes.add(() -> map[next.y()][next.x()] = Type.EMPTY);
                    moveBoxes.add(() -> map[offset.y()][offset.x()] = Type.BOX);
                } else if (map[next.y()][next.x()] == Type.LEFT_BOX) {
                    if (direction == Direction.RIGHT) {
                        removeBoxes.add(() -> map[next.y()][next.x()] = Type.EMPTY);
                        moveBoxes.add(() -> map[offset.y()][offset.x()] = Type.LEFT_BOX);
                        Point2 right = direction.offset(offset);
                        moveBoxes.add(() -> map[right.y()][right.x()] = Type.RIGHT_BOX);
                        boxes.add(right);
                    } else {
                        removeBoxes.add(() -> map[next.y()][next.x()] = Type.EMPTY);
                        Point2 nextRight = Direction.RIGHT.offset(next);
                        removeBoxes.add(() -> map[nextRight.y()][nextRight.x()] = Type.EMPTY);
                        moveBoxes.add(() -> map[offset.y()][offset.x()] = Type.LEFT_BOX);
                        Point2 right = Direction.RIGHT.offset(offset);
                        moveBoxes.add(() -> map[right.y()][right.x()] = Type.RIGHT_BOX);
                        boxes.add(right);
                        boxes.add(offset);
                    }
                } else if (map[next.y()][next.x()] == Type.RIGHT_BOX) {
                    if (direction == Direction.LEFT) {
                        removeBoxes.add(() -> map[next.y()][next.x()] = Type.EMPTY);
                        moveBoxes.add(() -> map[offset.y()][offset.x()] = Type.RIGHT_BOX);
                        Point2 right = direction.offset(offset);
                        moveBoxes.add(() -> map[right.y()][right.x()] = Type.LEFT_BOX);
                        boxes.add(right);
                    } else {
                        removeBoxes.add(() -> map[next.y()][next.x()] = Type.EMPTY);
                        Point2 nextRight = Direction.LEFT.offset(next);
                        removeBoxes.add(() -> map[nextRight.y()][nextRight.x()] = Type.EMPTY);
                        moveBoxes.add(() -> map[offset.y()][offset.x()] = Type.RIGHT_BOX);
                        Point2 right = Direction.LEFT.offset(offset);
                        moveBoxes.add(() -> map[right.y()][right.x()] = Type.LEFT_BOX);
                        boxes.add(right);
                        boxes.add(offset);
                    }
                } else if (map[next.y()][next.x()] == Type.WALL) {
                    wall = true;
                    break;
                }
            }
            if (!wall) {
                removeBoxes.forEach(Runnable::run);
                moveBoxes.forEach(Runnable::run);
                robot = direction.offset(robot);
            }
        }

        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == Type.BOX || map[y][x] == Type.LEFT_BOX) {
                    result += y * 100 + x;
                }
            }
        }
        return result;
    }

    private Point2 findRobot(Type[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == Type.ROBOT) {
                    map[y][x] = Type.EMPTY;
                    return new Point2(x, y);
                }
            }
        }
        return null;
    }

    public static enum Type {
        EMPTY, WALL, BOX, ROBOT, LEFT_BOX, RIGHT_BOX;
    }

}
