package org.tradingbot.common.xsd.element;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.tradingbot.common.xsd.ce.ClassElement;
import org.tradingbot.common.xsd.ce.ParameterElement;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class XsdElementFactory {
    private XsdElementFactory() {}
    public static Optional<XsdElement> build(ParameterElement parameter,
                                             Map<String, ClassElement> classNameToCompilationUnit, Set<ClassElement> toDefine)
            throws ElementNotSupportedException {
        if (parameter.isInt()) {
            return Optional.of(new IntXsdElement(parameter));
        }
        if (parameter.isDouble()) {
            return Optional.of(new DoubleXsdElement(parameter));
        }
        if (parameter.isBoolean()) {
            return Optional.of(new BooleanXsdElement(parameter));
        }
        if (parameter.getTypeName().equals(BarSeries.class.getName())) {
            return Optional.empty();
        }
        if (parameter.getTypeName().equals(Indicator.class.getName())) {
            return Optional.of(new IndicatorXsdElement(parameter));
        }
        if (parameter.getTypeName().equals(Rule.class.getName())) {
            return Optional.of(new RuleXsdElement(parameter));
        }
        if (parameter.isNumber()) {
            return Optional.of(new NumXsdElement(parameter));
        }

        return Optional.of(new ObjectXsdElement(parameter, classNameToCompilationUnit, toDefine));
    }
}
