#Introduction

Simplfies storage of POJOs in infinispan using JPA annotations.

@Entity
@Embedded
@Embeddable
@Transient
@Id

Person person = new Person("someid);
InfinispanEntityManager.persist(person);

Person person = InfinispanEntityManager.find("someId",Person.class);


