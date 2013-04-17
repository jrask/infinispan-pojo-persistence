package com.jayway.infinispan.o2gm;

/**
 * <p>Represents an object in serialized form</p>
 *
 */
public interface Serialized {

    /**
     * Cache that should be used
     * @return
     */
    String cache();

    /**
     * Key value
     * @return
     */
    Object key();

    /**
     * Cache value
     * @return
     */
    Object value();

    /**
     * If the entity has already been attached to cache or not
     * if true, the entity manager will not store the object in cache
     * @return
     */
    boolean isAttached();
}
