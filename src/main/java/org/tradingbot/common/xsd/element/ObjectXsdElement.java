package org.tradingbot.common.xsd.element;

import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.tradingbot.common.xsd.ce.ClassElement;
import org.tradingbot.common.xsd.ce.ParameterElement;

import java.util.*;

public class ObjectXsdElement extends XsdElement {
    private final Map<String, ClassElement> classNameToClassElement;
    private final Set<ClassElement> toDefine;
    private String preDefinition;

    ObjectXsdElement(ParameterElement parameter, Map<String, ClassElement> classNameToClassElement,
                     Set<ClassElement> toDefine) throws ElementNotSupportedException {
        super(parameter);
        this.classNameToClassElement = classNameToClassElement;
        this.toDefine = toDefine;
        getElement();
    }

    @Override
    public String getElement() throws ElementNotSupportedException {
        final var classElement = classNameToClassElement.get(getParameter().getTypeName());
        if (classElement == null) {
            try {
                final Class<?> aClass = Class.forName(getParameter().getTypeName());
                if (aClass.isEnum()) {
                    List<String> l = new ArrayList<>();
                    for (var e : aClass.getEnumConstants()) {
                        l.add(e.toString());
                    }
                    return getEnum(l);
                }
            } catch (ClassNotFoundException e) {
                throw new ElementNotSupportedException(e);
            }
            throw new ElementNotSupportedException(getParameter().getTypeName());
        } else if (classElement.isEnum()) {
            return getEnum(classElement.getEnumValues());
        } else {
            if (!(classElement.isChildOf(Indicator.class) || classElement.isChildOf(Rule.class))) {
                throw new ElementNotSupportedException(classElement.getClassName());
//                toDefine.add(classElement);
            }
        }

        return "<xsd:element ref=\"" + getParameter().getTypeName() + "\" />";
    }

    private String getEnum(List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xsd:simpleType name=\"").append(getParameter().getTypeName()).append("\" final=\"restriction\" >")
                .append("<xsd:restriction base=\"xsd:string\">");
        for (var s : values) {
            sb.append("<xsd:enumeration value=\"").append(s).append("\" />");
        }

        preDefinition = sb.append("</xsd:restriction></xsd:simpleType>").toString();
        return "<xsd:element name=\"" + getParameter().getName() + "\" type=\"" + getParameter().getTypeName() +
                "\" />";
    }

    @Override
    public Optional<String> getPredefinition() {
        return Optional.ofNullable(preDefinition);
    }
}
