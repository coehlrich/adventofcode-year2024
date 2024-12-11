package com.coehlrich.adventofcode.day11;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.stream.Stream;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        LongList list = new LongArrayList(Stream.of(input.replace("\n", "").split(" ")).mapToLong(Long::parseLong).toArray());
        Long2LongMap map = new Long2LongOpenHashMap();
        for (long value : list) {
            map.put(value, map.get(value) + 1);
        }
        long part1 = 0;
        for (int i = 0; i < 75; i++) {
            if (i == 25) {
                part1 = map.values().longStream().sum();
            }
//            System.out.println(i);
            Long2LongMap newMap = new Long2LongOpenHashMap();
            for (Long2LongMap.Entry entry : map.long2LongEntrySet()) {
                long value = entry.getLongKey();
                long count = entry.getLongValue();
                double digits = Math.floor(Math.log10(value)) + 1;

                if (value == 0) {
                    newMap.put(1, newMap.get(1) + count);
                } else if (digits % 2 == 0) {
                    long half = (long) Math.pow(10, digits / 2);
                    newMap.put(value / half, newMap.get(value / half) + count);
                    newMap.put(value % half, newMap.get(value % half) + count);
                } else {
                    newMap.put(value * 2024, newMap.get(value * 2024) + count);
                }
            }
            map = newMap;
        }
        return new Result(part1, map.values().longStream().sum());
    }
}
