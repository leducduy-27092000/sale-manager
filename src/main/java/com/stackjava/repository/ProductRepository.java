package com.stackjava.repository;


import org.springframework.data.repository.PagingAndSortingRepository;

import com.stackjava.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, String>{
}
