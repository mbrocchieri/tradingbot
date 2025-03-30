package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public class IntXsdElement extends PrimitiveXsdElement {
    IntXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    protected String getXsdType() {
        return "integer";
    }


}
