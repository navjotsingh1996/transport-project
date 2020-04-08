package com.transport.commons;

import com.itextpdf.text.Paragraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DocumentCreationHelperTests {

    @Test
    public void titleTest() {
        assertThat(DocumentCreationHelper.title()).isEqualTo(new Paragraph(""));
    }

    @Test
    public void billToTest() {
        assertThat(DocumentCreationHelper.billTo()).isEqualTo(new Paragraph(""));
    }

    @Test
    public void stopsTest() {
        assertThat(DocumentCreationHelper.stops(new ArrayList<>())).isEqualTo(new Paragraph(""));
    }

    @Test
    public void totalCostTest() {
        assertThat(DocumentCreationHelper.totalCosts()).isEqualTo(new Paragraph(""));
    }
}
