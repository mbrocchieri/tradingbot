package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class IndicatorXsdElement extends RefXsdElement {
    IndicatorXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getObjectName() {
        return "indicator";
    }
}
