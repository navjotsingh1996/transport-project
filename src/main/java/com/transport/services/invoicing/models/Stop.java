package com.transport.services.invoicing.models;

import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class Stop {
    @NonNull
    private LocalDate date;
    @NonNull
    private String name;
    @NonNull
    private String city;
    @NonNull
    private String state;
    private int zip;
    @NonNull
    private StopType type;

    public enum StopType {
        PICKUP,
        DELIVERY
    }
}
