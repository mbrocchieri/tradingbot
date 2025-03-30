package org.tradingbot.common.xsd;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.tradingbot.common.Strings;
import org.tradingbot.common.xsd.ce.ClassElement;
import org.tradingbot.common.xsd.ce.ClassElementFactory;
import org.tradingbot.common.xsd.element.ElementNotSupportedException;
import org.tradingbot.common.xsd.element.XsdElement;
import org.tradingbot.common.xsd.element.XsdElementFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class StrategyXsdGenerator {

    public static void main(String[] args) throws FileNotFoundException {
        JavaParser javaParser = new JavaParser();

        // read ta4j source code
        File root = new File(System.getProperty("user.home") + "/dev/ta4j/ta4j-core/src/main/java/org/ta4j/core");
        Map<String, ClassElement> map = readAllFiles(javaParser, root);

        // read tradingbot source code
        root = new File(System.getProperty("user.home") + "/dev/tradingbot/src/main/java/org/tradingbot");
        map.putAll(readAllFiles(javaParser, root));

        ClassElementFactory.computeParents(map);

        // generate xsd
        var s = generateXsd(map);
        s = Strings.prettyFormatXml(s);
        System.out.println(s);
    }

    private static String generateXsd(Map<String, ClassElement> classNameToCompilationUnit) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?><xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xsd:element name=\"rule\" abstract=\"true\" /><xsd:element name=\"indicator\" abstract=\"true\" />" +
                "<xsd:element name=\"strategy\"><xsd:complexType><xsd:sequence><xsd:element name=\"entryRule\">" +
                "<xsd:complexType><xsd:sequence><xsd:element ref=\"rule\" /></xsd:sequence></xsd:complexType>" +
                "</xsd:element><xsd:element name=\"exitRule\"><xsd:complexType><xsd:sequence>" +
                "<xsd:element ref=\"rule\" /></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence>" +
                "</xsd:complexType></xsd:element>");

        Set<ClassElement> toDefineAfter = new HashSet<>();
        Set<String> preDefinitionSet = new HashSet<>();
        for (var compilationUnit : classNameToCompilationUnit.values()) {
            try {
                if (compilationUnit.isChildOf(Indicator.class)) {
                    sb.append(toXsd(compilationUnit, "indicator", classNameToCompilationUnit, toDefineAfter,
                            preDefinitionSet));
                }
                if (compilationUnit.isChildOf(Rule.class)) {
                    sb.append(toXsd(compilationUnit, "rule", classNameToCompilationUnit, toDefineAfter,
                            preDefinitionSet));
                }
            } catch (ElementNotSupportedException e) {
                //                e.printStackTrace();
            }
        }
        for (var s : preDefinitionSet) {
            sb.append(s);
        }

        sb.append("</xsd:schema>");
        return sb.toString();
    }

    private static String toXsd(ClassElement classElement, String strType,
                                Map<String, ClassElement> classNameToClassElement, Set<ClassElement> toDefineAfter,
                                Set<String> preDefinitionSet) throws ElementNotSupportedException {

        if (!classElement.isInstantiable()) {
            return "<xsd:element name=\"" + classElement.getClassName() + "\" abstract=\"true\" />";
        }


        StringBuilder sb = new StringBuilder();
        sb.append("<xsd:element name=\"").append(classElement.getClassName()).append("\" substitutionGroup=\"")
                .append(strType).append("\"");

        List<List<XsdElement>> xsdConstructor = new ArrayList<>();
        for (var constructor : classElement.getConstructors()) {
            List<XsdElement> parameters = new ArrayList<>();
            xsdConstructor.add(parameters);
            for (var parameter : constructor.getParameters()) {
                try {
                    var optionalXsdElement = XsdElementFactory.build(parameter, classNameToClassElement, toDefineAfter);
                    if (optionalXsdElement.isPresent()) {
                        var xsdElement = optionalXsdElement.get();
                        if (xsdElement.getPredefinition().isPresent()) {
                            preDefinitionSet.add(xsdElement.getPredefinition().get());
                        }
                        parameters.add(xsdElement);
                    }
                } catch (ElementNotSupportedException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        var sequence = generateSequence(xsdConstructor, preDefinitionSet);
        if (sequence.isEmpty()) {
            sb.append(" />");
        } else {
            sb.append("><xsd:complexType>");
            sb.append(sequence);
            sb.append("</xsd:complexType></xsd:element>");
        }
        return sb.toString();
    }

    private static String generateSequence(List<List<XsdElement>> xsdConstructor, Set<String> preDefinitionSet)
            throws ElementNotSupportedException {
        var custom = generateCustomElement(xsdConstructor);
        if (custom != null) {
            return custom;
        }


        Set<String> xsdConstructors = new HashSet<>();
        for (var parameters : xsdConstructor) {
            StringBuilder sb = new StringBuilder();
            for (var parameter : parameters) {
                sb.append(parameter.getElement());
                if (parameter.getPredefinition().isPresent()) {
                    preDefinitionSet.add(parameter.getPredefinition().get());
                }
            }
            if (sb.length() > 0) {
                xsdConstructors.add("<xsd:sequence>" + sb + "</xsd:sequence>");
            }
        }
        StringBuilder sb = new StringBuilder();
        if (xsdConstructors.size() > 1) {
            sb.append("<xsd:choice>");
            for (var s : xsdConstructors) {
                sb.append(s);
            }
            sb.append("</xsd:choice>");
        } else if (!xsdConstructors.isEmpty()) {
            sb.append(xsdConstructors.iterator().next());
        }

        return sb.toString();
    }

    private static String generateCustomElement(List<List<XsdElement>> xsdConstructors)
            throws ElementNotSupportedException {
        if (xsdConstructors.size() < 2) {
            return null;
        }
        int max = 0;
        for (var constructor : xsdConstructors) {
            max = Math.max(max, constructor.size());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<xsd:sequence>");
        for (int i = 0; i < max; i++) {
            List<String> parametersList = new ArrayList<>();
            Set<String> parametersSet = new HashSet<>();
            for (var constructor : xsdConstructors) {
                if (i < constructor.size()) {
                    final var element = constructor.get(i).getElement();
                    parametersSet.add(element);
                    parametersList.add(element);
                }
            }
            if (parametersList.size() == xsdConstructors.size()) {
                if (parametersSet.size() == 1) {
                    sb.append(parametersSet.iterator().next());
                } else {
                    sb.append("<xsd:choice>");
                    for (var s : parametersSet) {
                        sb.append(s);
                    }
                    sb.append("</xsd:choice>");
                }
            } else {
                if (parametersSet.size() == 1) {
                    final var next = parametersSet.iterator().next();
                    String element = next.substring(0, next.length() - 2) + " minOccurs=\"0\" />";
                    sb.append(element);
                } else {
                    return "";
                }
            }
        }
        sb.append("</xsd:sequence>");
        return sb.toString();
    }

    private static Map<String, ClassElement> readAllFiles(JavaParser javaParser, File file)
            throws FileNotFoundException {
        Map<String, ClassElement> map = new HashMap<>();
        if (file.isFile()) {
            if (file.getName().contains("DayOfWeekRule")) {
                return Collections.emptyMap();
            }
            ParseResult<CompilationUnit> result = javaParser.parse(file);
            if (result.getResult().isPresent() && result.getResult().get().getTypes().isNonEmpty()) {
                for (var classElement : ClassElementFactory.create(result.getResult().get())) {
                    map.put(classElement.getClassName(), classElement);
                }
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (var f : files) {
                    map.putAll(readAllFiles(javaParser, f));
                }
            }
        }
        return map;
    }
}
