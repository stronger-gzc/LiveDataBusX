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
@SupportedAnnotationTypes({Constants.ANNOTATION})
@SupportedOptions(value = {Constants.OBSERVERS})
public class ObserveProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Filer mFilerUtils;
    private Map<String, LiveDataObserverCreator> map = new HashMap<>();
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
        map.clear();
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Observe.class);
        for (Element element : elements) {
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            //获得全类名
            String fullClassName = classElement.getQualifiedName().toString();
            LiveDataObserverCreator liveDataObserverCreator = map.get(fullClassName);
            if (liveDataObserverCreator == null) {
                liveDataObserverCreator = new LiveDataObserverCreator(mElementUtils, classElement,processingEnv);
                map.put(fullClassName, liveDataObserverCreator);
            }
            liveDataObserverCreator.putElement(element);
        }
        //通过在项目的gradle中设置的observers值拿到包名
        String observers = processingEnv.getOptions().get(Constants.OBSERVERS);
        int period = observers.lastIndexOf('.');
        String myPackage = period > 0 ? observers.substring(0, period) : null;
        ObserversCreator creator = new ObserversCreator(myPackage,elements);

        try {
            JavaFileObject sourceFile = mFilerUtils.createSourceFile(observers);
            Writer writer = sourceFile.openWriter();
            writer.write(creator.generateClassCode());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //通过遍历mProxyMap，创建java文件
        for (String key : map.keySet()) {
            LiveDataObserverCreator liveDataObserverCreator = map.get(key);
            try {
                JavaFileObject jfo = mFilerUtils.createSourceFile(liveDataObserverCreator.getClassFullName());
                Writer writer = jfo.openWriter();
                writer.write(liveDataObserverCreator.generateClassCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
