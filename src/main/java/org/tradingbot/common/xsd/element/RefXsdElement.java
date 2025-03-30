package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public abstract class RefXsdElement extends XsdElement {
    RefXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    public String getElement() {
        return "<xsd:element ref=\"" + getObjectName() + "\" />";
    }

    protected abstract String getObjectName();
}
