package com.transport.services.invoicing.models;

import lombok.NonNull;
import lombok.Value;

import javax.persistence.Embeddable;

@Value
@Embeddable
public class CompanyInfo {

    @NonNull
    private String name;
    @NonNull
    private String streetAddress;
    @NonNull
    private String city;
    @NonNull
    private String state;
    private int zip;
}
