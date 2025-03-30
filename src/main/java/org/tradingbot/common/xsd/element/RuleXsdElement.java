package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class RuleXsdElement extends RefXsdElement {
    public RuleXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getObjectName() {
        return "rule";
    }
}
