package com.stackjava.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.stackjava.entity.Account;

public interface AccountRepository extends PagingAndSortingRepository<Account, String>{

}
