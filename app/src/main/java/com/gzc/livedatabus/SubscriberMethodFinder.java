package com.gzc.livedatabus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Administrator
 * Date: 2021-01-24 18:15
 * Describe:
 */
public class SubscriberMethodFinder {
    private static final Map<Class<?>,List<SubscriberMethod>>METHOD_CACHE = new ConcurrentHashMap<>();

    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass,String key) {
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        if(subscriberMethods!=null){
            return subscriberMethods;
        }

        if(subscriberMethods.isEmpty()){
            throw new LiveDataBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @Subscribe annotation");
        }else{
            METHOD_CACHE.put(subscriberClass,subscriberMethods);
            return subscriberMethods;
        }
    }
}
