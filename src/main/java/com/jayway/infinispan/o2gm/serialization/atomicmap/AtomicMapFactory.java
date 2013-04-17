package com.jayway.infinispan.o2gm.serialization.atomicmap;

import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;

public interface AtomicMapFactory {

    AtomicMap create(Cache<Object,?> cache,Object key);
}
