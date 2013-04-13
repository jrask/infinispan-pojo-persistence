package com.jayway.infinispan;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Person {

    @Id
    private String id;

    private String name;

    @Transient
    private String secret;

    @Embedded
    private Address address;

    public Person(String name, Address address) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.secret = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecret() {
        return secret;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", secret='" + secret + '\'' +
                ", address=" + address +
                '}';
    }
}
