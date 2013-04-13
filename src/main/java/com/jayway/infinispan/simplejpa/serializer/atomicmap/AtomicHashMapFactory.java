package com.jayway.infinispan.simplejpa.serializer.atomicmap;

import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.atomic.AtomicMapLookup;

public class AtomicHashMapFactory implements AtomicMapFactory {
    public AtomicMap create(Cache<Object, ?> cache, Object key) {
        return AtomicMapLookup.getAtomicMap(cache,key,true);
    }
}
