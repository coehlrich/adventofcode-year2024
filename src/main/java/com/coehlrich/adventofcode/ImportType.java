package com.coehlrich.adventofcode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

public enum ImportType {
    RAW((day, string) -> string),
    FILE((day, string) -> {
        Path path = Path.of(string);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }),
    SESSION((day, string) -> {
        try {
            Path path = Path.of("inputs/day" + day + ".txt");
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                HttpRequest request = HttpRequest
                        .newBuilder(new URI("https://adventofcode.com/2024/day/" + day + "/input"))
                        .header("User-Agent",
                                "coehlrich-AOC-Input-Downloader (https://github.com/coehlrich/adventofcode-year2024/)")
                        .header("Cookie", "session=" + string)
                        .build();
                Util.HTTP_CLIENT.send(request, BodyHandlers.ofFile(path));
            }
            return Files.readString(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    private final BiFunction<Integer, String, String> inputFunction;

    private ImportType(BiFunction<Integer, String, String> input) {
        this.inputFunction = input;
    }

    public String getInput(int day, String arg) {
        return inputFunction.apply(day, arg);
    }
}
