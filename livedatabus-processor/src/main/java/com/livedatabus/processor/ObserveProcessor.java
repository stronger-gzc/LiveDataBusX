package com.livedatabus.processor;

import com.google.auto.service.AutoService;
import com.livedatabus.annotion.Observe;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * author：gzc
 * date：2021/1/26
 * describe：
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.livedatabus.annotion.Observe"})
@SupportedOptions(value = {"observers"})
public class ObserveProcessor extends AbstractProcessor {
    public static final String OBSERVERS = "observers";

    private Elements mElementUtils;
    private Filer mFilerUtils;
    private Map<String, ObserverClassCreator> mProxyMap = new HashMap<>();
    private Log log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = Log.newLog(processingEnv.getMessager());
        mElementUtils = processingEnv.getElementUtils();
        mFilerUtils = processingEnv.getFiler();
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
            ObserverClassCreator proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ObserverClassCreator(mElementUtils, classElement,processingEnv);
                mProxyMap.put(fullClassName, proxy);
            }
            proxy.putElement(element);
        }

        String observers = processingEnv.getOptions().get(OBSERVERS);
        int period = observers.lastIndexOf('.');
        String myPackage = period > 0 ? observers.substring(0, period) : null;
        ObserverMapClassCreator creator = new ObserverMapClassCreator(myPackage);

        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(observers);
            Writer writer = sourceFile.openWriter();
            writer.write(creator.generateClassCode());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //通过遍历mProxyMap，创建java文件
        for (String key : mProxyMap.keySet()) {
            ObserverClassCreator proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateClassCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
