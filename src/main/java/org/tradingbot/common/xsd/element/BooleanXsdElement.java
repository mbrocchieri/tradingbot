package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class BooleanXsdElement extends PrimitiveXsdElement {
    BooleanXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getXsdType() {
        return "boolean";
    }
}
