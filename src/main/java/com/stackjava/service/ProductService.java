package com.stackjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.stackjava.entity.Product;
import com.stackjava.repository.ProductRepository;



public class ProductService {
	@Autowired
	private ProductRepository productRepo;
	
	public Page<Product> listAll(int pageNumber){
		//Sort sort = Sort.by(sortField);
		//sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber-1,5);
		return productRepo.findAll(pageable);
	}
}
