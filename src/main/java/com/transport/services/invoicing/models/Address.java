package com.transport.services.invoicing.models;

import lombok.NonNull;
import lombok.Value;

@Value
public class Address {
    private String name;
    private String streetAddress;
    @NonNull
    private String city;
    @NonNull
    private String state;
    private int zip;
}
