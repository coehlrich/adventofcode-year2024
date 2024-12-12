package com.coehlrich.adventofcode.util;

import java.util.Set;

public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    public static final Set<Direction> HORIZONTAL = Set.of(LEFT, RIGHT);
    public static final Set<Direction> VERTICAL = Set.of(UP, DOWN);

    public final int x;
    public final int y;

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2 offset(Point2 point) {
        return new Point2(point.x() + x, point.y() + y);
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public Direction left() {
        return switch (this) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    public Direction right() {
        return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }
}
