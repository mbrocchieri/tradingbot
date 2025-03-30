package org.tradingbot.common.xsd.element;

public class ElementNotSupportedException extends Exception {
    public ElementNotSupportedException(Throwable e) {
        super(e);
    }

    public ElementNotSupportedException(String message) {
        super(message);
    }
}
