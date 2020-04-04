package com.transport.services.invoicing.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.transport.commons.ConverterListStop;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    private long loadNumber;

    @NonNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

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
