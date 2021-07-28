package com.stackjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stackjava.entity.OrderDetail;
import com.stackjava.model.OrderDetailInfo;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String>{
	
//	@Query("Select d"
//			+ " from OrderDetail d  where d.order.id = :orderId ")
//	List<OrderDetail> findAllRecordById(@Param("orderId") String orderId);
	String s="com.stackjava.model.OrderDetailInfo";
	@Query("Select new com.stackjava.model.OrderDetailInfo(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount)"
			+ " from OrderDetail d  where d.order.id = :orderId ")
	List<OrderDetailInfo> findAllRecordById(@Param("orderId") String orderId);
}
