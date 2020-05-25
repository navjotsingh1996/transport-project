package com.transport.services.invoicing;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.transport.commons.DocumentCreationHelper;
import com.transport.services.invoicing.models.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoicingManager implements InvoiceService {

    private static final String INVOICE_PDS_PATH = "documents/invoices/";
    @NonNull
    private final InvoicingRepository ir;

    /**
     * Convert from entity to dto
     *
     * @param entity to be converted
     * @return a dto from the entity
     */
    private static InvoiceDto toDto(Invoice entity) {
        return new InvoiceDto(entity.getId(), entity.getLoadNumber(), entity.getDate(), entity.getBillTo(),
                entity.getStops(), entity.getBalances());
    }

    /**
     * Creates the file name of the pdf using the company name and load number
     *
     * @param info       company info
     * @param loadNumber loadnumber of invoice
     * @return name of pdf
     */
    private static String getFileName(CompanyInfo info, String loadNumber) {
        List<String> companyName = Arrays.asList(info.getName().split(" "));
        StringBuilder filename = new StringBuilder(10);
        for (String f : companyName) {
            filename.append(f.charAt(0));
        }
        return INVOICE_PDS_PATH + filename + ' ' + loadNumber + ".pdf";
    }

    /**
     * Convert from a dto to entity
     *
     * @param dto to be converted
     * @return an entity from the dto
     */
    Invoice toEntity(InvoiceDto dto) {
        long date = Instant.now().toEpochMilli();
        if (dto.getDate() != 0) {
            date = dto.getDate();
        }
        return new Invoice(dto.getLoadNumber(), date, dto.getBillTo(), dto.getStops(), dto.getBalances());
    }

    @Override
    public String createInvoice(InvoiceDto invoice) {
        validateStops(invoice.getStops());
        toEntity(invoice);
        ir.save(toEntity(invoice));
        return createInvoicePdf(invoice);
    }

    @Override
    public InvoiceDto getInvoice(long id) {
        Invoice invoice = ir.findById(id).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + id + " id"));
        return toDto(invoice);
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        return ir.findAll().stream().map(InvoicingManager::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteInvoices(List<Long> ids) {
        ids.forEach(id -> ir.delete(ir.findById(id).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + id + " id"))));
    }

    @Override
    public String editInvoice(InvoiceDto invoice) {
        validateStops(invoice.getStops());
        Invoice inv = ir.findById(invoice.getId()).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + invoice.getId() + " id"));
        inv.setBalances(invoice.getBalances());
        inv.setStops(invoice.getStops());
        inv.setBillTo(invoice.getBillTo());
        inv.setDate(Instant.now().toEpochMilli());
        inv.setLoadNumber(invoice.getLoadNumber());
        ir.save(inv);
        return createInvoicePdf(invoice);
    }

    /**
     * Validates teh stops, making sure there aer a valid number of them
     * @param stops array of stops
     */
    private static void validateStops(List<Stop> stops) {
        int pickups = 0;
        int deliveries = 0;
        if (stops.isEmpty()) {
            throw new IllegalStateException("Must have at least two stops");
        }
        if (stops.size() == 1) {
            throw new IllegalStateException("Must have at least 2 stops");
        }
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getType() == Stop.StopType.PICKUP) {
                pickups++;
            } else if (stops.get(i).getType() == Stop.StopType.DELIVERY) {
                deliveries++;
            } else {
                throw new IllegalStateException("Stop type unknown: " + stops.get(i));
            }
        }
        if (pickups == 0 || deliveries == 0) {
            throw new IllegalStateException("Must have at least one stop and one delivery");
        }
    }

    /**
     * Creates Invoice from InvoiceDto data
     *
     * @param invoice to be created
     * @return a file containing the newly created invoice
     */
    private String createInvoicePdf(InvoiceDto invoice) {

        Document document = new Document();
        String fileName = getFileName(invoice.getBillTo(), invoice.getLoadNumber());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(DocumentCreationHelper.title());
            document.close();
            writer.close();
            // TODO: Need to throw internal server error
        } catch (DocumentException e) {
            log.error("Something is wrong with the document", e);
        } catch (FileNotFoundException e) {
            log.error("Unable to find file", e);
        }
        return fileName;
    }
}
