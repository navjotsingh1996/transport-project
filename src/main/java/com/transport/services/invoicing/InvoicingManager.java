package com.transport.services.invoicing;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.transport.services.invoicing.models.CompanyInfo;
import com.transport.services.invoicing.models.Invoice;
import com.transport.services.invoicing.models.InvoiceDto;
import com.transport.services.invoicing.models.InvoicingRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoicingManager implements InvoiceService {

    @NonNull
    private final InvoicingRepository ir;

    private static final String INVOICE_PDS_PATH = "documents/invoices/";

    /**
     * Convert from a dto to entity
     *
     * @param dto to be converted
     * @return an entity from the dto
     */
    Invoice toEntity(InvoiceDto dto) {
        return new Invoice(dto.getLoadNumber(), LocalDate.now(), dto.getBillTo(), dto.getStops(), dto.getBalances());
    }

    /**
     * Convert from entity to dto
     *
     * @param entity to be converted
     * @return a dto from the entity
     */
    InvoiceDto toDto(Invoice entity) {
        return new InvoiceDto(entity.getId(), entity.getLoadNumber(), entity.getDate(), entity.getBillTo(),
                entity.getStops(), entity.getBalances());
    }

    public String createInvoice(InvoiceDto invoice) {
        toEntity(invoice);
        // Save the company info
        ir.save(toEntity(invoice));
        return createInvoicePdf(invoice);
    }

    // TODO: NEEDS LOCALIZATION
    public InvoiceDto getInvoice(long id) {
        Invoice invoice = ir.findById(id).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + id + " id"));
        return toDto(invoice);
    }

    private String getFileName(CompanyInfo info, String loadNumber) {
        List<String> companyName = Arrays.asList(info.getName().split(" "));
        String filename = "";
        for (String f : companyName) {
            filename += f.charAt(0);
        }
        return INVOICE_PDS_PATH + filename + ' ' + loadNumber + ".pdf";
    }

    private String createInvoicePdf(InvoiceDto invoice) {

        Document document = new Document();
        String fileName = getFileName(invoice.getBillTo(), invoice.getLoadNumber());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(new Paragraph("A Hello World PDF document."));
            document.close();
            writer.close();
        } catch (DocumentException e) {
            log.error("Something is wrong with the document", e);
        } catch (FileNotFoundException e) {
            log.error("Unable to find file", e);
        }
        return fileName;
    }
}
