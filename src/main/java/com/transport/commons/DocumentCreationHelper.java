package com.transport.commons;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.transport.services.invoicing.models.Stop;

import java.io.IOException;
import java.util.List;

import static com.itextpdf.io.font.FontConstants.TIMES_ROMAN;

public class DocumentCreationHelper {
    private static final String COMPANY_NAME = "MOONLIGHT TRANS INC.";

    private static Text createBoldUnderlineChunk(String text) throws IOException {
        Text textBoldUnderline = new Text(text);
        textBoldUnderline.setBold();
        textBoldUnderline.setUnderline(1.5f, -1);
        textBoldUnderline.setFont(PdfFontFactory.createFont(TIMES_ROMAN));
        textBoldUnderline.setFontSize(15);
        return textBoldUnderline;
    }

    /**
     * This function will return the title of the invoice. Which would include
     * both the company name and invoice details (invoice # and date)
     *
     * @return Paragraph of the title
     */
    public static Paragraph title() throws IOException {
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.add(createBoldUnderlineChunk(COMPANY_NAME));
        return titleParagraph;
    }

    /**
     * This function will create the bill to for the invoice. This will also include
     * the load #
     *
     * @return paragraph of the bill to
     */
    public static Paragraph billTo() {
        return new Paragraph("");
    }


    /**
     * This function will create the stops section of the invoice
     *
     * @param stops ?
     * @return paragraph for list of stops
     */
    public static Paragraph stops(List<Stop> stops) {
        return new Paragraph("");
    }

    /**
     * This function will create the total costs of the invoice, including
     * total rate, detention, layover, etc.
     *
     * @return total cost section of the invoice
     */
    public static Paragraph totalCosts() {
        return new Paragraph("");
    }
}
