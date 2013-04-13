package com.jayway.infinispan.simplejpa.serializer.java;

import com.jayway.infinispan.simplejpa.Serialized;
import com.jayway.infinispan.simplejpa.serializer.AbstractSerializer;
import com.rits.cloning.Cloner;
import org.infinispan.manager.CacheContainer;

import javax.persistence.Embedded;
import javax.persistence.Transient;
import java.lang.reflect.Field;

/**
 * <p>Serializer using java Serialization</p>
 */
public class JavaSerializer extends AbstractSerializer {

    private final Cloner cloner = new Cloner();

    public JavaSerializer(CacheContainer cacheContainer) {
        super(cacheContainer);
    }

    public Serialized serialize(Object entity) {

        final Object entityCopy = cloner.deepClone(entity);

        clearTransientValues(entityCopy);

        return new Serialized() {
            public String cache() {
                return cacheName(entityCopy.getClass());
            }

            public Object key() {
                return id(entityCopy);
            }

            public Object value() {
                return entityCopy;
            }

            public boolean isAttached() {
                return false;
            }
        };
    }

    void clearTransientValues(final Object entity) {
        System.out.println("clear");
        try {
            filter(entity.getClass().getDeclaredFields(),new EnableFieldAccessCommand<Void>() {
                @Override
                public Void apply(Field field) throws IllegalAccessException {
                    super.apply(field);
                    System.out.println(field);
                    if(field.getAnnotation(Transient.class) != null) {
                        System.out.println("nulling field " + field);
                        setField(field,null,entity);
                    } else if (field.getAnnotation(Embedded.class) != null) {
                        clearTransientValues(field.get(entity));
                    }
                    return null;
                }
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(Object key, Class<T> entityType) {
        return (T)cache(entityType).get(key);
    }
}
