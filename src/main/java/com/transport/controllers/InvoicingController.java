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
import java.io.FileNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class InvoicingController {
    @NonNull
    private final InvoicingManager im;

    /**
     * Takes in a File and returns a pdf responseEntity
     * @param file to be sent by the controller
     * @return ResponseEntity (pdf file)
     * @throws FileNotFoundException if the file is not found
     */
    private static ResponseEntity<InputStreamResource> downloadPdf(File file) throws FileNotFoundException {
        HttpHeaders respHeaders = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/pdf");
        respHeaders.setContentType(mediaType);
        respHeaders.setContentLength(file.length());
        respHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

    /**
     * creates a new invoice
     *
     * @param invoice new invoice that is to be created
     * @return the new pdf
     */
    @PostMapping("/invoice")
    public ResponseEntity<InputStreamResource> createInvoice(@RequestBody InvoiceDto invoice) {
        try {
            return downloadPdf( new File(im.createInvoice(invoice)));
        } catch (Exception e) {
            log.error("Unable to download pdf", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets invoice details for a specified invoice
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
    @PutMapping("/invoice/delete")
    public void deleteInvoices(@RequestBody List<Long> ids) {
        im.deleteInvoices(ids);
    }

    /**
     * Edit and update an invoice in the database
     * @param invoice that needs to be updated
     * @return the new pdf
     */
    @PutMapping("/invoice")
    public ResponseEntity<InputStreamResource> editInvoices(@RequestBody InvoiceDto invoice){
        try {
            return downloadPdf( new File(im.editInvoice(invoice)));
        } catch (Exception e) {
            log.error("Unable to download pdf", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
