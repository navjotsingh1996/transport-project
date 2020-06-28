package com.transport.services.invoicing.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicingRepository extends JpaRepository<Invoice, Long> {

    @Query("select a.billTo from Invoice a where lower(a.billTo.name) like %?1% and " +
            "lower(a.billTo.streetAddress) like %?2%")
    List<CompanyInfo> findAllByCompanyInfoNameOrAddress(String name, String address);
}
