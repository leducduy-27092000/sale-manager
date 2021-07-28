package com.stackjava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.stackjava.entity.Order;

public interface OrderRepository extends PagingAndSortingRepository<Order, String>{
//	@Query("SELECT u FROM User u WHERE u.status = :status and u.name = :name")
//    User findUserByNamedParams(@Param("status") Integer status, @Param("name") String name);

    // Native SQL
    @Query(value = "SELECT MAX(o.order_num) FROM orders o ", nativeQuery = true)
    int getMaxOrderNum();
}
