package com.coehlrich.adventofcode.day9;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import java.util.ArrayList;
import java.util.List;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        List<Sequence> blocks = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            int blockCount = Character.digit(input.charAt(i), 10);
            if (blockCount > 0) {
                blocks.add(new Sequence(i % 2 == 0 ? i / 2 : -1, blockCount));
            }
        }

        List<Sequence> part1 = new ArrayList<>(blocks);
        Sequence current = null;
        for (int i = 0; i < part1.size(); i++) {
            while (current == null || current.id() == -1 || current.length() == 0) {
                current = part1.removeLast();
            }
            Sequence sequence = part1.get(i);
            if (sequence.id() == -1) {
                int length = Math.min(sequence.length(), current.length());
                sequence = new Sequence(-1, sequence.length() - length);
                if (sequence.length() == 0) {
                    part1.remove(i);
                } else {
                    part1.set(i, sequence);
                }

                part1.add(i, new Sequence(current.id(), length));
                current = new Sequence(current.id(), current.length() - length);
            }
        }
        part1.add(current);

        List<Sequence> part2 = new ArrayList<>(blocks);
        for (int i = part2.size() - 1; i >= 0; i--) {
            Sequence sequence = part2.get(i);
            if (sequence.id() != -1) {
                for (int j = 0; j < i; j++) {
                    Sequence newSequence = part2.get(j);
                    if (newSequence.id() == -1 && newSequence.length() >= sequence.length()) {
                        part2.add(j, sequence);
                        int newLength = newSequence.length() - sequence.length();
                        if (newLength > 0) {
                            part2.set(j + 1, new Sequence(-1, newLength));
                            i += 1;
                        } else {
                            part2.remove(j + 1);
                        }
                        part2.set(i, new Sequence(-1, sequence.length()));
                        break;
                    }
                }
            }
        }

        return new Result(checksum(part1), checksum(part2));
    }

    private long checksum(List<Sequence> blocks) {
        int count = 0;
        long result = 0;
        for (Sequence sequence : blocks) {
            if (sequence.id() == -1) {
                count += sequence.length();
            } else {
                for (int i = 0; i < sequence.length(); i++) {
                    result += sequence.id() * count++;
                }
            }
        }
        return result;
    }

}
