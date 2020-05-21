package com.transport.services.invoicing.models;

import lombok.NonNull;
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
        return Math.round((rateAmount + detention + layover + advance + extraStop + other + lumper) * 100.0) / 100.0;
    }
}
