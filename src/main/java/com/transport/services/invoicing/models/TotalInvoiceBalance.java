package com.transport.services.invoicing.models;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;

@Value
@Embeddable
public class TotalInvoiceBalance {
    @NonNull
    private Double rateAmount;

    private boolean truckOrderNotUsed;

    private double detention;

    private double layover;

    private double advance;

    private double extraStop;

    private double lumper;

    private double other;

    public double getTotalBalance() {
        return rateAmount + detention + layover + advance + extraStop + other + lumper;
    }
}
