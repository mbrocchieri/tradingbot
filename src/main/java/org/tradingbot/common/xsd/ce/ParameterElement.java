package org.tradingbot.common.xsd.ce;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;

public class ParameterElement {
    public static String SUB_CLASS_SEPARATOR = ".";
    private final Parameter parameter;

    public ParameterElement(Parameter parameter) {
        this.parameter = parameter;
    }

    public String getName() {
        return parameter.getName().getIdentifier();
    }

    public String getTypeName() {
        final var compilationUnit =
                (CompilationUnit) parameter.getParentNode().flatMap(Node::getParentNode).flatMap(Node::getParentNode).get();
        var imports = compilationUnit.getImports();
        var result = ClassElement.getClassFullName(parameter.getType().asClassOrInterfaceType(), imports);
        if (result.isEmpty()) {
            if (parameter.getParentNode().get() instanceof ConstructorDeclaration)  {
                return compilationUnit.getPackageDeclaration().get().getNameAsString() + "." +
                        ((ConstructorDeclaration) parameter.getParentNode().get()).getNameAsString() + SUB_CLASS_SEPARATOR +
                        parameter.getType().asString();

            }
            return compilationUnit.getPackageDeclaration().get().getNameAsString() + "." +
                    parameter.getType().asString();
        }
        return result.get();
    }

    public boolean isInt() {
        return isPrimitive(PrimitiveType.Primitive.INT);
    }

    public boolean isDouble() {
        return isPrimitive(PrimitiveType.Primitive.DOUBLE);
    }

    public boolean isBoolean() {
        return isPrimitive(PrimitiveType.Primitive.BOOLEAN);
    }

    public boolean isNumber() {
        if (!parameter.getType().isClassOrInterfaceType()) {
            return false;
        }
        return parameter.getType().asClassOrInterfaceType().getNameAsString().equals("Num") ||
                parameter.getType().asClassOrInterfaceType().getNameAsString().equals("Number") ||
                parameter.getType().asClassOrInterfaceType().getNameAsString().equals("T");
    }

    private boolean isPrimitive(PrimitiveType.Primitive primitive) {
        return parameter.getType().isPrimitiveType() &&
                ((PrimitiveType) parameter.getType()).getType().equals(primitive);
    }
}
