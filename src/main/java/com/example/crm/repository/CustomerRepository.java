package com.example.crm.repository;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.crm.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Customer> findByPhoneContainingIgnoreCase(String phone, Pageable pageable);
    @Query("""
            SELECT c FROM Customer c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            """)
    Page<Customer> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByCreatedAtGreaterThanEqual(Instant createdAt);

    @Query("""
            SELECT COUNT(c) FROM Customer c
            WHERE LOWER(c.status.name) = 'vip'
            """)
    long countVipCustomers();

}
