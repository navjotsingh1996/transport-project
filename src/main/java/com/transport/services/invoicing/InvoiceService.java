package com.transport.services.invoicing;

import com.transport.services.invoicing.models.InvoiceDto;

public interface InvoiceService {

    /**
     * This function creates an invoice and adds it to the database
     * @param invoice to be added to the database
     * @return the invoice number
     */
    long createInvoice(InvoiceDto invoice);

    /**
     * Get an invoice by invoice id
     * @param id used to get invoice
     * @return invoice dto
     */
    InvoiceDto getInvoice(long id);
}
