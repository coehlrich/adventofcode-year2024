package com.coehlrich.adventofcode.day25;

import com.coehlrich.adventofcode.Day;
import com.coehlrich.adventofcode.Result;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main implements Day {

    @Override
    public Result execute(String input) {
        List<IntList> keys = new ArrayList<>();
        List<IntList> locks = new ArrayList<>();
        for (String part : input.split("\n\n")) {
            List<String> lines = part.lines().toList();
            boolean isKey = lines.get(0).equals("#####");
            if (!isKey) {
                lines = lines.reversed();
            }
            IntList list = new IntArrayList();
            for (int i = 0; i < lines.get(0).length(); i++) {
                for (int j = 0; j < lines.size(); j++) {
                    if (lines.get(j).charAt(i) == '.') {
                        list.add(j);
                        break;
                    }
                }
            }
            (isKey ? keys : locks).add(list);
        }

        int result = 0;
        for (IntList key : keys) {
            for (IntList lock : locks) {
                boolean valid = true;
                for (int i = 0; i < key.size(); i++) {
                    if (key.getInt(i) + lock.getInt(i) > 7) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    result++;
                }
            }
        }
        return new Result(result, "N/A");
    }

}
