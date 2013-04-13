package com.jayway.infinispan;

import com.jayway.infinispan.simplejpa.serializer.atomicmap.AtomicHashMapFactory;
import com.jayway.infinispan.simplejpa.serializer.atomicmap.AtomicMapSerializer;
import com.jayway.infinispan.simplejpa.serializer.java.JavaSerializer;
import com.jayway.infinispan.simplejpa.InfinispanEntityManager;
import com.jayway.infinispan.simplejpa.Serializer;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class InfinispanEntityManagerTest {

    private InfinispanEntityManager cache;
    DefaultCacheManager cacheManager = new DefaultCacheManager();

    Person person = new Person("Johan",new Address("streetname"));

    @Before
    public void prepare() {

        cacheManager.defineConfiguration("person", new ConfigurationBuilder().transaction()
                .transactionMode(TransactionMode.TRANSACTIONAL)
                .invocationBatching().enable()
                .build());

    }
      @After
      public void shutdownCache() {
          cacheManager.stop();
      }


    @Test
    public void save_person_with_java_serializer() {
        Serializer serializer = new JavaSerializer(cacheManager);
        cache = new InfinispanEntityManager(cacheManager,serializer);
        doSaveAndVerify();
    }


    @Test
    public void save_person_with_atomicmap_serializer() {
        Serializer serializer = new AtomicMapSerializer(cacheManager,new AtomicHashMapFactory(),Person.class,Address.class);
        cache = new InfinispanEntityManager(cacheManager,serializer);
        doSaveAndVerify();
    }

    private void doSaveAndVerify() {
        cache.persist(person);
        Person personFromCache = cache.find(person.getId(), Person.class);
        assertThat(personFromCache).isNotNull();
        assertThat(personFromCache).isEqualTo(person);
        assertThat(personFromCache.getId()).isEqualTo(person.getId());
        assertThat(personFromCache.getAddress().getStreet()).isEqualTo(person.getAddress().getStreet());
        assertThat(personFromCache.getSecret()).isNull();
    }

}
