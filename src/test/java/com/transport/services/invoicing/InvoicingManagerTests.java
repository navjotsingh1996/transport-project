package com.transport.services.invoicing;

import com.transport.services.invoicing.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvoicingManagerTests {
    private final CompanyInfo companyInfoTest1 = new CompanyInfo("test", "test", "test", "TE", 95212);
    private final TotalInvoiceBalance totalInvoiceBalanceTest1 = new TotalInvoiceBalance(10.10, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private final Stop pickup = new Stop(Instant.now().getEpochSecond(), "PICKUP", "TEST", "TE", "TEST", 12345, Stop.StopType.PICKUP);
    private final Stop delivery = new Stop(Instant.now().getEpochSecond(), "DELIVERY", "TEST", "TE", "TEST", 12345, Stop.StopType.DELIVERY);
    private final Invoice testInv1 = new Invoice("1234", Instant.now().getEpochSecond(),
            companyInfoTest1, Arrays.asList(pickup, delivery), totalInvoiceBalanceTest1);
    private final InvoiceDto testInvDto = new InvoiceDto(0L, "1234", Instant.now().getEpochSecond(),
            companyInfoTest1, Arrays.asList(pickup, delivery), totalInvoiceBalanceTest1);

    private InvoicingRepository irMock;
    private InvoicingManager im;

    private static final String INVOICE_FILE_PATH = "documents/invoices/";

    @BeforeEach
    void setup() {
        irMock = mock(InvoicingRepository.class);
        im = new InvoicingManager(irMock);
    }

    @Test
    void constructorTests() {
        assertThatThrownBy(() -> new InvoicingManager(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getInvoiceTests() {
        assertThatThrownBy(() -> im.getInvoice(2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Unable to find Invoice with " + 2L + " id");

        when(irMock.findById(1L)).thenReturn(Optional.of(testInv1));
        assertThat(im.getInvoice(1L)).isEqualTo(testInvDto);
    }

    @Test
    void createInvoiceTests() {
        assertThatThrownBy(() -> im.createInvoice(null))
                .isInstanceOf(NullPointerException.class);

        when(irMock.save(im.toEntity(testInvDto))).thenReturn(testInv1);
        assertThat(im.createInvoice(testInvDto)).isEqualToIgnoringCase(INVOICE_FILE_PATH + "t 1234.pdf");
    }

    @Test
    void invalidStopsTests() {
        InvoiceDto invoiceNoStops = new InvoiceDto(testInvDto.getId(), testInvDto.getLoadNumber(), testInvDto.getDate(),
                testInvDto.getBillTo(), new ArrayList<>(), testInvDto.getBalances());
        Stop pickupStop = new Stop(Instant.now().getEpochSecond(), "pickup", "test", "TE", "Test", 12345, Stop.StopType.PICKUP);
        Stop deliveryStop = new Stop(Instant.now().getEpochSecond(), "delivery", "test", "TE", "Test", 12345, Stop.StopType.DELIVERY);
        InvoiceDto invoiceSameStops = new InvoiceDto(testInvDto.getId(), testInvDto.getLoadNumber(), testInvDto.getDate(),
                testInvDto.getBillTo(), Arrays.asList(pickupStop, pickupStop), testInvDto.getBalances());
        InvoiceDto invoiceOneStop = new InvoiceDto(testInvDto.getId(), testInvDto.getLoadNumber(), testInvDto.getDate(),
                testInvDto.getBillTo(), Arrays.asList(deliveryStop), testInvDto.getBalances());
        assertThatThrownBy(() -> im.createInvoice(invoiceNoStops))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Must have at least two stops");
        assertThatThrownBy(() -> im.createInvoice(invoiceSameStops))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Must have at least one stop and one delivery");
        assertThatThrownBy(() -> im.createInvoice(invoiceSameStops))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Must have at least one stop and one delivery");
    }

    @Test
    void getAllInvoicesTests() {
        when(irMock.findAll()).thenReturn(new ArrayList<>());
        assertThat(im.getAllInvoices()).isEqualTo(new ArrayList<>());

        when(irMock.findAll()).thenReturn(Arrays.asList(testInv1, testInv1));
        assertThat(im.getAllInvoices().size()).isEqualTo(2);
    }

    @Test
    void deleteInvoicesTests() {
        im.deleteInvoices(new ArrayList<>());
        assertThatThrownBy(() -> im.deleteInvoices(Arrays.asList(1L, 2L)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Unable to find Invoice with 1 id");
        doNothing().when(irMock).delete(any());
        when(irMock.findById(1L)).thenReturn(Optional.of(testInv1));
        when(irMock.findById(2L)).thenReturn(Optional.of(testInv1));
        im.deleteInvoices(Arrays.asList(1L, 2L));
    }

    @Test
    void editInvoiceTests() {
        assertThatThrownBy(() -> im.editInvoice(testInvDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Unable to find Invoice with 0 id");

        when(irMock.findById(testInvDto.getId())).thenReturn(Optional.of(testInv1));
        assertThat(im.editInvoice(testInvDto)).isEqualToIgnoringCase(INVOICE_FILE_PATH + "t 1234.pdf");
    }

}
