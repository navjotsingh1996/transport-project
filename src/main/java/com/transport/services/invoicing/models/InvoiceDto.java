package com.transport.services.invoicing.models;

import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class InvoiceDto {

    private final long id;

    @NonNull
    private final String loadNumber;

    private final long date;

    @NonNull
    private final CompanyInfo billTo;

    @NonNull
    private final List<Stop> stops;

    @NonNull
    private final TotalInvoiceBalance balances;

    public enum invoiceSearchTypes {
        BILLTO,
        STOPS
    }
}
