package com.coehlrich.adventofcode.day5;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
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
            if (check(page, before)) {
                part1 += page.getInt(page.size() / 2);
            } else {
                Int2ObjectMap<IntList> invalid = new Int2ObjectOpenHashMap<>();
                for (int value : page) {
                    invalid.put(value, new IntArrayList(before.get(value).stream()
                            .mapToInt(Policy::after)
                            .filter(page::contains)
                            .toArray()));
                }
                page = new IntArrayList(page);
                IntSet sorted = new IntOpenHashSet();
                for (Int2ObjectMap.Entry<IntList> entry : invalid.int2ObjectEntrySet()) {
                    sort(page, entry.getIntKey(), entry.getValue(), invalid, sorted);
                }
                part2 += page.getInt(page.size() / 2);
            }
        }
        return new Result(part1, part2);
    }

    private boolean check(IntList page, Int2ObjectMap<List<Policy>> before) {
        for (int i = 0; i < page.size(); i++) {
            if (before.containsKey(page.getInt(i))) {
                List<Policy> policies = before.get(page.getInt(i));
                for (Policy policy : policies) {
                    int afterIndex = page.indexOf(policy.after());
                    if (afterIndex < i && afterIndex != -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void sort(IntList page, int key, IntList value, Int2ObjectMap<IntList> invalid, IntSet sorted) {
        if (sorted.contains(key)) {
            return;
        }
        for (int dependency : value) {
            if (!sorted.contains(dependency) && invalid.containsKey(dependency)) {
                sort(page, dependency, invalid.get(dependency), invalid, sorted);
            }
        }
        OptionalInt min = value.intStream()
                .map(page::indexOf)
                .min();
        if (min.isPresent()) {
            int index = page.indexOf(key);
            if (index > min.getAsInt()) {
                page.removeInt(index);
                page.add(min.getAsInt(), key);
            }
        }
        sorted.add(key);
    }

}
