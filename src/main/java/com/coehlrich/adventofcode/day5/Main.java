package com.coehlrich.adventofcode.day5;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        String[] parts = input.split("\n\n");
        Int2ObjectMap<List<Policy>> before = new Int2ObjectOpenHashMap<>(
                parts[0].lines()
                        .map(Policy::parse)
                        .collect(Collectors.toMap(Policy::before, policy -> List.of(policy), (list1, list2) -> {
                            List<Policy> newList = new ArrayList<>();
                            newList.addAll(list1);
                            newList.addAll(list2);
                            return newList;
                        })));

        List<IntList> pages = parts[1].lines()
                .map(line -> Stream.of(line.split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .map(IntList::of)
                .toList();
        int part1 = 0;
        int part2 = 0;
        for (IntList page : pages) {
            Int2ObjectMap<List<Policy>> invalid = check(page, before);
            if (invalid.isEmpty()) {
                part1 += page.getInt(page.size() / 2);
            } else {
                page = new IntArrayList(page);
                while (!invalid.isEmpty()) {
                    for (Int2ObjectMap.Entry<List<Policy>> entry : invalid.int2ObjectEntrySet()) {
                        int min = entry.getValue().stream()
                                .mapToInt(Policy::after)
                                .map(page::indexOf)
                                .min()
                                .getAsInt();
                        int value = page.removeInt(entry.getIntKey());
                        page.add(min, value);
                    }
                    invalid = check(page, before);
                }
                part2 += page.getInt(page.size() / 2);
            }
        }
        return new Result(part1, part2);
    }

    private Int2ObjectMap<List<Policy>> check(IntList page, Int2ObjectMap<List<Policy>> before) {
        Int2ObjectMap<List<Policy>> invalid = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < page.size(); i++) {
            if (before.containsKey(page.getInt(i))) {
                List<Policy> policies = before.get(page.getInt(i));
                for (Policy policy : policies) {
                    int afterIndex = page.indexOf(policy.after());
                    if (afterIndex < i && afterIndex != -1) {
                        invalid.computeIfAbsent(i, i2 -> new ArrayList<>()).add(policy);
                    }
                }
            }
        }
        return invalid;
    }

}
