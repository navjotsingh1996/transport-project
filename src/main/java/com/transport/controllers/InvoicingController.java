package com.transport.controllers;

import com.transport.services.invoicing.InvoicingManager;
import com.transport.services.invoicing.models.InvoiceDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class InvoicingController {
    @NonNull
    private final InvoicingManager im;

    /**
     * creates a new invoice
     *
     * @param invoice new invoice that is to be created
     * @return id of the new invoice
     */
    @PostMapping("/invoice")
    public ResponseEntity<InputStreamResource> createInvoice(@RequestBody InvoiceDto invoice) {
        try {
            File file = new File(im.createInvoice(invoice));
            HttpHeaders respHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/pdf");
            respHeaders.setContentType(mediaType);
            respHeaders.setContentLength(file.length());
            respHeaders.setContentDispositionFormData("attachment", file.getName());
            InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unable to download pdf", e);
            return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * gets invoice details for a specified invoice
     *
     * @param id of invoices
     * @return invoice details
     */
    @GetMapping("/invoice/{id}")
    public InvoiceDto getInvoice(@PathVariable long id) {
        return im.getInvoice(id);
    }

    /**
     * Gets all of the invoices
     *
     * @return a list of invoices
     */
    @GetMapping("/invoice")
    public List<InvoiceDto> getAllInvoices() {
        return im.getAllInvoices();
    }

    /**
     * Delete all invoices with the given ids
     * @param ids ids to be deleted
     */
    @DeleteMapping("/invoice")
    public void deleteInvoices(@RequestBody List<Long> ids) {
        im.deleteInvoices(ids);
    }

    /**
     * Edit and update an invoices in the database
     * @param invoices that were edited and need to be updated
     * @return id list of edited invoices
     */
    @PutMapping("/invoice")
    public List<Long> editInvoices(@RequestBody List<InvoiceDto> invoices){
        return im.editInvoices(invoices);
    }
}
