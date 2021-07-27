package com.stackjava.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stackjava.entity.Order;
import com.stackjava.entity.OrderDetail;
import com.stackjava.entity.Product;
import com.stackjava.model.CartInfo;
import com.stackjava.model.CartLineInfo;
import com.stackjava.model.CustomerInfo;
import com.stackjava.model.OrderDetailInfo;
import com.stackjava.model.OrderInfo;
import com.stackjava.repository.OrderDetailRepository;
import com.stackjava.repository.OrderRepository;
import com.stackjava.repository.ProductRepository;


@Repository
public class OrderDAO{
	
	@Autowired
	OrderRepository orderRepo;
	
	@Autowired
	ProductRepository productRepo;
	
	@Autowired
	OrderDetailRepository orderDetailRepo;
	
	@Transactional(rollbackFor = Exception.class)
    public int getMaxOrderNum(){
		int value = (Integer)orderRepo.getMaxOrderNum();
		return value;
	}
	
	@Transactional(rollbackFor = Exception.class)
    public void saveOrder(CartInfo cartInfo) {
		 int orderNum =  this.getMaxOrderNum() + 1;
	     Order order = new Order();
	     order.setId(UUID.randomUUID().toString());
	     order.setOrderNum(orderNum);
	     order.setOrderDate(new Date());
	     order.setAmount(cartInfo.getAmountTotal());
	     
	     CustomerInfo customerInfo = cartInfo.getCustomerInfo();
	     System.out.println(customerInfo);
	     order.setCustomerName(customerInfo.getName());
	     order.setCustomerEmail(customerInfo.getEmail());
	     order.setCustomerPhone(customerInfo.getPhone());
	     order.setCustomerAddress(customerInfo.getAddress());
	     
	     orderRepo.save(order);
	     
	     List<CartLineInfo> lines = cartInfo.getCartLines();
	     
	        for (CartLineInfo line : lines) {
	            OrderDetail detail = new OrderDetail();
	            detail.setId(UUID.randomUUID().toString());
	            detail.setOrder(order);
	            detail.setAmount(line.getAmount());
	            detail.setPrice(line.getProductInfo().getPrice());
	            detail.setQuanity(line.getQuantity());
	 
	            String code = line.getProductInfo().getCode();
	            Product product = productRepo.findById(code).get();
	            detail.setProduct(product);
	            orderDetailRepo.save(detail); 
	        }
	        cartInfo.setOrderNum(orderNum);
	}
	public Page<Order> listAll(int pageNumber){
		Pageable pageable = PageRequest.of(pageNumber-1,5);
		return orderRepo.findAll(pageable);
	}
	
	public Order findOrder(String orderId) {
        return orderRepo.findById(orderId).get();
    }
 
    public OrderInfo getOrderInfo(String orderId) {
        Order order = this.findOrder(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInfo(order.getId(), order.getOrderDate(), //
                order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
                order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
    }
    
    public List<OrderDetailInfo> listOrderDetailInfos(String orderId) {
        List<OrderDetailInfo> list = orderDetailRepo.findAllRecordById(orderId);
        return list;
    }
 
}