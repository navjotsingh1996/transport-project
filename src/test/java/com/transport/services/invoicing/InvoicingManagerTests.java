package com.transport.services.invoicing;

import com.transport.services.invoicing.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvoicingManagerTests {
    private final CompanyInfo companyInfoTest1 = new CompanyInfo("test", "test", "test", "TE", 95212);
    private final TotalInvoiceBalance totalInvoiceBalanceTest1 = new TotalInvoiceBalance(10.10, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private final Invoice testInv1 = new Invoice("1234", Instant.now().toEpochMilli(),
            companyInfoTest1, new ArrayList<>(), totalInvoiceBalanceTest1);
    private final InvoiceDto testInvDto = new InvoiceDto(0L, "1234", Instant.now().toEpochMilli(),
            companyInfoTest1, new ArrayList<>(), totalInvoiceBalanceTest1);

    private InvoicingRepository irMock;
    private InvoicingManager im;

    private static String INVOICE_FILE_PATH = "documents/invoices/";

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

}
