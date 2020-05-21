package com.transport.services.invoicing.models;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Stop {
    @NonNull
    private Long date;
    @NonNull
    private String name;
    @NonNull
    private String city;
    @NonNull
    private String state;
    private String streetAddress;
    private int zip;
    @NonNull
    private StopType type;

    public enum StopType {
        PICKUP,
        DELIVERY
    }
}
