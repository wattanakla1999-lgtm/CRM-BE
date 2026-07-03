package com.example.crm.service;

import com.example.crm.dto.CustomerStatsResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crm.dto.CustomerRequest;
import com.example.crm.dto.CustomerResponse;
import com.example.crm.entity.Customer;
import com.example.crm.exception.CustomerNotFoundException;
import com.example.crm.repository.CustomerRepository;

@Service
public class CustomerService {
    private static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<CustomerResponse> findAll(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return customerRepository.search(keyword, pageable)
                    .map(CustomerResponse::from);
        }

        return customerRepository.findAll(pageable)
                .map(CustomerResponse::from);
    }

    public CustomerStatsResponse getStats() {
        Instant startOfMonth = ZonedDateTime.now(BANGKOK_ZONE)
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay(BANGKOK_ZONE)
                .toInstant();

        return new CustomerStatsResponse(
                customerRepository.count(),
                customerRepository.countByCreatedAtGreaterThanEqual(startOfMonth),
                customerRepository.countVipCustomers(),
                0
        );
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(findEntityById(id));
    }

    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer(request.name(), request.email(), request.phone());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findEntityById(id);
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        return CustomerResponse.from(customer);
    }

    public void delete(Long id) {
        Customer customer = findEntityById(id);
        customerRepository.delete(customer);
    }

    private Customer findEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
