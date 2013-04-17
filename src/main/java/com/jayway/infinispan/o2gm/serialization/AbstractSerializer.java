package com.jayway.infinispan.o2gm.serialization;

import com.jayway.infinispan.o2gm.Serializer;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;

import javax.persistence.Id;
import java.lang.reflect.Field;

public abstract class AbstractSerializer implements Serializer {


    private final CacheContainer cacheContainer;

    protected AbstractSerializer(CacheContainer cacheContainer) {
        this.cacheContainer = cacheContainer;
    }

    protected Cache<Object, ?> cache(final Object entity) {
        return cacheContainer.getCache(cacheName(entity.getClass()));
    }
    protected Cache<Object, ?> cache(final Class entity) {
        return cacheContainer.getCache(cacheName(entity));
    }

    protected String cacheName(Class object) {
        return object.getSimpleName().toLowerCase();
    }

    protected Object id(final Object entity) {
        try {
            return filter(entity.getClass().getDeclaredFields(), new EnableFieldAccessCommand<Object>() {
                @Override
                public Object apply(Field field) throws IllegalAccessException {
                    super.apply(field);
                    if (field.getAnnotation(Id.class) != null) {
                        Object o = field.get(entity);
                        return o;
                    }
                    return null;
                }
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T filter(Field[] fields, Command command) throws IllegalAccessException {
        for (Field field : fields) {
            T result = (T) command.apply(field);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


    public static interface Command<T> {
        T apply(Field field) throws IllegalAccessException;
    }

    public static class EnableFieldAccessCommand<T> implements Command<T> {

        public T apply(Field field) throws IllegalAccessException {
            field.setAccessible(true);
            return null;
        }
    }
    protected void setField(Field f, Object value, Object target) {
        try {
            f.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
