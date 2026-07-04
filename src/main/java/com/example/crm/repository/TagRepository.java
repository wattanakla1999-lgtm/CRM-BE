package com.example.crm.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.crm.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {




    @Query("""
            SELECT t FROM Tag t
            WHERE t.customer.id IN :customerIds
            """)
    List<Tag> findByCustomerIdIn(Collection<Long> customerIds);



    
}
