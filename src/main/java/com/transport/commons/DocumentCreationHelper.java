package com.transport.commons;

import com.itextpdf.text.Paragraph;
import com.transport.services.invoicing.models.Stop;

import java.util.List;

public class DocumentCreationHelper {

    /**
     * This function will return the title of the invoice. Which would include
     * both the company name and invoice details (invoice # and date)
     * @return Paragraph of the title
     */
    public static Paragraph title() {
        return new Paragraph("");
    }

    /**
     * This function will create the bill to for the invoice. This will also include
     * the load #
     * @return paragraph of the bill to
     */
    public static Paragraph billTo() {
        return new Paragraph("");
    }


    /**
     * This function will create the stops section of the invoice
     * @param stops ?
     * @return paragraph for list of stops
     */
    public static Paragraph stops(List<Stop> stops) {
        return new Paragraph("");
    }

    /**
     * This function will create the total costs of the invoice, including
     * total rate, detention, layover, etc.
     * @return total cost section of the invoice
     */
    public static Paragraph totalCosts() {
        return new Paragraph("");
    }
}
