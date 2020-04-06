package com.transport.services.invoicing;

import com.transport.services.invoicing.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DataJpaTest
public class InvoicingManagerTests {
    @Mock
    private InvoicingRepository irMock;
    private InvoicingManager im;

    private final CompanyInfo companyInfoTest1 = new CompanyInfo("test", "test", "test", "TE", 95212);
    private final TotalInvoiceBalance totalInvoiceBalanceTest1 = new TotalInvoiceBalance(10.10, false, 0, 0, 0, 0, 0);
    private final Invoice testInv1 = new Invoice( "1234", LocalDate.now(),
            companyInfoTest1, new ArrayList<>(), totalInvoiceBalanceTest1);
    private final InvoiceDto testInvDto = new InvoiceDto(1L, "123", LocalDate.now(),
            companyInfoTest1, new ArrayList<>(), totalInvoiceBalanceTest1);

    @BeforeEach
    void setup() {
        im = new InvoicingManager(irMock);
    }

    @Test
    public void constructorTests() {
        assertThatThrownBy(() -> new InvoicingManager(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getInvoiceTests() {
        assertThatThrownBy(() -> im.getInvoice(2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Unable to find Invoice with " + 2L + " id");

        when(irMock.findById(1L)).thenReturn(Optional.of(testInv1));
        assertThat(im.getInvoice(1L)).isEqualTo(testInv1);
    }

    @Test
    public void createInvoiceTests() {
        assertThatThrownBy(() -> im.createInvoice(null))
                .isInstanceOf(NullPointerException.class);

        when(irMock.save(im.toEntity(testInvDto))).thenReturn(testInv1);
        assertThat(im.createInvoice(testInvDto)).isEqualToIgnoringCase("t 1234.pdf");
    }

}
