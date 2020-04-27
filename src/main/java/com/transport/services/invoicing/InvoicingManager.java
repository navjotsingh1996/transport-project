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
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
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
     * Convert from a dto to entity
     *
     * @param dto to be converted
     * @return an entity from the dto
     */
    private Invoice toEntity(InvoiceDto dto) {
        long date = Instant.now().toEpochMilli();
        if (dto.getDate() != 0) {
            date = dto.getDate();
        }
        if (dto.getId() != 0) {
            Invoice inv = ir.findById(dto.getId()).orElseThrow(() ->
                    new NoSuchElementException("Unable to find Invoice with " + dto.getId() + " id"));
            inv.setLoadNumber(dto.getLoadNumber());
            inv.setDate(date);
            inv.setBillTo(dto.getBillTo());
            inv.setStops(dto.getStops());
            inv.setBalances(dto.getBalances());
            return inv;
        }
        return new Invoice(dto.getLoadNumber(), date, dto.getBillTo(), dto.getStops(), dto.getBalances());
    }

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

    @Override
    public String createInvoice(InvoiceDto invoice) {
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
        ir.findAllById(ids).forEach(ir::delete);
    }

    @Override
    public String editInvoice(InvoiceDto invoice) {
        ir.save(toEntity(invoice));
        return createInvoicePdf(invoice);
    }

    private String getFileName(CompanyInfo info, String loadNumber) {
        List<String> companyName = Arrays.asList(info.getName().split(" "));
        StringBuilder filename = new StringBuilder(10);
        for (String f : companyName) {
            filename.append(f.charAt(0));
        }
        return INVOICE_PDS_PATH + filename + ' ' + loadNumber + ".pdf";
    }

    /**
     * Creates Invoice from InvoiceDto data
     * @param invoice to be created
     * @return a file containing the newly created invoice
     */
    private String createInvoicePdf(InvoiceDto invoice) {

        Document document = new Document();
        String fileName = getFileName(invoice.getBillTo(), invoice.getLoadNumber());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(new Paragraph("Invoice # " + Long.toString(invoice.getId())));
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
