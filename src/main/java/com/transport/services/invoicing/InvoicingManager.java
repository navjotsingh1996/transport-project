package com.transport.services.invoicing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.services.invoicing.models.Invoice;
import com.transport.services.invoicing.models.InvoiceDto;
import com.transport.services.invoicing.models.InvoicingRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoicingManager implements InvoiceService {

    @NonNull
    private final InvoicingRepository ir;

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

    public long createInvoice(InvoiceDto invoice) {
        toEntity(invoice);
        // Save the company info
        return ir.save(toEntity(invoice)).getId();
    }

    // TODO: NEEDS LOCALIZATION
    public InvoiceDto getInvoice(long id) {
        Invoice invoice = ir.findById(id).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + id + " id"));
        return toDto(invoice);
    }
}
