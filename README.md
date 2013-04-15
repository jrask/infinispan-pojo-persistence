#Infinispan object to graph mapper

Simplfies storage of POJOs in infinispan using JPA annotations.

## Goal

More lightweight than hibernate-ogm

Should be as simple as possible

No external configuration (yet...)

Minimal performance overhead


## Supported annotations

@Entity

@Embedded

@Embeddable

@Transient

@Id

## Usage

Person person = new Person("someid",new Address("Home"));
InfinispanEntityManager.persist(person);

Person person = InfinispanEntityManager.find("someId",Person.class);

## Serialization

Java serialization

DeltaAware support with AtomicHashMap & FineGrainedAtomicHashMap


## TODO

Support for Embedded Iterable
Support for secondary id´s
