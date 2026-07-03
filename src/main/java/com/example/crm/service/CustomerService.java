package com.example.crm.service;

import com.example.crm.dto.CustomerRequest;
import com.example.crm.dto.CustomerResponse;
import com.example.crm.entity.Customer;
import com.example.crm.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(CustomerResponse::from);
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
        customerRepository.deleteById(id);
    }

    private Customer findEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }
}
