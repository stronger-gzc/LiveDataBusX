package com.livedatabusx.processor;

import com.livedatabusx.annotation.Constants;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;
import com.livedatabusx.annotation.Utils;

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
 * describe：LifecycleOwner调用Bus进行observe的类
 */
public class LiveDataObserverCreator {
    private String mObserverClassName;
    private String mClassName;//所属类名
    private TypeElement mTypeElement;
    private List<Element> mVariableElements = new ArrayList<>();
    private ProcessingEnvironment processingEnv;

    public LiveDataObserverCreator(Elements elementUtils, TypeElement classElement, ProcessingEnvironment processingEnv) {
        this.mTypeElement = classElement;
        mClassName = mTypeElement.getSimpleName().toString();
        this.mObserverClassName = mClassName + Constants.SUFFIX;
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
                .append("package " + Constants.LIVEDATABUSX_PACKAGE_NAME + ";\n\n")
                .append("import androidx.lifecycle.LifecycleOwner;\n\n")
                .append("import androidx.lifecycle.Observer;\n\n")
                .append("import com.gzc.livedatabusx.LiveDataObserver;\n\n")
                .append("import com.gzc.livedatabusx.Bus;\n\n")
                .append("import com.gzc.livedatabusx.LiveDataBusX;\n\n")
                .append("import com.gzc.livedatabusx.Observation;\n\n")
                .append("import java.lang.reflect.Method;\n\n")
                .append("import static com.livedatabusx.annotation.ThreadMode.*;\n\n")
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
                .append("public void observe(final LifecycleOwner owner , final String dynamicKey){\n");
        for (Element element : mVariableElements) {
            Observe annotation = element.getAnnotation(Observe.class);
            //取出Observe中注解的值
            ThreadMode threadMode = annotation.threadMode();
            String annotationKey = annotation.key();
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
            String eventClass = getClassString(paramElement, Constants.LIVEDATABUSX_PACKAGE_NAME);

            if (Utils.isEmpty(annotationKey)) {
                throw new RuntimeException("key不能为空");
            }

            String key = null;
            //根据append来决定key值
            if (append) {
                key = "\"" + annotationKey + "::\"+dynamicKey";
            } else {
                key = "\"" + annotationKey + "\"";
            }

            methodsClass.append("Bus.getInstance()\n")
                    .append(".with(" + key + "," + eventClass + ".class," + sticky + ")\n");


            methodsClass
                    .append(".observe(owner, new Observer<" + eventClass + ">() {\n")
                    .append("@Override\n")
                    .append("public void onChanged(" + eventClass + " bean) {\n")
                    .append(generateOnChangeMethodCode(threadMode, method.getSimpleName().toString()))
                    .append("}\n")
                    .append("});\n");
        }

        methodsClass.append("\n}");
        return methodsClass.toString();
    }

    private String generateOnChangeMethodCode(ThreadMode threadMode, String methodName) {

        StringBuilder onChangeMethodCode = new StringBuilder();

        onChangeMethodCode.append("Observation observation = new Observation(" + threadMode + ",owner,bean,\"" + methodName + "\");\n\n");

        onChangeMethodCode.append("LiveDataBusX.getInstance()\n")
                .append(".postToThread(observation);\n");

        return onChangeMethodCode.toString();
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



    public String getClassFullName() {
        return Constants.LIVEDATABUSX_PACKAGE_NAME + "." + mObserverClassName;
    }

    public String getClassName() {
        return mObserverClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
