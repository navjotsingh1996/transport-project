package com.transport.controllers;

import com.transport.services.invoicing.InvoicingManager;
import com.transport.services.invoicing.models.Address;
import com.transport.services.invoicing.models.CompanyInfo;
import com.transport.services.invoicing.models.InvoiceDto;
import com.transport.services.invoicing.models.InvoiceDto.invoiceSearchTypes;
import com.transport.services.invoicing.models.Stop;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class InvoicingController {
    @NonNull
    private final InvoicingManager im;
    // TODO: Add correct rest exceptions for the service level exceptions being thrown

    /**
     * Takes in a File and returns a pdf responseEntity
     *
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
    public ResponseEntity createInvoice(@RequestBody InvoiceDto invoice) {
        try {
            return downloadPdf(new File(im.createInvoice(invoice)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Failed to create invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unable to download pdf", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to download invoice");
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
        try {
            return im.getInvoice(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
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
     *
     * @param ids ids to be deleted
     */
    @PutMapping("/invoice/delete")
    public void deleteInvoices(@RequestBody List<Long> ids) {
        try {
            im.deleteInvoices(ids);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Edit and update an invoice in the database
     *
     * @param invoice that needs to be updated
     * @return the new pdf
     */
    @PutMapping("/invoice")
    public ResponseEntity editInvoices(@RequestBody InvoiceDto invoice) {
        try {
            return downloadPdf(new File(im.editInvoice(invoice)));
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Failed to create invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unable to download pdf", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to download invoice");
        }
    }

    /**
     * Search the invoices for a bill to that matches the query params
     *
     * @param name    query param for name
     * @param address query param for address
     * @return List of bill to's that match the query params
     */
    @GetMapping("/invoice/search")
    public List<Address> searchInvoice(@RequestParam("name") String name,
                                       @RequestParam("address") String address,
                                       @RequestParam("field") invoiceSearchTypes field) {
        return field == invoiceSearchTypes.BILLTO ?
                im.searchBillTo(name.toLowerCase(Locale.ENGLISH), address.toLowerCase(Locale.ENGLISH)).stream()
                        .map(billTo -> new Address(billTo.getName(), billTo.getStreetAddress(), billTo.getCity(),
                                billTo.getState(), billTo.getZip())).collect(Collectors.toList()) :
                im.searchStops(name.toLowerCase(Locale.ENGLISH), address.toLowerCase(Locale.ENGLISH))
                        .stream().map(stop -> new Address(stop.getName(), stop.getStreetAddress(), stop.getCity(),
                        stop.getState(), stop.getZip())).collect(Collectors.toList());
    }
}
