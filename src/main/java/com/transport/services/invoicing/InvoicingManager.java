package com.transport.services.invoicing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.transport.commons.DocumentCreationHelper;
import com.transport.services.invoicing.models.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
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
        if (info.getName().isEmpty()) {
            throw new IllegalArgumentException("Bill to must have a company name");
        }
        List<String> companyName = Arrays.asList(info.getName().split(" "));
        StringBuilder filename = new StringBuilder(10);
        for (String f : companyName) {
            filename.append(f.charAt(0));
        }
        return INVOICE_PDS_PATH + filename + ' ' + loadNumber + ".pdf";
    }

    /**
     * Validates teh stops, making sure there aer a valid number of them
     *
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
     * Convert from a dto to entity
     *
     * @param dto to be converted
     * @return an entity from the dto
     */
    Invoice toEntity(InvoiceDto dto) {
        long date = Instant.now().getEpochSecond();
        if (dto.getDate() != 0) {
            date = dto.getDate();
        }
        Invoice invoice = new Invoice(
                dto.getId(), dto.getLoadNumber(), date, dto.getBillTo(), dto.getStops(), dto.getBalances());
        invoice.setId(dto.getId());
        return invoice;
    }

    @Override
    public String createInvoice(InvoiceDto invoice) throws IOException {
        validateStops(invoice.getStops());
        toEntity(invoice);
        return createInvoicePdf(toDto(ir.save(toEntity(invoice))));

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
    public String editInvoice(InvoiceDto invoice) throws IOException {
        validateStops(invoice.getStops());
        Invoice inv = ir.findById(invoice.getId()).orElseThrow(() ->
                new NoSuchElementException("Unable to find Invoice with " + invoice.getId() + " id"));
        inv.setBalances(invoice.getBalances());
        inv.setStops(invoice.getStops());
        inv.setBillTo(invoice.getBillTo());
        inv.setDate(Instant.now().getEpochSecond());
        inv.setLoadNumber(invoice.getLoadNumber());
        return createInvoicePdf(toDto(ir.save(inv)));
    }

    /**
     * Creates Invoice from InvoiceDto data
     *
     * @param invoice to be created
     * @return a file containing the newly created invoice
     */
    private String createInvoicePdf(InvoiceDto invoice) throws IOException {
        ArrayList<Stop> pickup = new ArrayList<>();
        ArrayList<Stop> delivery = new ArrayList<>();
        String fileName = getFileName(invoice.getBillTo(), invoice.getLoadNumber());
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDoc);
        document.add(DocumentCreationHelper.title(invoice.getId()));
        document.add(DocumentCreationHelper.createLine());
        document.add(DocumentCreationHelper.billTo(invoice.getLoadNumber(), invoice.getBillTo()));
        document.add(DocumentCreationHelper.createLine());

        invoice.getStops().forEach(stop -> {
            if (stop.getType() == Stop.StopType.PICKUP) {
                pickup.add(stop);
            } else {
                delivery.add(stop);
            }
        });

        Table pickupTable = DocumentCreationHelper.createBorderlessTable(pickup, Stop.StopType.PICKUP);
        Table deliveryTable = DocumentCreationHelper.createBorderlessTable(delivery, Stop.StopType.DELIVERY);
        document.add(pickupTable);
        document.add(DocumentCreationHelper.createLine());
        document.add(deliveryTable);
        document.add(DocumentCreationHelper.createLine());
        document.add(DocumentCreationHelper.spacing());
        document.add(DocumentCreationHelper.truckOrderNotUsedCase(invoice.getBalances()));
        document.add(DocumentCreationHelper.totalCosts(invoice.getBalances()));
        document.add(DocumentCreationHelper.checksPayable());
        document.close();
        return fileName;
    }

    @Override
    public List<CompanyInfo> searchBillTo(String name, String address) {
        Map<String, CompanyInfo> addresses = new HashMap<>();

        ir.findAllByCompanyInfoNameOrAddress(name, address).forEach(billTo -> {
            String add = billTo.getName() + billTo.getStreetAddress() + billTo.getCity() + billTo.getState() + billTo.getZip();
            addresses.put(add.replaceAll("\\s+", ""), billTo);
        });
        return new ArrayList<>(addresses.values());
    }

    // TODO: Inefficient, need to do a query through sql to search for this or switch to Elastic search
    @Override
    public List<Stop> searchStops(String name, String address) {
        List<Invoice> allInvoices = ir.findAll();
        List<Stop> stops = new ArrayList<>();
        Integer a = 0;
        allInvoices.forEach(invoice -> {
            invoice.getStops().forEach(stop -> {
                if (stop.getName().toLowerCase(Locale.ENGLISH).contains(name) ||
                        stop.getStreetAddress().toLowerCase(Locale.ENGLISH).contains(address)) {
                    stops.add(stop);
                }
            });
        });
        Map<String, Stop> addresses = new HashMap<>();

        stops.forEach(stop -> {
            String add = stop.getName() + stop.getStreetAddress() + stop.getCityStateZip();
            addresses.put(add.replaceAll("\\s+", ""), stop);
        });
        return new ArrayList<>(addresses.values());
    }
}
