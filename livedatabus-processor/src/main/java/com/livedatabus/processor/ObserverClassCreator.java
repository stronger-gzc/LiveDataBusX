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
public class ObserverClassCreator {
    private String mObserverClassName;
    private String mPackageName;
    private String mClassName;//所属类名
    private TypeElement mTypeElement;
    private List<Element> mVariableElements = new ArrayList<>();
    private ProcessingEnvironment processingEnv;

    public ObserverClassCreator(Elements elementUtils, TypeElement classElement, ProcessingEnvironment processingEnv) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        mClassName = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mObserverClassName = mClassName + "LiveDataObserver";
        this.processingEnv = processingEnv;

    }

    public void putElement(Element element) {
        mVariableElements.add(element);
    }

    /**
     * 创建Java代码
     *
     * @return
     */
    public String generateClassCode() {
        StringBuilder classCode = new StringBuilder();
        classCode
                .append("package "+mPackageName+";")
                .append("import androidx.lifecycle.LifecycleOwner;\n\n")
                .append("import androidx.lifecycle.Observer;\n\n")
                .append("import com.gzc.livedatabus.LiveDataObserver;\n\n")
                .append("import com.gzc.livedatabus.Bus;\n\n")
                .append("import java.lang.reflect.Method;\n\n")
                .append("public class " + mObserverClassName + " implements LiveDataObserver{\n")
                .append(generateMethodsCode())
                .append("\n}");
        return classCode.toString();
    }

    /**
     * 加入Method
     */
    private String generateMethodsCode() {
        StringBuilder methodsClass = new StringBuilder();
        methodsClass
                .append("@Override\n")
                .append("public void observe(final LifecycleOwner owner , String dynamicKey){\n");

        for (Element element : mVariableElements) {
            Observe annotation = element.getAnnotation(Observe.class);
            //取出Observe中注解的值
            ThreadMode threadMode = annotation.threadMode();
            String key = annotation.key();
            boolean append = annotation.append();
            boolean sticky = annotation.sticky();

            ExecutableElement method = (ExecutableElement) element;
            List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() != 1) {
                throw new RuntimeException("被@Observe注解的方法参数应该为1个");
            }
            //获取事件的类名
            VariableElement param = parameters.get(0);
            TypeMirror paramType = param.asType();
            TypeElement paramElement = (TypeElement) processingEnv.getTypeUtils().asElement(paramType);
            String eventClass = getClassString(paramElement, mPackageName);
            methodsClass.append("Bus.getInstance()\n");

            if (!Utils.isEmpty(key)) {
                if (append) {
                    methodsClass.append(".with(\"" + key + "::\"+dynamicKey," + eventClass + ".class," + sticky + ")\n");
                } else {
                    methodsClass.append(".with(\"" + key + "\"," + eventClass + ".class," + sticky + ")\n");
                }
            } else {
                throw new RuntimeException("key不能为空");

            }


            methodsClass
                    .append(".observe(owner, new Observer<" + eventClass + ">() {\n")
                    .append("@Override\n")
                    .append("public void onChanged(" + eventClass + " bean) {\n")
                    .append(generateReflectCode(method.getSimpleName().toString(), eventClass))
                    .append("}\n")
                    .append("});\n");
        }

        methodsClass.append("\n}");
        return methodsClass.toString();
    }

    private String generateReflectCode(String methodName, String paramClassName) {
        StringBuilder reflectCode = new StringBuilder();
        reflectCode.append("try{\n")
                .append("Method method = owner.getClass().getDeclaredMethod(\"" + methodName + "\"," + paramClassName + ".class);\n")
                .append("method.setAccessible(true);\n")
                .append("method.invoke(owner,bean);\n")
                .append("}catch(Exception e){\n")
                .append("e.printStackTrace();\n")
                .append("}\n");

        return reflectCode.toString();
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

    public String getPackageName() {
        return mPackageName;
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mObserverClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
