package com.coehlrich.adventofcode.day24;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main implements Day {

    private static final Pattern OPERATION = Pattern.compile("([a-z0-9]+) (AND|XOR|OR) ([a-z0-9]+) -> ([a-z0-9]+)");

    @Override
    public Result execute(String input) {
        String[] parts = input.split("\n\n");
        Map<String, Wire> wires = new HashMap<>();
        Object2BooleanMap<String> cache = new Object2BooleanOpenHashMap<>();
        for (String line : parts[0].lines().toList()) {
            String[] split = line.split(": ");
            wires.put(split[0], new Constant(split[1].equals("1")));
        }

        for (String line : parts[1].lines().toList()) {
            Matcher matcher = OPERATION.matcher(line);
            matcher.matches();
            String first = matcher.group(1);
            String second = matcher.group(3);
            wires.put(matcher.group(4), new Operation(matcher.group(1), matcher.group(3), Type.valueOf(matcher.group(2)), switch (matcher.group(2)) {
                case "AND" -> (b1, b2) -> b1 && b2;
                case "OR" -> (b1, b2) -> b1 || b2;
                case "XOR" -> (b1, b2) -> b1 ^ b2;
                default -> throw new IllegalArgumentException("Unexpected value: " + matcher.group(2));
            }, matcher.group(4)));
        }

        long part1 = 0;
        for (int i = 63; i >= 0; i--) {
            String wire = Integer.toString(i);
            if (wire.length() == 1) {
                wire = "0" + wire;
            }
            wire = "z" + wire;
            if (wires.containsKey(wire)) {
                part1 <<= 1;
                if (get(wire, wires, cache)) {
                    part1 |= 0x1;
                }
            }
        }

        Set<String> swapped = new HashSet<>();
        Map<String, Set<Operation>> used = new HashMap<>();
        for (Map.Entry<String, Wire> wire : wires.entrySet()) {
            used.putIfAbsent(wire.getKey(), new HashSet<>());
            if (wire.getValue() instanceof Operation operation) {
                used.computeIfAbsent(operation.input1(), input1 -> new HashSet<>()).add(operation);
                used.computeIfAbsent(operation.input2(), input2 -> new HashSet<>()).add(operation);
            }
        }
//        for (int i = 0; i <= 45; i++) {
//            Queue<Operation> queue = new ArrayDeque<>();
//            queue.add((Operation) wires.get(getWireName(i, "z")));
//            Set<String> xAndY = new HashSet<>();
////            while (!queue.isEmpty()) {
////                Operation next = queue.poll();
////                for (String inputLine : Set.of(next.input1, next.input2)) {
////                    if (wires.get(inputLine))
////                }
////            }
//            System.out.println(wires.get(getWireName(i, "z")));
//        }
        String two = null;
        String otherTwo = null;
        int i = 0;
        Set<Operation> operations = wires.values().stream().filter(Operation.class::isInstance).map(Operation.class::cast).collect(Collectors.toSet());
        do {
            String x = getWireName(i, "x");
            String y = getWireName(i, "y");
            String z = getWireName(i, "z");
            Operation zWire = (Operation) wires.get(z);
            Operation bit = wires.containsKey(x) ? find(x, y, Type.XOR, null, used.get(x)) : null;
            String bitOutput = bit == null ? "" : bit.output;
            if (i == 0) {
                if (bit != zWire) {
                    swapped.add("z00");
                }
                two = find(x, y, Type.AND, null, used.get(x)).output;
            } else if (i == 1) {
                Operation and = find(two, bitOutput, Type.AND, null, used.get(two));
                if (find(two, bit.output, Type.XOR, z, Set.of(zWire)) == null) {
                    if (and == null) {
                        Set<String> inputs = Set.of(zWire.input1, zWire.input2);
                        if (!inputs.contains(two)) {
                            two = zWire.input1.equals(bitOutput) ? zWire.input2 : zWire.input1;
                            swapped.add(two);
                        } else if (!inputs.contains(bitOutput)) {
                            bitOutput = zWire.input1.equals(two) ? zWire.input2 : zWire.input1;
                            swapped.add(bitOutput);
                        }
                        and = find(zWire.input1, zWire.input2, Type.AND, null, used.get(zWire.input1));
                    } else {
                        swapped.add(zWire.output);
                    }
                } else if (and == null) {
                    and = find(zWire.input1, zWire.input2, Type.AND, null, used.get(zWire.input1));
                }
                otherTwo = find(two, bitOutput, Type.AND, null, used.get(two)).output;
                two = find(x, y, Type.AND, null, used.get(x)).output;
            } else {
                Operation or = find(two, otherTwo, Type.OR, null, used.get(two));
                if (or == null) {
                    or = find(two, null, Type.OR, null, used.get(two));
                    if (or != null) {
                        otherTwo = findSwap(or, two);
                        swapped.add(otherTwo);
                    } else {
                        or = find(otherTwo, null, Type.OR, null, used.get(otherTwo));
                        if (or != null) {
                            two = findSwap(or, otherTwo);
                            swapped.add(two);
                        }
                    }

                }

                if (bit != null) {
                    String orOutput = or.output;
                    Operation xor = find(bitOutput, orOutput, Type.XOR, null, used.get(bitOutput));
                    if (xor == null) {
                        xor = find(bitOutput, null, Type.XOR, null, used.get(bitOutput));
                        if (xor != null) {
                            orOutput = findSwap(xor, bitOutput);
                            swapped.add(orOutput);
                        } else {
                            xor = find(orOutput, null, Type.XOR, null, used.get(orOutput));
                            if (xor != null) {
                                bitOutput = findSwap(xor, orOutput);
                                swapped.add(bitOutput);
                            }
                        }
                    }
                    if (xor != zWire) {
                        swapped.add(z);
                    }
                    otherTwo = find(bitOutput, orOutput, Type.AND, null, used.get(bitOutput)).output;
                    two = find(x, y, Type.AND, null, used.get(x)).output;
                } else {
                    if (or != zWire) {
                        swapped.add(z);
                    }
                    otherTwo = null;
                    two = null;
                }
            }
//            System.out.println(swapped);
            i++;
        } while (two != null);
        return new Result(part1, String.join(",", swapped.stream().sorted().toArray(String[]::new)));
    }

    public String findSwap(Operation op, String known) {
        return op.input1.equals(known) ? op.input2 : op.input1;
    }

    public static boolean get(String wire, Map<String, Wire> wires, Object2BooleanMap<String> cache) {
        if (cache.containsKey(wire)) {
            return cache.getBoolean(wire);
        }

        boolean result = wires.get(wire).get(wires, cache);
        cache.put(wire, result);
        return result;
    }

    public String getWireName(int i, String prefix) {
        String wire = Integer.toString(i);
        if (wire.length() == 1) {
            wire = "0" + wire;
        }
        return prefix + wire;
    }

    public String expect(String input1, String input2, Type type, String output, Set<Operation> operations, Map<String, Wire> wires) {
        Operation operation = find(input1, input2, type, output, operations);
        if (operation == null) {
            if (output == null) {
                return find(input1, input2, type, null, wires.values().stream().filter(Operation.class::isInstance).map(Operation.class::cast).collect(Collectors.toSet())).output;
            } else {
                return output;
            }
        }
        return null;
    }

    public Operation find(String input1, String input2, Type type, String output, Set<Operation> operations) {
        Set<String> inputs = new HashSet<>();
        if (input1 != null) {
            inputs.add(input1);
        }
        if (input2 != null) {
            inputs.add(input2);
        }
        for (Operation operation : operations) {
            int correctInputs = 0;
            if (inputs.contains(operation.input1)) {
                correctInputs++;
            }
            if (inputs.contains(operation.input2)) {
                correctInputs++;
            }
            if (correctInputs == inputs.size()
                    && (type == null || operation.type == type)
                    && (output == null || operation.output.equals(output))) {
                return operation;
            }
        }
        return null;
    }

    public interface Wire {
        boolean get(Map<String, Wire> wires, Object2BooleanMap<String> cache);
    }

    public record Operation(String input1, String input2, Type type, BooleanBinaryOperator operator, String output) implements Wire {

        @Override
        public boolean get(Map<String, Wire> wires, Object2BooleanMap<String> cache) {
            return operator.apply(Main.get(input1, wires, cache), Main.get(input2, wires, cache));
        }

        public String toString(Map<String, Wire> wires) {
            String first = wires.get(input1) instanceof Operation op1 ? op1.toString(wires) : input1;
            String second = wires.get(input2) instanceof Operation op2 ? op2.toString(wires) : input2;
            return "(" + first + " " + switch (type) {
                case AND -> "&&";
                case OR -> "||";
                case XOR -> "^";
            } + " " + second + ")";
        }

    }

    public record Constant(boolean value) implements Wire {

        @Override
        public boolean get(Map<String, Wire> wires, Object2BooleanMap<String> cache) {
            return value;
        }

    }

    public enum Type {
        AND, OR, XOR;
    }

}
