package com.jayway.infinispan.simplejpa.serializer.atomicmap;

import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;

public interface AtomicMapFactory {

    AtomicMap create(Cache<Object,?> cache,Object key);
}
