package com.stackjava.dao;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stackjava.entity.Product;
import com.stackjava.form.ProductForm;
import com.stackjava.model.ProductInfo;
import com.stackjava.repository.ProductRepository;
@Transactional
@Repository
public class ProductDAO {
	
	@Autowired
	ProductRepository productRepo;
	
	public ProductInfo findProductInfo(String code) {
		Product product = null;
		try {
		 product = productRepo.findById(code).get();
		}catch(Exception e){
			return null;
		}
        return new ProductInfo(product.getCode(), product.getName(), product.getPrice());
    }
	
	public Product findProduct(String code) {
		Product product = null;
		try {
		 product = productRepo.findById(code).get();
		}catch(Exception e){
			return null;
		}
        return product;
	}
	
	public Page<Product> listAll(int pageNumber){
		//Sort sort = Sort.by(sortField);
		//sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber-1,5);
		return productRepo.findAll(pageable);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	public void save(ProductForm productForm) {
		String code = productForm.getCode();
		System.out.println("code la " +code);
		Product product = this.findProduct(code);
		 if (product == null) {
	            product = new Product();
	            product.setCreateDate(new Date());
	     }
	     product.setCode(code);
	     product.setName(productForm.getName());
	     product.setPrice(productForm.getPrice());   
	     productRepo.save(product);   
	}
	
}