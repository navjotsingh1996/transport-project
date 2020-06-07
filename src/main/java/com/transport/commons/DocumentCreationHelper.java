package com.transport.commons;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;
import com.transport.services.invoicing.models.Stop;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    public static Table createBorderlessTable(ArrayList<Stop> stops, Stop.StopType type) throws IOException {
        int col = stops.size();
        int row = 5;
        boolean isPickup = type == Stop.StopType.PICKUP;
        String stopString = isPickup ? "Pickup #" : "Delivery #";
        String dateString = isPickup ? "Pickup Date: " : "Delivery Date: ";

        if (!stops.isEmpty() && stops.get(0).getStreetAddress() == null || stops.get(0).getStreetAddress().isEmpty()) {
            row -= 1;
        }
        if (!stops.isEmpty() && stops.get(0).getName() == null || stops.get(0).getName().isEmpty()) {
            row -= 1;
        }

        String[][] stopData = new String[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                switch (i) {
                    case 0:
                        stopData[i][j] = stopString + (j + 1);
                        break;
                    case 1:
                        stopData[i][j] = dateString +
                                Instant.ofEpochMilli(
                                        stops.get(j).getDate()).atZone(ZoneId.systemDefault())
                                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        break;
                    case 2:
                        stopData[i][j] = row == 5 || row == 4 ? stops.get(j).getName() : stops.get(j).getCityStateZip();
                        break;
                    case 3:
                        stopData[i][j] = row == 5 ? stops.get(j).getStreetAddress() : stops.get(j).getCityStateZip();
                        break;
                    case 4:
                        stopData[i][j] = stops.get(j).getCityStateZip();
                        break;
                }
            }
        }

        float[] colWidth = new float[col];
        for (int i = 0; i < col; i++) {
            colWidth[i] = 10;
        }
        Table stopTable = new Table(UnitValue.createPointArray(colWidth)).useAllAvailableWidth();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                Cell cell = new Cell().add(stopData[i][j]).setBorder(Border.NO_BORDER)
                        .setFont(PdfFontFactory.createFont(TIMES_ROMAN));
                if (i == 0) {
                    cell.setBold().setUnderline(1.5f, -1)
                            .setFont(PdfFontFactory.createFont(TIMES_ROMAN)).setFontSize(15);
                }
                stopTable.addCell(cell);
            }
        }
        return stopTable;
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
