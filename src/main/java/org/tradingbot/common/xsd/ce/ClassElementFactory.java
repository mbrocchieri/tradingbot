package org.tradingbot.common.xsd.ce;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassElementFactory {
    public static List<ClassElement> create(CompilationUnit compilationUnit) {
        List<ClassElement> list = new ArrayList<>();
        if (compilationUnit.getTypes().isNonEmpty()) {
            for (var candidate : compilationUnit.getTypes()) {
                if (candidate.isClassOrInterfaceDeclaration() || candidate.isEnumDeclaration()) {
                    list.add(new ClassElement(candidate));
                    for (var member : candidate.getMembers()) {
                        if (member.isEnumDeclaration()) {
                            list.add(new ClassElement(member.asEnumDeclaration()));
                        }
                    }
                }
            }
        }
        return list;
    }

    public static void computeParents(Map<String, ClassElement> map) {
        for (var value : map.values()) {
            value.computeParents(map);
        }
    }
}
