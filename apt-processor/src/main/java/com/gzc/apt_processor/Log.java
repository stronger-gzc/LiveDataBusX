package com.gzc.apt_processor;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author Lance
 * @date 2018/2/22
 */

public class Log {
    private Messager messager;

    private Log(Messager messager) {
        this.messager = messager;
    }

    public static Log newLog(Messager messager) {
        return new Log(messager);
    }

    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void e(String msg){
        messager.printMessage(Diagnostic.Kind.ERROR,msg);
    }

}
