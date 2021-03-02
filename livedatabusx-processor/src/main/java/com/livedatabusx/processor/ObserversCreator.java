package com.livedatabusx.processor;

import com.livedatabusx.annotation.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * author：gzc
 * date：2021/1/26
 * describe：所有的LiveDataObserver类都会放在这个类里  key：全类名   value：LiveDataObserver对象
 * */
@Deprecated
public class ObserversCreator {
    private final String observerClassName;
    private String packageName;
    private Set<? extends Element> elements;


    public ObserversCreator(String packageName,String observerClassName,Set<? extends Element>elements) {
        this.packageName = packageName;
        this.elements = elements;
        this.observerClassName = observerClassName;
    }


    /**
     * 创建Java代码
     *
     * @return
     */
    public String generateClassCode() {
        StringBuilder classCode = new StringBuilder();
        classCode
                .append("package "+packageName+";\n\n")
                .append("import com.gzc.livedatabusx.Observers;\n\n")
                .append("import com.gzc.livedatabusx.LiveDataObserver;\n\n")
                .append("import java.util.HashMap;\n\n")
                .append("import java.util.Map;\n\n")
                .append("public class " + observerClassName + " implements Observers{\n")
                .append("private static final Map<String,LiveDataObserver>LIVEDATA_MAP;\n\n")
                .append("static{\n")
                .append("LIVEDATA_MAP = new HashMap<String,LiveDataObserver>();\n\n")
                .append(generateAddClass())
                .append("}\n")
                .append("@Override\n")
                .append("public Map<String, LiveDataObserver> getObservers() {\n")
                .append("return LIVEDATA_MAP;\n")
                .append("}\n")
                .append("\n}");
        return classCode.toString();
    }

    /**
     * 生成向map添加的代码
     * @return
     */
    private String generateAddClass(){
        Map<String,String>tempMap = new HashMap<>();
        StringBuilder addCode = new StringBuilder();
        for (Element element : elements) {
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            String className = classElement.getSimpleName().toString();
            //key就是全类名
            String key = packageName+"."+className;
            if(!tempMap.containsKey(key)){
                tempMap.put(key,className+ Constants.SUFFIX);
            }
            key = null;

        }
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            addCode.append("LIVEDATA_MAP.put(\""+entry.getKey()+"\",new "+entry.getValue()+"());\n\n");
        }
        return addCode.toString();
    }
}
