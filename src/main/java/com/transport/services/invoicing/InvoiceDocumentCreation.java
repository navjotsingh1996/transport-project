package com.transport.services.invoicing;

import com.itextpdf.text.Paragraph;

public class InvoiceDocumentCreation {

    public Paragraph title() {
        return new Paragraph("Invoice # " + Long.toString(invoice.getId());
    }
}