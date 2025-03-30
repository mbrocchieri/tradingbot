package org.tradingbot.common.xsd.ce;

import com.github.javaparser.ast.body.ConstructorDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ConstructorElement {
    private final ConstructorDeclaration constructor;

    public ConstructorElement(ConstructorDeclaration constructor) {
        this.constructor = constructor;
    }

    public List<ParameterElement> getParameters() {
        List<ParameterElement> list = new ArrayList<>();
        for (var parameter : constructor.getParameters()) {
            list.add(new ParameterElement(parameter));
        }
        return list;
    }
}
