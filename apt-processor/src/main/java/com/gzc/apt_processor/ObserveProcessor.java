package com.gzc.apt_processor;

import com.google.auto.service.AutoService;
import com.gzc.apt_annotation.Observe;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
/**
 * author：gzc
 * date：2021/1/26
 * describe：
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.gzc.apt_annotation.Observe"})
public class ObserveProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private Filer mFilerUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();
    private Log log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = Log.newLog(processingEnv.getMessager());
        mElementUtils = processingEnv.getElementUtils();
        mFilerUtils = processingEnv.getFiler();
        log.i("==========>init方法");
    }



    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mProxyMap.clear();
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Observe.class);
        for (Element element : elements) {
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            //获得全类名
            String fullClassName = classElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElementUtils, classElement,processingEnv,log);
                mProxyMap.put(fullClassName, proxy);
            }
            proxy.putElement(element);
        }
        //通过遍历mProxyMap，创建java文件
        for (String key : mProxyMap.keySet()) {
            log.i("key==========>"+key);
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            try {
                JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(),proxyInfo.generateClassCode()).build();
                javaFile.writeTo(mFilerUtils);
            } catch (IOException e) {
                log.e("发生错误===>"+e.getMessage());
            }
        }
        return true;
    }
}
