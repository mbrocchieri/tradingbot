package org.tradingbot.common.xsd.element;

import org.tradingbot.common.xsd.ce.ParameterElement;

import java.util.Objects;
import java.util.Optional;

public abstract class XsdElement {
    private final ParameterElement parameter;

    XsdElement(ParameterElement parameter) {
        this.parameter = parameter;
    }

    public abstract String getElement() throws ElementNotSupportedException;

    public ParameterElement getParameter() {
        return parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XsdElement that = (XsdElement) o;
        try {
            return Objects.equals(getElement(), that.getElement());
        } catch (ElementNotSupportedException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter);
    }

    @Override
    public String toString() {
        return "XsdElement{" + "parameter=" + parameter + '}';
    }

    public Optional<String> getPredefinition() {
        return Optional.empty();
    }
}
