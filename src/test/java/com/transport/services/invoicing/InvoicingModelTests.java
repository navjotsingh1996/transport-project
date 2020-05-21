package com.transport.services.invoicing;

import com.transport.services.invoicing.models.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.Arrays;

import static com.transport.services.invoicing.models.Stop.StopType.DELIVERY;
import static com.transport.services.invoicing.models.Stop.StopType.PICKUP;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class InvoicingModelTests {

    @Test
    void companyInfoTests() {
        CompanyInfo companyInfo1 = new CompanyInfo("TEST", "TEST", "TEST", "TE", 0);
        assertThat(companyInfo1.getName()).isEqualToIgnoringCase("TEST");
        assertThat(companyInfo1.getStreetAddress()).isEqualToIgnoringCase("TEST");
        assertThat(companyInfo1.getCity()).isEqualToIgnoringCase("TEST");
        assertThat(companyInfo1.getState()).isEqualToIgnoringCase("TE");
        assertThat(companyInfo1.getZip()).isEqualTo(0);
    }
    @Test
    void stopTests() {
        long date = Instant.now().toEpochMilli();
        Stop stop1 = new Stop(date, "stopName", "stopCity", "stopState", "stopAdd", 12345, PICKUP);
        Stop stop2 = new Stop(date, "stopName", "stopCity", "stopState", null, 12345, PICKUP);
        assertThat(stop1.getDate()).isEqualTo(date);
        assertThat(stop1.getName()).isEqualTo("stopName");
        assertThat(stop1.getCity()).isEqualTo("stopCity");
        assertThat(stop1.getState()).isEqualTo("stopState");
        assertThat(stop1.getStreetAddress()).isEqualTo("stopAdd");
        assertThat(stop1.getZip()).isEqualTo(12345);
        assertThat(stop1.getType()).isEqualTo(PICKUP);
    }

    @Test
    void balanceTests() {
        // TODO: Fix failing test (decimal place issue)
        // TODO: if truckordernotused then only compute ratebalance and ignore everything else
        TotalInvoiceBalance balance = new TotalInvoiceBalance(1.1, false, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1);
        assertThat(balance.getTotalBalance()).isEqualTo(7.7);
        assertThat(balance.getRateAmount()).isEqualTo(1.1);
        assertThat(balance.getAdvance()).isEqualTo(1.1);
        assertThat(balance.getDetention()).isEqualTo(1.1);
        assertThat(balance.getLayover()).isEqualTo(1.1);
        assertThat(balance.getLumper()).isEqualTo(1.1);
        assertThat(balance.getOther()).isEqualTo(1.1);
        assertThat(balance.getExtraStop()).isEqualTo(1.1);
        assertThat(balance.isTruckOrderNotUsed()).isEqualTo(false);
    }

    @Test
    void invoiceDtoTests() {
        long date = Instant.now().toEpochMilli();
        CompanyInfo companyInfo1 = new CompanyInfo("TEST", "TEST", "TEST", "TE", 0);
        Stop stop1 = new Stop(date, "stopName", "stopCity", "stopState", "stopAdd", 12345, PICKUP);
        Stop stop2 = new Stop(date, "stopName", "stopCity", "stopState", "stopAdd", 12345, DELIVERY);
        TotalInvoiceBalance balance = new TotalInvoiceBalance(1.1, false, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1);

        InvoiceDto invoice = new InvoiceDto(1L, "1234", date, companyInfo1, Arrays.asList(stop1, stop2), balance);
        assertThat(invoice.getId()).isEqualTo(1L);
        assertThat(invoice.getLoadNumber()).isEqualTo("1234");
        assertThat(invoice.getDate()).isEqualTo(date);
        assertThat(invoice.getBillTo()).isEqualTo(companyInfo1);
        assertThat(invoice.getStops()).isEqualTo(Arrays.asList(stop1, stop2));
        assertThat(invoice.getBalances()).isEqualTo(balance);
    }

    @Test
    void invoiceTests() {
        long date = Instant.now().toEpochMilli();
        CompanyInfo companyInfo1 = new CompanyInfo("TEST", "TEST", "TEST", "TE", 0);
        Stop stop1 = new Stop(date, "stopName", "stopCity", "stopState", "stopAdd", 12345, PICKUP);
        Stop stop2 = new Stop(date, "stopName", "stopCity", "stopState", "stopAdd", 12345, DELIVERY);
        TotalInvoiceBalance balance = new TotalInvoiceBalance(1.1, false, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1);

        Invoice invoice = new Invoice(1L, "1234", date, companyInfo1, Arrays.asList(stop1, stop2), balance);
        assertThat(invoice.getId()).isEqualTo(1L);
        assertThat(invoice.getLoadNumber()).isEqualTo("1234");
        assertThat(invoice.getDate()).isEqualTo(date);
        assertThat(invoice.getBillTo()).isEqualTo(companyInfo1);
        assertThat(invoice.getStops()).isEqualTo(Arrays.asList(stop1, stop2));
        assertThat(invoice.getBalances()).isEqualTo(balance);

        new Invoice("loadNumber", date, companyInfo1, Arrays.asList(stop1, stop2), balance);
        new Invoice("loadNumber", companyInfo1, Arrays.asList(stop1, stop2), balance);
    }
}
