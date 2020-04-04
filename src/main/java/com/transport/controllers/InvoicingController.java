package com.transport.controllers;

import com.itextpdf.text.Document;
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

    @GetMapping("/invoice/{id}")
    public InvoiceDto getInvoice(@PathVariable long id) {
        return im.getInvoice(id);
    }
}
