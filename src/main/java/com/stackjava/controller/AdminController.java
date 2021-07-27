package com.stackjava.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stackjava.dao.OrderDAO;
import com.stackjava.dao.ProductDAO;
import com.stackjava.entity.Order;
import com.stackjava.entity.Product;
import com.stackjava.form.ProductForm;
import com.stackjava.model.OrderDetailInfo;
import com.stackjava.model.OrderInfo;

@Controller
@Transactional
public class AdminController {
	@Autowired
    private OrderDAO orderDAO;
 
    @Autowired
    private ProductDAO productDAO;
    
    @RequestMapping(value= {"/admin/login"},method = RequestMethod.GET)
    public String login(Model model, HttpServletRequest request) {
    	Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
				if(cookie.getName().equals("userC")) {
					model.addAttribute("userC",cookie.getValue());
				}
				if(cookie.getName().equals("passC")) {
					model.addAttribute("passC",cookie.getValue());
				}
			}
        }
     
    	return "login";
    }
    
    @RequestMapping(value= {"/admin/accountInfo"},method = RequestMethod.GET)
    public String accountInfo(Model model, HttpServletResponse response) {
    	UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	System.out.println(userDetails.getUsername());
    	System.out.println(userDetails.getPassword());
        System.out.println(userDetails.isEnabled());
        
        model.addAttribute("userDetails", userDetails);

    	return "accountInfo";
    }
    
    
    @RequestMapping("/admin/orderList/{pageNumber}")
    public String listProductHandler(Model model,@PathVariable("pageNumber") int currentPage) {
    	Page<Order> page = orderDAO.listAll(currentPage);
		List<Order> listOrder = page.getContent();
		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("listOrder", listOrder);
    	return "orderList";
    }

    @RequestMapping(value = { "/admin/order" }, method = RequestMethod.GET)
    public String orderView(Model model, @RequestParam("orderId") String orderId) {
    	OrderInfo orderInfo = null;
        if (orderId != null) {
            orderInfo = this.orderDAO.getOrderInfo(orderId);
        }
        if (orderInfo == null) {
            return "redirect:/admin/orderList";
        }
        List<OrderDetailInfo> details = this.orderDAO.listOrderDetailInfos(orderId);
        orderInfo.setDetails(details);
 
        model.addAttribute("orderInfo", orderInfo);
 
        return "order";

    }
    
    @RequestMapping(value= {"/admin/product"}, method=RequestMethod.GET)
    public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
    	ProductForm productForm = null;
    	
    	if(code != null && code.length()>0) {
    		Product product = productDAO.findProduct(code);	
    		if (product != null) {
                productForm = new ProductForm(product);
            }
    	}
    	if(productForm == null) {
    		productForm = new ProductForm();
            productForm.setNewProduct(true);
    	}
    	model.addAttribute("productForm", productForm);
    	return "product";
    }
    
    @RequestMapping(value= {"/admin/product"}, method=RequestMethod.POST)
    public String saveProduct(Model model, @ModelAttribute ProductForm productForm) {
    	 	
             productDAO.save(productForm);
       
    	return "redirect:/productList/1";
    }
      	
}
