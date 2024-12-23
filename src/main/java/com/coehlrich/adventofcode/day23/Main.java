package com.coehlrich.adventofcode.day23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        Map<String, Set<String>> connections = new HashMap<>();
        List<Pair<String, String>> connectionList = input.lines().<Pair<String, String>>map(line -> {
            String[] computers = line.split("-");
            return new ObjectObjectImmutablePair<>(computers[0], computers[1]);
        }).toList();
        Set<Set<String>> found = new HashSet<>();
        for (Pair<String, String> pair : connectionList) {
            connections.computeIfAbsent(pair.left(), left -> new HashSet<>()).add(pair.right());
            connections.computeIfAbsent(pair.right(), right -> new HashSet<>()).add(pair.left());
        }
        int part1 = 0;
        Set<String> biggest = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : connections.entrySet()) {
            if (entry.getKey().startsWith("t")) {
                List<String> connected = new ArrayList<>(entry.getValue());
                for (int i = 0; i < connected.size(); i++) {
                    for (int j = i + 1; j < connected.size(); j++) {
                         String first = entry.getKey();
                         String second = connected.get(i);
                         String third = connected.get(j);
                         if (connections.get(second).contains(third) && found.add(Set.of(first, second, third))) {
                             part1++;
                         }
                    }
                }
            }
            Set<String> thisBiggest = getBiggest(connections, new ArrayList<>(entry.getValue()), 0, Set.of(entry.getKey()));
            if (thisBiggest.size() > biggest.size()) {
                biggest = thisBiggest;
            }
        }
        return new Result(part1, String.join(",", biggest.stream().sorted().toArray(String[]::new)));
    }

    public Set<String> getBiggest(Map<String, Set<String>> connections, List<String> connected, int i, Set<String> found) {
        if (i >= connected.size()) {
            return found;
        }
        Set<String> without = getBiggest(connections, connected, i + 1, found);
        if (connections.get(connected.get(i)).containsAll(found)) {
            Set<String> added = new HashSet<>(found);
            added.add(connected.get(i));
            Set<String> with = getBiggest(connections, connected, i + 1, added);
            if (with.size() >= without.size()) {
                return with;
            }
        }
        return without;
    }

}
