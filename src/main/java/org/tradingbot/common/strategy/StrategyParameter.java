package org.tradingbot.common.strategy;

import org.apache.commons.lang3.StringUtils;
import org.tradingbot.common.persistence.ConfigParameterEntity;

import java.math.BigDecimal;
import java.util.*;

public class StrategyParameter {
    private final List<Parameter> parameters = new ArrayList<>();

    public StrategyParameter(List<Parameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public Iterator<Map<String, Integer>> valuesIterator() {
        List<Iterator<Integer>> parametersIterator = new ArrayList<>();
        for (var parameter : parameters) {
            parametersIterator.add(parameter.iterator());
        }
        Map<String, Integer> parametersValues = new HashMap<>();

        return new MapIterator(parametersIterator, parametersValues);
    }

    private class MapIterator implements Iterator<Map<String, Integer>> {
        private final List<Iterator<Integer>> parametersIterator;
        private final Map<String, Integer> parametersValues;
        boolean first;

        public MapIterator(List<Iterator<Integer>> parametersIterator, Map<String, Integer> parametersValues) {
            this.parametersIterator = parametersIterator;
            this.parametersValues = parametersValues;
            first = true;
        }

        @Override
        public boolean hasNext() {
            for (var it : parametersIterator) {
                if (it.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Map<String, Integer> next() {
            if (first) {
                first = false;
                for (var i = 0; i < parameters.size(); ++i) {
                    parametersValues.put(parameters.get(i).getName(), parametersIterator.get(i).next());
                }
            } else {
                for (int i = parametersIterator.size() - 1; i >= 0; --i) {
                    if (parametersIterator.get(i).hasNext()) {
                        int value = parametersIterator.get(i).next();
                        parametersValues.put(parameters.get(i).getName(), value);
                        break;
                    } else if (i != 0) {
                        parametersIterator.set(i, parameters.get(i).iterator());
                        parametersValues.put(parameters.get(i).getName(), parametersIterator.get(i).next());
                    }
                }
            }
            return parametersValues;
        }
    }

    public static Map<String, BigDecimal> toMap(String parameters) {
        Map<String, BigDecimal> m = new HashMap<>();
        var split = StringUtils.split(parameters, ";");
        for (var entry : split) {
            var kv = StringUtils.split(entry, "=");
            m.put(kv[0], new BigDecimal(kv[1].trim()));
        }
        return m;
    }

    public static Map<String, BigDecimal> toMap(List<ConfigParameterEntity> configParameterEntities) {
        Map<String, BigDecimal> m = new HashMap<>();
        for (var c : configParameterEntities) {
            m.put(c.getParameter().getName(), c.getValue());
        }
        return m;
    }

    public static String toString(Map<String, BigDecimal> defaultParameters) {
        if (defaultParameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (var entry : defaultParameters.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(";");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue().toPlainString());
        }
        return sb.toString();
    }
}
