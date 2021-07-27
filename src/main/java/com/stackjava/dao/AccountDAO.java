package com.stackjava.dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stackjava.entity.Account;
import com.stackjava.repository.AccountRepository;


 
@Transactional
@Repository
public class AccountDAO {
 
    @Autowired
    AccountRepository accountRepo;
 
    public Account findAccount(String userName) {
        return accountRepo.findById(userName).get();
    }
 
}