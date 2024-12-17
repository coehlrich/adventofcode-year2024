package com.coehlrich.adventofcode.day17;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main implements Day {

    private static final Pattern REGISTER = Pattern.compile("Register ([A-C]): (\\d+)");

    @Override
    public Result execute(String input) {
        String[] parts = input.split("\n\n");
        int a = 0;
        int b = 0;
        int c = 0;
        for (String line : parts[0].lines().toList()) {
            Matcher matcher = REGISTER.matcher(line);
            matcher.matches();
            int value = Integer.parseInt(matcher.group(2));
            switch (matcher.group(1)) {
                case "A" -> a = value;
                case "B" -> b = value;
                case "C" -> c = value;
            }
        }

        Int2ObjectMap<Function<State, OperandResult>> operands = new Int2ObjectOpenHashMap<>();
        // adv
        operands.put(0, state -> new OperandResult(state.a() >> state.operand(), state.b(), state.c(), -1, -1));
        // bxl
        operands.put(1, state -> new OperandResult(state.a(), state.b() ^ state.literal(), state.c(), -1, -1));
        // bst
        operands.put(2, state -> new OperandResult(state.a(), state.operand() % 8, state.c(), -1, -1));
        // jnz
        operands.put(3, state -> new OperandResult(state.a(), state.b(), state.c(), state.a() == 0 ? -1 : state.literal(), -1));
        // bxc
        operands.put(4, state -> new OperandResult(state.a(), state.b() ^ state.c(), state.c(), -1, -1));
        // out
        operands.put(5, state -> new OperandResult(state.a(), state.b(), state.c(), -1, (int) (state.operand() % 8)));
        // bdv
        operands.put(6, state -> new OperandResult(state.a(), state.a() >> state.operand(), state.c(), -1, -1));
        // cdv
        operands.put(7, state -> new OperandResult(state.a(), state.b(), state.a() >> state.operand(), -1, -1));

        int[] program = Stream.of(parts[1].replace("Program: ", "").replace("\n", "").split(",")).mapToInt(Integer::parseInt).toArray();

        int[] output = null;
//        int part2 = -1;
//        while (!Arrays.equals(program, output)) {
//            part2++;
//            output = calculate(part2, b, c, operands, program);
//        }
        IntList programList = new IntArrayList(program);
        int[] testProgram = programList.subList(0, programList.size() - 2).toIntArray(); // Remove loop

        long result = calculatePart2(program.length - 1, 0, b, c, program, testProgram, operands);
//        System.out.println(Arrays.toString(calculate(result, b, c, operands, program)));
//        System.out.println(Arrays.toString(program));
        return new Result(String.join(",", IntStream.of(calculate(a, b, c, operands, program)).mapToObj(Integer::toString).toArray(String[]::new)), result);
    }

    public long calculatePart2(int index, long result, long b, long c, int[] program, int[] testProgram, Int2ObjectMap<Function<State, OperandResult>> operands) {
        int value = program[index];
        for (int i = 0; i < 8; i++) {
            long tmp = result | (long) i;
            int[] testResult = calculate(tmp, b, c, operands, testProgram);
            if (value == testResult[0] && (index != program.length - 1 || i != 0)) {
                if (index > 0) {
                    long part2 = calculatePart2(index - 1, (result | i) << 3, b, c, program, testProgram, operands);
                    if (part2 != -1) {
                        return part2;
                    }
                } else {
                    return result | i;
                }
            }
        }
        return -1;
    }

    public int[] calculate(long a, long b, long c, Int2ObjectMap<Function<State, OperandResult>> operands, int[] program) {
        IntList output = new IntArrayList();
        for (int i = 0; i < program.length;) {
            int literal = program[i + 1];
            long combo = switch (literal) {
                case 0, 1, 2, 3 -> literal;
                case 4 -> a;
                case 5 -> b;
                case 6 -> c;
                case 7 -> 1000000;
                default -> throw new IllegalArgumentException("Unexpected value: " + literal);
            };
            OperandResult result = operands.get(program[i]).apply(new State(a, b, c, combo, literal));
            a = result.a();
            b = result.b();
            c = result.c();
            i = result.jump() != -1 ? result.jump() : i + 2;
            if (result.out() != -1) {
                output.add(result.out());
            }
        }
        return output.toIntArray();
    }

    public static record State(long a, long b, long c, long operand, int literal) {
    }

    public static record OperandResult(long a, long b, long c, int jump, int out) {
    }

}
