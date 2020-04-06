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
    private double rateAmount;

    private boolean truckorderNotUsed;

    private double detention;

    private double layover;

    private double advance;

    private double extraStop;

    private double other;

    public double getTotalBalance() {
        return rateAmount + detention + layover + advance + extraStop + other;
    }
}
