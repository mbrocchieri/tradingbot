package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

public abstract class PrimitiveXsdElement extends XsdElement{
    PrimitiveXsdElement(ParameterElement parameter) {
        super(parameter);
    }

    @Override
    public String getElement() {
        return  "<xsd:element name=\"" + getParameter().getName() + "\" type=\"xsd:" + getXsdType() + "\" />";
    }

    protected abstract String getXsdType();
}
