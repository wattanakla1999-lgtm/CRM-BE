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
    Page<Customer> findByPhoneStartingWith(String phonePrefix, Pageable pageable);
    Page<Customer> findByStatusNameIgnoreCase(String statusName, Pageable pageable);
    Page<Customer> findByCompanyNameIgnoreCase(String companyName, Pageable pageable);
    Page<Customer> findByCompanyNameIgnoreCaseAndStatusNameIgnoreCase(
            String companyName,
            String statusName,
            Pageable pageable
    );
    Page<Customer> findByCompanyNameIgnoreCaseOrStatusNameIgnoreCase(
            String companyName,
            String statusName,
            Pageable pageable
    );

    @Query("""
            SELECT c FROM Customer c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            """)
    Page<Customer> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query(value = """
            WITH matched_customers AS (
                SELECT c.id
                FROM customers c
                WHERE :hasSearch = FALSE

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.name) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.email) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.phone) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                JOIN companies comp ON comp.id = c.company_id
                WHERE :hasSearch = TRUE
                  AND LOWER(comp.name) LIKE :searchPattern
            )
            SELECT
                c.id AS id,
                c.name AS name,
                c.email AS email,
                c.phone AS phone,
                s.name AS statusName,
                comp.name AS companyName,
                COALESCE(STRING_AGG(DISTINCT t.name, ',' ORDER BY t.name), '') AS tags,
                c.created_at AS createdAt
            FROM matched_customers mc
            JOIN customers c ON c.id = mc.id
            LEFT JOIN statuses s ON s.id = c.status_id
            LEFT JOIN companies comp ON comp.id = c.company_id
            LEFT JOIN tags t ON t.customer_id = c.id
            GROUP BY
                c.id,
                c.name,
                c.email,
                c.phone,
                s.name,
                comp.name,
                c.created_at
            """,
            countQuery = """
            WITH matched_customers AS (
                SELECT c.id
                FROM customers c
                WHERE :hasSearch = FALSE

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.name) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.email) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                WHERE :hasSearch = TRUE
                  AND LOWER(c.phone) LIKE :searchPattern

                UNION

                SELECT c.id
                FROM customers c
                JOIN companies comp ON comp.id = c.company_id
                WHERE :hasSearch = TRUE
                  AND LOWER(comp.name) LIKE :searchPattern
            )
            SELECT COUNT(*)
            FROM matched_customers
            """,
            nativeQuery = true)
    Page<CustomerSummaryProjection> searchSummaries(
            @Param("hasSearch") boolean hasSearch,
            @Param("searchPattern") String searchPattern,
            Pageable pageable
    );

    long countByCreatedAtGreaterThanEqual(Instant createdAt);

    @Query("""
            SELECT COUNT(c) FROM Customer c
            WHERE LOWER(c.status.name) = 'vip'
            """)
    long countVipCustomers();

}
