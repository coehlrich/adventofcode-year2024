package com.coehlrich.adventofcode.day7;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        List<Equation> equations = input.lines().map(Equation::parse).toList();
        long part1 = 0;
        long part2 = 0;
        for (Equation equation : equations) {
            if (valid(equation.value(), equation.values().getLong(0), 1, equation.values(), false)) {
                part1 += equation.value();
                part2 += equation.value();
            } else if (valid(equation.value(), equation.values().getLong(0), 1, equation.values(), true)) {
                part2 += equation.value();
            }
        }
        return new Result(part1, part2);
    }

    public boolean valid(long tv, long cv, int i, LongList v, boolean part2) {
        if (i == v.size()) {
            return cv == tv;
        }
        if (cv > tv) {
            return false;
        }

        if (valid(tv, cv + v.getLong(i), i + 1, v, part2)) {
            return true;
        }

        if (valid(tv, cv * v.getLong(i), i + 1, v, part2)) {
            return true;
        }

        if (part2) {
            long m = 1l;
            long n = v.getLong(i);
            while (m <= n) {
                m *= 10;
            }
//            System.out.println("Log10: " + Math.log10(n));
//            System.out.println("Calculation: " + m);
            if (valid(tv, cv * m + n, i + 1, v, part2)) {
                return true;
            }
        }
        return false;
    }

}
