package com.transport.commons;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.transport.services.invoicing.models.Stop;
import com.transport.services.invoicing.models.CompanyInfo;
import com.transport.services.invoicing.models.TotalInvoiceBalance;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.itextpdf.io.font.FontConstants.TIMES_ROMAN;

public class DocumentCreationHelper {
    /**
     *
     */
    private static final String COMPANY_NAME = "MOONLIGHT TRANS, INC.";
    private static final String STREET_ADDRESS = "6507 PACIFIC AVE. #228";
    private static final String INVOICE_TITLE = "INVOICE";
    private static final String CITY_STATE_ZIP = "STOCKTON, CA 95207";
    private static final String POUND = "# ";
    private static final String EMAIL = "MOONLIGHTTRANSINC@GMAIL.COM";
    private static final String DATE_TITLE = "DATE: ";
    private static final String BILL_TO_TITLE = "BILL TO: ";
    private static final String LOAD_NUM = "LOAD # ";
    private static final String RATE_AMOUNT = "RATE AMOUNT";
    private static final String DETENTION = "DETENTION";
    private static final String LAYOVER = "LAYOVER";
    private static final String EXTRA_STOP = "EXTRA STOP";
    private static final String ADVANCE = "ADVANCE";
    private static final String LUMPER = "LUMPER";
    private static final String OTHER = "OTHER";
    private static final String BAL_DUE = "BAL DUE";
    private static final String DOLLAR_SIGN = "$";
    private static final String TRUCK_ORDER_NOT_USED = "TRUCK ORDER NOT USED\n";
    private static final String CHECKS_PAYABLE_TITLE = "\nPLEASE MAKE ALL CHECKS PAYABLE TO:\n";


    public static LineSeparator  createLine(){
        return new LineSeparator(new SolidLine(1f));
    }
    
    private static Text BOLD_UNDERLINE(String text) throws IOException {
        Text textBoldUnderline = new Text(text)
            .setBold()
            .setUnderline(1f, -2)
            .setFont(PdfFontFactory.createFont(TIMES_ROMAN))
            .setFontSize(12);
        return textBoldUnderline;
    }

    private static Text UNDERLINE(String text) throws IOException {
        Text textUnderline = new Text(text)
            .setUnderline(1f, -2)
            .setFont(PdfFontFactory.createFont(TIMES_ROMAN))
            .setFontSize(12);
        return textUnderline;
    }

