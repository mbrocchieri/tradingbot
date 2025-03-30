package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class DoubleXsdElement extends PrimitiveXsdElement {
    DoubleXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getXsdType() {
        return "double";
    }
}
