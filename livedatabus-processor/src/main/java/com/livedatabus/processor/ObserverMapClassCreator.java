package com.livedatabus.processor;

import com.livedatabus.annotion.Observe;
import com.livedatabus.annotion.ThreadMode;
import com.livedatabus.annotion.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * author：gzc
 * date：2021/1/26
 * describe：
 */
public class ObserverMapClassCreator {
    private final String mObserverClassName = "LiveDataObservers";
    private String packageName;

    public ObserverMapClassCreator(String packageName) {
        this.packageName = packageName;
    }


    /**
     * 创建Java代码
     *
     * @return
     */
    public String generateClassCode() {
        StringBuilder classCode = new StringBuilder();
        classCode
                .append("import "+packageName+";\n\n")
                .append("import com.gzc.livedatabus.Observers\n\n")
                .append("public class " + mObserverClassName + " implements Observers{\n")
                .append("private static final Map<String,LiveDataObserver>LIVEDATA_MAP;\n\n")
                .append("static{\n")
                .append("LIVEDATA_MAP = new HashMap<String,LiveDataObserver>();\n\n")
                .append("}\n")
                .append("private static void put")
                .append("\n}");
        return classCode.toString();
    }

    private String getClassString(TypeElement typeElement, String myPackage) {
        PackageElement packageElement = getPackageElement(typeElement);
        String packageString = packageElement.getQualifiedName().toString();
        String className = typeElement.getQualifiedName().toString();
        if (packageString != null && !packageString.isEmpty()) {
            if (packageString.equals(myPackage)) {
                className = cutPackage(myPackage, className);
            } else if (packageString.equals("java.lang")) {
                className = typeElement.getSimpleName().toString();
            }
        }
        return className;
    }

    private PackageElement getPackageElement(TypeElement subscriberClass) {
        Element candidate = subscriberClass.getEnclosingElement();
        while (!(candidate instanceof PackageElement)) {
            candidate = candidate.getEnclosingElement();
        }
        return (PackageElement) candidate;
    }

    private String cutPackage(String paket, String className) {
        if (className.startsWith(paket + '.')) {
            // Don't use TypeElement.getSimpleName, it doesn't work for us with inner classes
            return className.substring(paket.length() + 1);
        } else {
            // Paranoia
            throw new IllegalStateException("Mismatching " + paket + " vs. " + className);
        }
    }
}
