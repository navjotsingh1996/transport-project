package com.transport.services.invoicing.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicingRepository extends CrudRepository<Invoice, Long> {
}
