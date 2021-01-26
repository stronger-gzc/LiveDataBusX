package com.gzc.apt_processor;

import androidx.lifecycle.LifecycleOwner;

import com.gzc.apt_annotation.LiveDataObserver;
import com.gzc.apt_annotation.Observe;
import com.gzc.apt_annotation.ThreadMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * author：gzc
 * date：2021/1/26
 * describe：
 */
public class ClassCreatorProxy {
    private String mObserverClassName;
    private String mPackageName;
    private String mClassName;//所属类名
    private TypeElement mTypeElement;
    private List<Element> mVariableElements = new ArrayList<>();
    private Log log;
    private ProcessingEnvironment processingEnv;

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement, ProcessingEnvironment processingEnv,Log log) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        mClassName = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mObserverClassName = mClassName + "LiveDataObserver";
        this.log = log;
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
    public TypeSpec generateClassCode() {
        TypeSpec observeClass = TypeSpec.classBuilder(mObserverClassName)
                .addSuperinterface(ClassName.get(LiveDataObserver.class))
                .addModifiers(PUBLIC)
                .addMethod(generateMethodsCode())
                .build();
        return observeClass;

    }

    /**
     * 加入Method
     */
    private MethodSpec generateMethodsCode() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("observe")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(ClassName.get(LifecycleOwner.class),"owner")
                .addParameter(ClassName.get(String.class), "key");

        for (Element element : mVariableElements) {
            Observe annotation = element.getAnnotation(Observe.class);
            //取出Observe中注解的值
            ThreadMode threadMode = annotation.threadMode();
            String key = annotation.key();
            boolean append = annotation.append();
            boolean sticky = annotation.sticky();

            ExecutableElement method = (ExecutableElement) element;
            List<? extends VariableElement> parameters = method.getParameters();
            if(parameters.size()!=1){
                throw new RuntimeException("被@Observe注解的方法参数应该为1个");
            }
            VariableElement param = parameters.get(0);
            TypeMirror paramType = param.asType();
            TypeElement paramElement = (TypeElement) processingEnv.getTypeUtils().asElement(paramType);
            String eventClass = getClassString(paramElement, mPackageName);

            methodBuilder.addCode("Bus")
                    .addCode(".getInstance()")
                    .addCode(".with(\""+mClassName+"::"+method.getSimpleName()+"\","+eventClass+".class,"+sticky+")")
                    .addCode(".observe(owner, new Observer<"+eventClass+">() {\n")
                    .addCode("@Override\n")
                    .addCode("public void onChanged("+eventClass+" bean) {\n")
                    .addCode("}});\n");
        }

        return methodBuilder.build();
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