    private static Text SET_FONT(String text) throws IOException{
        Text textNormal = new Text(text)
            .setFont(PdfFontFactory.createFont(TIMES_ROMAN))
            .setFontSize(12);
        return textNormal;
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
                Cell cell = new Cell().add(stopData[i][j].toUpperCase()).setBorder(Border.NO_BORDER)
                        .setFont(PdfFontFactory.createFont(TIMES_ROMAN)).setFontSize(12);
                if (i == 0) {
                    cell.setBold().setUnderline(1f, -2)
                            .setFont(PdfFontFactory.createFont(TIMES_ROMAN)).setFontSize(12);
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
    public static Paragraph title(long id) throws IOException {
        Paragraph titleParagraph = new Paragraph()
            .addTabStops(new TabStop(350, TabAlignment.LEFT))
            .add(BOLD_UNDERLINE(COMPANY_NAME))
            .add(new Text("\n"))
            .add(new Text("\n"))
            .add(SET_FONT(STREET_ADDRESS))
            .add(new Tab())
            .add(BOLD_UNDERLINE(INVOICE_TITLE))
            .add(new Text("\n"))
            .add(SET_FONT(CITY_STATE_ZIP))
            .add(new Tab())
            .add(SET_FONT(POUND+id))
            .add(new Text("\n"))
            .add(SET_FONT(EMAIL))
            .add(new Tab())
            .add(SET_FONT(DATE_TITLE + LocalDate.now()))
            .add(new Text("\n"));
        return titleParagraph;
    }

    /**
     * This function will create the bill to for the invoice. This will also include
     * the load #
     *
     * @return paragraph of the bill to
     */
    public static Paragraph billTo(String loadNum, CompanyInfo billToInfo) throws IOException {
        Paragraph billToParagraph = new Paragraph()
            .addTabStops(new TabStop(350, TabAlignment.LEFT))
            .add(BOLD_UNDERLINE(BILL_TO_TITLE))
            .add(new Tab())
            .add(SET_FONT(LOAD_NUM + loadNum.toUpperCase()))
            .add(new Text("\n"))
            .add(new Text("\n"))
            .add(SET_FONT(billToInfo.getName().toUpperCase()))
            .add(new Text("\n"))
            .add(SET_FONT(billToInfo.getStreetAddress().toUpperCase()))
            .add(new Text("\n"))
            .add(SET_FONT(billToInfo.getCity().toUpperCase() + ", " + billToInfo.getState().toUpperCase() + " " + Integer.toString(billToInfo.getZip())))
            .add(new Text("\n"));
        return billToParagraph;
    }

    public static Paragraph truckOrderNotUsedCase(TotalInvoiceBalance balances) throws IOException {
        Paragraph costs = new Paragraph();
        if(balances.isTruckOrderNotUsed()) {
            costs.add("\n\n")
            .addTabStops(new TabStop(5, TabAlignment.RIGHT));
            costs.add(BOLD_UNDERLINE(TRUCK_ORDER_NOT_USED));
            String n = String.format("%.2f%n",balances.getRateAmount());
            costs.add(UNDERLINE(RATE_AMOUNT))
                .add(new Tab())
                .add(UNDERLINE("$"))
                .add(SET_FONT(n));    
            String m = String.format("%.2f%n",balances.getTotalBalance());
            costs.add(SET_FONT(BAL_DUE))
                .add(new Tab())
                .add(SET_FONT(DOLLAR_SIGN))
                .add(SET_FONT(m));      
        }
        return costs;
    }
    
    public static Paragraph spacing() throws IOException {
     return new Paragraph().add("\n\n");      
    }

    /**
     * This function will create the total costs of the invoice, including
     * total rate, detention, layover, etc.
     *
     * @return total cost section of the invoice
     */
    public static Table totalCosts(TotalInvoiceBalance balances) throws IOException {
            ArrayList<Double> amountsVal = new ArrayList<Double>();
            ArrayList<String> amountsTitle = new ArrayList<String>();
            
            amountsVal.add(balances.getRateAmount());
            amountsTitle.add(RATE_AMOUNT);

            if (balances.getDetention() != 0.0) {
                amountsVal.add(balances.getDetention());
                amountsTitle.add(DETENTION);
            }
            if(balances.getLayover() != 0.0) {
                amountsVal.add(balances.getLayover());
                amountsTitle.add(LAYOVER);
            }
            if(balances.getAdvance() != 0.0) {
                amountsVal.add(balances.getAdvance());
                amountsTitle.add(ADVANCE);
            }
            if(balances.getExtraStop() != 0.0) {
                amountsVal.add(balances.getExtraStop());
                amountsTitle.add(EXTRA_STOP);
            }
            if(balances.getLumper() != 0.0) {
                amountsVal.add(balances.getLumper());
                amountsTitle.add(LUMPER);
            }
            if(balances.getOther() != 0.0) {
                amountsVal.add(balances.getOther());
                amountsTitle.add(OTHER);
            }
            amountsTitle.add(BAL_DUE);
            amountsVal.add(balances.getTotalBalance());

            String reformat;
                        
            int col = 10;
            int row = amountsTitle.size();
            
            String[][] amounts = new String[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (j == 0) {
                        amounts[i][j]= amountsTitle.get(i);
                    }
                    else if (j == 1) {
                        amounts[i][j] = DOLLAR_SIGN;
                    }
                    else if (j == 2) {
                        reformat = String.format("%.2f%n",amountsVal.get(i));
                        amounts[i][j]= reformat;
                    }
                    else {
                        amounts[i][j]= "0000000";
                    }
                }
            }

            float[] colWidth = new float[col];
            for (int i = 0; i < col; i++) {
                colWidth[i] = 10;
            }
            Table costsTable = new Table(UnitValue.createPointArray(colWidth)).useAllAvailableWidth();
            if(balances.isTruckOrderNotUsed() == false) {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (j < 3){
                        Cell cell = new Cell().add(amounts[i][j]).setVerticalAlignment(VerticalAlignment.BOTTOM)
                                .setFont(PdfFontFactory.createFont(TIMES_ROMAN)).setFontSize(12);
                        if(j == 2){
                            cell.setTextAlignment(TextAlignment.RIGHT);
                        }
                        if (i == amountsTitle.size()-2) {
                            cell.setBorderTop(Border.NO_BORDER);
                            cell.setBorderRight(Border.NO_BORDER);
                            cell.setBorderLeft(Border.NO_BORDER);
                        }
                        else {
                            cell.setBorder(Border.NO_BORDER);
                        }
                        costsTable.addCell(cell);
                    }
                    else {
                        Cell cell = new Cell().add(amounts[i][j]).setBorder(Border.NO_BORDER)
                            .setFontColor(Color.WHITE);
                        costsTable.addCell(cell);
                    }
                    }
                }
        }
        return costsTable;
    }


    public static Paragraph checksPayable() throws IOException {
        Paragraph costs = new Paragraph();
        costs.add(BOLD_UNDERLINE(CHECKS_PAYABLE_TITLE))
                .add(SET_FONT(COMPANY_NAME+"\n"))
                .add(SET_FONT(STREET_ADDRESS+"\n"))
                .add(SET_FONT(CITY_STATE_ZIP +"\n\n"))
                .add(new Tab());
        return costs;
        }
}
