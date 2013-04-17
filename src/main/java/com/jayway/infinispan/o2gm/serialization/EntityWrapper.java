package com.jayway.infinispan.o2gm.serialization;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityWrapper<T> {


    private final Objenesis objenesis = new ObjenesisStd();
    private final Object targetInstance;
    private final Map<Class, ObjectInstantiator> clazzFactoryMap = new ConcurrentHashMap();

    public EntityWrapper(Object targetInstance) {
        this.targetInstance = targetInstance;
    }

    public EntityWrapper(Class<T> targetClass) {
        this.targetInstance = createInstance(targetClass);
    }


    private <T> T createInstance(Class<T> clazz) {
        if(!clazzFactoryMap.containsKey(clazz)) {
            clazzFactoryMap.put(clazz,objenesis.getInstantiatorOf(clazz));
        }
        return (T)clazzFactoryMap.get(clazz).newInstance();
    }

    public String cache() {
        return targetInstance.getClass().getSimpleName().toLowerCase();
    }




}
