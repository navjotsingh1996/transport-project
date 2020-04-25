package com.transport.services.invoicing.models;

import com.transport.commons.ConverterListStop;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "invoice")
@SequenceGenerator(name = "SequenceIdGenerator",
        sequenceName = "SEQ_ID_GEN", initialValue = 30000)
public class Invoice {

    @Id
    @GeneratedValue(generator = "SequenceIdGenerator")
    private long id;

    @NonNull
    private String loadNumber;

    @NonNull
    private Long date;

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

}
