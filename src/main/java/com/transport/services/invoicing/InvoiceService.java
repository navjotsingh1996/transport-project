package com.transport.services.invoicing;

import com.transport.services.invoicing.models.CompanyInfo;
import com.transport.services.invoicing.models.InvoiceDto;
import com.transport.services.invoicing.models.Stop;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    /**
     * This function creates an invoice and adds it to the database
     *
     * @param invoice to be added to the database
     * @throws IOException if creating the pdf fails
     * @return path to pdf
     */
    String createInvoice(InvoiceDto invoice) throws IOException;

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
     * updating an existing invoice
     * @param invoice invoice to be edited
     * @throws IOException if creating the pdf fails
     * @return path to pdf
     */
    String editInvoice(InvoiceDto invoice) throws IOException;

    /**
     * Searches Bill To field for all Invoices to find a match
     * @param name query for the name
     * @param address query for the address
     * @return list of BillTo (CompanyInfo fields) that match the query
     */
    List<CompanyInfo> searchBillTo(String name, String address);

    /**
     * Searches Stops field for all Invoices to find a match
     * @param name query for the name
     * @param address query for the address
     * @return list of Stops that match the query
     */
    List<Stop> searchStops(String name, String address);
}
