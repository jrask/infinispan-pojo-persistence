#Introduction

Simplfies storage of POJOs in infinispan using JPA annotations.

## Goal

Adding pojos to infinispan should be simple

No external configuration

Minimal performance overhead


## Supported annotations

@Entity

@Embedded

@Embeddable

@Transient

@Id

## Usage

Person person = new Person("someid);
InfinispanEntityManager.persist(person);

Person person = InfinispanEntityManager.find("someId",Person.class);

## Serialization

Java serialization

AtomicHashMap & FineGrainedAtomicHashMap


## TODO

Support for Embedded Iterable
