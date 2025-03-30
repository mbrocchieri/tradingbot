package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class IgnoredXsdElement extends XsdElement {
    IgnoredXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    public String getElement() {
        return "";
    }
}
