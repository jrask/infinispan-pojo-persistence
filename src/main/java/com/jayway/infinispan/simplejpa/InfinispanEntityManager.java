package com.jayway.infinispan.simplejpa;

import com.jayway.infinispan.simplejpa.serializer.atomicmap.AtomicHashMapFactory;
import com.jayway.infinispan.simplejpa.serializer.atomicmap.AtomicMapSerializer;
import org.infinispan.manager.CacheContainer;


/**
 *
 */
public class InfinispanEntityManager {

    private final CacheContainer cacheManager;
    private final Serializer serializer;

    public InfinispanEntityManager(CacheContainer cacheManager,Class<?>... entityClasses) {
        this.cacheManager = cacheManager;
        this.serializer = new AtomicMapSerializer(cacheManager, new AtomicHashMapFactory(),entityClasses);
    }

    public InfinispanEntityManager(CacheContainer cacheManager,Serializer serializer) {
        this.cacheManager = cacheManager;
        this.serializer = serializer;
    }

    public void persist(Object object) {
        Serialized serialized = serializer.serialize(object);
        if(!serialized.isAttached()) {
            cacheManager.getCache(serialized.cache()).put(serialized.key(),serialized.value());
        }
    }

    public <T> T find(Object key, Class<T> clazz) {
        return (T) serializer.deserialize(key, clazz);
    }
}
