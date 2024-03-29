package com.jayway.infinispan.o2gm;

/**
 * <p>Responsible for serializing an entity into a serialized form</p>
 *
 */
public interface Serializer {


    Serialized serialize(Object entity);


    <T> Object deserialize(Object key, Class<T> entityType);
}
