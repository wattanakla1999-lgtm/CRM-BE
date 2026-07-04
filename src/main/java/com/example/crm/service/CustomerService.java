package com.example.crm.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crm.dto.CustomerRequest;
import com.example.crm.dto.CustomerResponse;
import com.example.crm.dto.CustomerStatsResponse;
import com.example.crm.entity.Customer;
import com.example.crm.entity.Tag;
import com.example.crm.exception.CustomerNotFoundException;
import com.example.crm.repository.CustomerRepository;
import com.example.crm.repository.TagRepository;

@Service
public class CustomerService {
    private static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

    private final CustomerRepository customerRepository;
    private final TagRepository tagRepository;

    public CustomerService(CustomerRepository customerRepository, TagRepository tagRepository) {
        this.customerRepository = customerRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable, String keyword) {
        Page<Customer> customers;
        if (keyword != null && !keyword.isBlank()) {
            customers = customerRepository.search(keyword, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        Map<Long, List<String>> tagsByCustomerId = loadTagsByCustomerId(customers.getContent());
        return customers.map(customer -> CustomerResponse.from(
                customer,
                tagsByCustomerId.getOrDefault(customer.getId(), List.of())
        ));
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

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = findEntityById(id);
        Map<Long, List<String>> tagsByCustomerId = loadTagsByCustomerId(List.of(customer));
        return CustomerResponse.from(customer, tagsByCustomerId.getOrDefault(customer.getId(), List.of()));
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

    private Map<Long, List<String>> loadTagsByCustomerId(List<Customer> customers) {
        List<Long> customerIds = customers.stream()
                .map(Customer::getId)
                .toList();

        if (customerIds.isEmpty()) {
            return Map.of();
        }

        return tagRepository.findByCustomerIdIn(customerIds).stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getCustomer().getId(),
                        Collectors.mapping(Tag::getName, Collectors.toList())
                ));
    }
}
