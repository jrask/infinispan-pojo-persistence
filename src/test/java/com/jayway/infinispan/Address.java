package com.jayway.infinispan;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Address {

    private String street;

    @Transient
    private String streetSecret = "secret";

    public Address(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                '}';
    }
}
