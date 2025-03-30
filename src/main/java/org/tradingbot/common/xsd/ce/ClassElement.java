package org.tradingbot.common.xsd.ce;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator;

import java.util.*;

import static org.tradingbot.common.xsd.ce.ParameterElement.SUB_CLASS_SEPARATOR;

public class ClassElement {
    private final TypeDeclaration<?> typeDeclaration;
    /**
     * super class or interface
     */
    private final Set<ClassElement> parents = new HashSet<>();

    public ClassElement(TypeDeclaration<?> typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
    }

    public String getClassName() {
        final var node = typeDeclaration.getParentNode().orElseThrow();
        if (node instanceof CompilationUnit) {
            return getPackageName() + "." + typeDeclaration.getNameAsString();
        } else {
            var compilationUnit =  (CompilationUnit) node.getParentNode().orElseThrow();
            for (var childNode : compilationUnit.getChildNodes()) {
                if (childNode instanceof ClassOrInterfaceDeclaration) {
                    var name = ((ClassOrInterfaceDeclaration) childNode).asClassOrInterfaceDeclaration().getNameAsString();
                    return getPackageName() + "." + name + SUB_CLASS_SEPARATOR + typeDeclaration.getNameAsString();
                }
            }
            throw new IllegalStateException();
        }
    }

    private String getPackageName() {
        CompilationUnit compilationUnit;

        if (typeDeclaration.getParentNode().isEmpty()) {
            throw new IllegalStateException();
        }
        final var node = typeDeclaration.getParentNode().orElseThrow();
        if (node instanceof CompilationUnit) {
            compilationUnit = (CompilationUnit) node;
        } else {
            // Case of inner class / enum ...
            compilationUnit = (CompilationUnit) node.getParentNode().orElseThrow();
        }
        return compilationUnit.getPackageDeclaration().orElseThrow().getNameAsString();
    }

    public void computeParents(Map<String, ClassElement> map) {
        if (typeDeclaration.isClassOrInterfaceDeclaration()) {
            if (typeDeclaration.asClassOrInterfaceDeclaration().getExtendedTypes().isNonEmpty()) {
                extracted(map, typeDeclaration.asClassOrInterfaceDeclaration().getExtendedTypes());
            }

            if (typeDeclaration.asClassOrInterfaceDeclaration().getImplementedTypes().isNonEmpty()) {
                extracted(map, typeDeclaration.asClassOrInterfaceDeclaration().getImplementedTypes());
            }
        }
    }

    public boolean isInstantiable() {
        var toto = typeDeclaration.asClassOrInterfaceDeclaration();
        return !(toto.isInterface() || toto.isAbstract());
    }

    private void extracted(Map<String, ClassElement> map, NodeList<ClassOrInterfaceType> extendedTypes) {
        for (var implementationType : extendedTypes) {
            String className = getClassNameFormImport(implementationType, typeDeclaration.getParentNode().get().findCompilationUnit().get().getImports());
            final var obj = map.get(className);
            if (obj != null) {
                parents.add(obj);
            } // if obj == null means it is in java or another library
        }
    }

    public String getClassNameFormImport(ClassOrInterfaceType implementationType, NodeList<ImportDeclaration> imports) {
        var className = getClassFullName(implementationType, imports);
        if (className.isEmpty()) {
            // the interface package is the same that the current package
            return getPackageName() + "." + implementationType.getNameAsString();
        }
        return className.get();
    }

    public static Optional<String> getClassFullName(ClassOrInterfaceType implementationType, NodeList<ImportDeclaration> imports) {
        for (var imp : imports) {
            final var nameAsString = imp.getNameAsString();
            var className = nameAsString.substring(nameAsString.lastIndexOf('.') + 1);
            if (className.equals(implementationType.getNameAsString())) {
                return Optional.of(imp.getNameAsString());
            }
        }
        return Optional.empty();
    }

    public boolean isChildOf(Class<?> indicatorClass) {
        return isChildOf(indicatorClass.getName());
    }

    private boolean isChildOf(String indicatorClass) {
        if (getClassName().equals(indicatorClass)) {
            return true;
        }
        if (parents.isEmpty()) {
            return false;
        }
        for (var parent : parents) {
            if (parent.isChildOf(indicatorClass)) {
                return true;
            }
        }
        return false;
    }

    public List<ConstructorElement> getConstructors() {
        List<ConstructorElement> list = new ArrayList<>();
        for (var constructor : typeDeclaration.getConstructors()) {
            list.add(new ConstructorElement(constructor));
        }
        return list;
    }

    @Override
    public String toString() {
        return getClassName();
    }

    public boolean isEnum() {
        return typeDeclaration.isEnumDeclaration();
    }

    public List<String> getEnumValues() {
        if (!isEnum()) {
            throw new IllegalStateException();
        }
        var entries = typeDeclaration.asEnumDeclaration().getEntries();
        List<String> l = new ArrayList<>();
        for (var entry : entries) {
            l.add(entry.getNameAsString());
        }
        return Collections.unmodifiableList(l);
    }
}
