package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    boolean existsByUsername(String username);

    Customer findByUsername(String username);

    Customer findByCredentialId(byte[] bytes);
}
