package com.coehlrich.adventofcode;

import java.util.Locale;

import joptsimple.ValueConverter;

public class CaseInsensitiveEnumArg<T extends Enum<T>> implements ValueConverter<T> {

    private final Class<T> clazz;

    public CaseInsensitiveEnumArg(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convert(String value) {
        return Enum.valueOf(clazz, value.toUpperCase(Locale.ROOT));
    }

    @Override
    public Class<? extends T> valueType() {
        return clazz;
    }

    @Override
    public String valuePattern() {
        return null;
    }

}
