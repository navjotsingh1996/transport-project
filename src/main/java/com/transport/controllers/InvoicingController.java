package com.transport.controllers;

import com.transport.services.invoicing.InvoicingManager;
import com.transport.services.invoicing.models.InvoiceDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class InvoicingController {
    @NonNull
    private final InvoicingManager im;

    /**
     * creates a new invoice
     * @param invoice new invoice that is to be created
     * @return id of the new invoice
     */
    @PostMapping("/invoice")
    public long createInvoice(@RequestBody InvoiceDto invoice) {
        return im.createInvoice(invoice);
    }

    @GetMapping("/invoice/{id}")
    public InvoiceDto getInvoice(@PathVariable long id) {
        return im.getInvoice(id);
    }
}
