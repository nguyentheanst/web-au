package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    public boolean existsByUsername(String username) {
        return customerRepo.existsByUsername(username);
    }

    public Customer create(String username, byte[] credentialId, byte[] publicKeyCose, long signatureCount) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setCredentialId(credentialId);
        customer.setPublicKeyCose(publicKeyCose);
        customer.setSignatureCount(signatureCount);

        return customerRepo.save(customer);
    }

    public Customer findByUsername(String username) {
        return customerRepo.findByUsername(username);
    }
}
