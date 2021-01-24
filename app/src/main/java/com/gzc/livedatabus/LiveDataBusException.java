package com.gzc.livedatabus;

/**
 * User: Administrator
 * Date: 2021-01-24 18:18
 * Describe:
 */
public class LiveDataBusException extends RuntimeException{
    public LiveDataBusException(String detailMessage){
        super(detailMessage);
    }

    public LiveDataBusException(Throwable throwable){
        super(throwable);
    }

    public LiveDataBusException(String detailMessage,Throwable throwable){
        super(detailMessage,throwable);
    }
}
