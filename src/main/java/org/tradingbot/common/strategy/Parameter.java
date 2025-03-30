package org.tradingbot.common.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Parameter {

    private final String name;
    private final int from;
    private final int to;

    private Parameter(String name, int from, int to) {
        this.name = Objects.requireNonNull(name);
        this.from = from;
        this.to = to;
    }

    public static Parameter rangeParameter(String name, int inclusiveFrom, int inclusiveTo) {
        return new Parameter(name, inclusiveFrom, inclusiveTo);
    }

    public static Parameter value(String name, int value) {
        return new Parameter(name, value, value);
    }

    public String getName() {
        return name;
    }

    public Iterator<Integer> iterator() {
        List<Integer> values = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            values.add(i);
        }
        return values.iterator();
    }
}
