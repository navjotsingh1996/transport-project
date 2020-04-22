package com.transport.services.invoicing.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicingRepository extends JpaRepository<Invoice, Long> {
}
