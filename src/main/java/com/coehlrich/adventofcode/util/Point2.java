package com.coehlrich.adventofcode.util;

public record Point2(int x, int y) {
    public int distance(Point2 o) {
        return Math.abs(o.x - x) + Math.abs(o.y - y);
    }

    public double euclideanDistance(Point2 o) {
        return Math.sqrt(Math.pow(Math.abs(o.x - x), 2) + Math.pow(Math.abs(o.y - y), 2));
    }
}
