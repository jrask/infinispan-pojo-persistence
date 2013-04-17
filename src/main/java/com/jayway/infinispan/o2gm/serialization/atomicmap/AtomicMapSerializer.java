package com.jayway.infinispan.o2gm.serialization.atomicmap;

import com.jayway.infinispan.o2gm.Serialized;
import com.jayway.infinispan.o2gm.serialization.AbstractSerializer;
import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.atomic.AtomicMapLookup;
import org.infinispan.manager.CacheContainer;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AtomicMapSerializer extends AbstractSerializer {

    private final Objenesis objenesis = new ObjenesisStd();

    private final CacheContainer cacheContainer;
    private final AtomicMapFactory atomicMapFactory;
    private final Map<Class, ObjectInstantiator> clazzFactoryMap = new HashMap<Class, ObjectInstantiator>();

    public AtomicMapSerializer(CacheContainer cacheContainer,
                               AtomicMapFactory atomicMapFactory,
                               Class<?>... clazzzes) {
        super(cacheContainer);
        this.cacheContainer = cacheContainer;
        this.atomicMapFactory = atomicMapFactory;
        for (Class c : clazzzes) {
            clazzFactoryMap.put(c, objenesis.getInstantiatorOf(c));
        }
    }

    public <T> T deserialize(Object key, Class<T> clazz) {
        Cache<Object, ?> cache = cacheContainer.getCache(clazz.getSimpleName().toLowerCase());
        Map<Object, Object> map = atomicMapFactory.create(cache, key);

        T instance = (T)createInstance(clazz);

        for (Field field : instance.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            deserializeField(instance, field, map);
        }

        return instance;
    }

    private <T> T createInstance(Class<T> clazz) {
         return (T)clazzFactoryMap.get(clazz).newInstance();
    }

    private <T> void deserializeField(T instance, Field field, Map<Object, Object> map) {
        Object value = map.get(field.getName());

        if (field.getAnnotation(Embedded.class) != null) {
            Object embedded = createInstance(field.getType());
            for (Field f : embedded.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                deserializeField(embedded, f, map);
            }
            setField(field, embedded, instance);
            return;
        }
        if (value != null) {
            setField(field, value, instance);
        }
    }

    public Serialized serialize(Object object) {
        String cacheName = object.getClass().getSimpleName().toLowerCase();
        Cache<Object, ?> cache = cacheContainer.getCache(cacheName);

        try {
            Map map = getCacheMap(object, cache);
            return serialize(map, object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }




    private Serialized serialize(final Map<Object, Object> map, final Object object) throws IllegalAccessException {

        Entity annotation = object.getClass().getAnnotation(Entity.class);
        Embedded embedded = object.getClass().getAnnotation(Embedded.class);
        if (annotation == null && map == null) {
            throw new IllegalStateException("No entity annotation");
        }

        filter(object.getClass().getDeclaredFields(), new EnableFieldAccessCommand<Void>() {
            @Override
            public Void apply(Field field) throws IllegalAccessException {
                super.apply(field);
                serializeField(object, field, map);
                return null;
            }
        });

        return new Serialized() {
            public String cache() {
                return cacheName(object.getClass());
            }

            public Object key() {
                return id(object);
            }

            public Object value() {
                return map;
            }

            public boolean isAttached() {
                return true;
            }
        };
    }

    private void serializeField(Object object, Field field, Map<Object, Object> map) throws IllegalAccessException {
        if (field.getAnnotation(Transient.class) != null) {
            return;
        }

        if (field.getAnnotation(Embedded.class) != null) {
            serialize(map, field.get(object));
            return;
        }

        String mapKey = field.getName();
        Object value = field.get(object);
        // Only put value if it has been changed
        if (map.containsKey(mapKey)) {
            Object existingValue = map.get(mapKey);
            if (!existingValue.equals(value)) {
                map.put(mapKey, value);
            }
        } else {
            map.put(mapKey, value);
        }
    }

    private Map<Object, Object> getCacheMap(final Object object, final Cache<Object, ?> cache) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        System.out.println(fields.length);
        Map<Object, Object> map = filter(fields, new EnableFieldAccessCommand<AtomicMap<Object, Object>>() {
            @Override
            public AtomicMap<Object, Object> apply(Field field) throws IllegalAccessException {
                super.apply(field);
                if (field.getAnnotation(Id.class) != null) {
                    Object o = field.get(object);
                    return AtomicMapLookup.getAtomicMap(cache, o);
                }
                return null;
            }
        });
        if (map != null) {
            return map;
        }
        throw new IllegalStateException("No field with @Id is declared");
    }

}
