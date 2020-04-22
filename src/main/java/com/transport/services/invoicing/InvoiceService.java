package com.transport.services.invoicing;

import com.transport.services.invoicing.models.InvoiceDto;

import java.util.List;

public interface InvoiceService {

    /**
     * This function creates an invoice and adds it to the database
     *
     * @param invoice to be added to the database
     * @return name of the invoice
     */
    String createInvoice(InvoiceDto invoice);

    /**
     * Get an invoice by invoice id
     *
     * @param id used to get invoice
     * @return invoice dto
     */
    InvoiceDto getInvoice(long id);

    /**
     * Gets all Invoices
     * @return all Invoices
     */
    List<InvoiceDto> getAllInvoices();

    /**
     * Delete all invoices with the given id
     * @param ids ids to be deleted
     */
    void deleteInvoices(List<Long> ids);

    /**
     * Editing a list of invoices
     * @param invoices invoices to be edited
     * @return invoice ids that were edited
     */
    List<Long> editInvoices(List<InvoiceDto> invoices);
}
