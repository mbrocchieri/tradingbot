package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class NumXsdElement extends PrimitiveXsdElement {
    public NumXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getXsdType() {
        return "decimal";
    }
}
