package com.transport.services.invoicing.models;

import com.transport.commons.ConverterListStop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "invoice")
@SequenceGenerator(name = "SequenceIdGenerator",
        sequenceName = "SEQ_ID_GEN", initialValue = 30000)
@RequiredArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(generator = "SequenceIdGenerator")
    private long id;

    @NonNull
    private String loadNumber;

    private long date;

    @NonNull
    @Embedded
    private CompanyInfo billTo;

    @NonNull
    @Convert(converter = ConverterListStop.class)
    @Column(name = "stops", length = 1024)
    private List<Stop> stops;

    @NonNull
    @Embedded
    private TotalInvoiceBalance balances;

    public Invoice(String loadNumber, long date, CompanyInfo billTo, List<Stop> stops, TotalInvoiceBalance balances) {
        this.date = date;
        this.loadNumber = loadNumber;
        this.date = date;
        this.billTo = billTo;
        this.stops = stops;
        this.balances = balances;
    }

}
