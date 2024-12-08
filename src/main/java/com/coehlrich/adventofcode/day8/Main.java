package com.coehlrich.adventofcode.day8;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import com.coehlrich.adventofcode.util.Point2;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        Char2ObjectMap<List<Point2>> antennas = new Char2ObjectOpenHashMap<>();
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] != '.') {
                    antennas.computeIfAbsent(map[y][x], character -> new ArrayList<>()).add(new Point2(x, y));
                }
            }
        }

        int mx = map[0].length;
        int my = map.length;
        Set<Point2> part1 = new HashSet<>();
        Set<Point2> part2 = new HashSet<>();
        for (List<Point2> frequency : antennas.values()) {
            for (int i = 0; i < frequency.size(); i++) {
                for (int j = 0; j < frequency.size(); j++) {
                    if (i != j) {
                        Point2 difference = frequency.get(i).subtract(frequency.get(j));
                        Point2 antinode = frequency.get(i).offset(difference);
                        part2.add(frequency.get(i));
                        if (antinode.x() >= 0 && antinode.x() < mx
                                && antinode.y() >= 0 && antinode.y() < my) {
                            part1.add(antinode);
                        }

                        while (antinode.x() >= 0 && antinode.x() < mx
                                && antinode.y() >= 0 && antinode.y() < my) {
                            part2.add(antinode);
                            antinode = antinode.offset(difference);
                        }
                    }
                }
            }
        }
        return new Result(part1.size(), part2.size());
    }

}
