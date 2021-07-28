package com.stackjava.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stackjava.dao.OrderDAO;
import com.stackjava.dao.ProductDAO;
import com.stackjava.entity.Product;
import com.stackjava.form.CustomerForm;
import com.stackjava.model.CartInfo;
import com.stackjava.model.CustomerInfo;
import com.stackjava.model.Nguoi;
import com.stackjava.model.ProductInfo;
import com.stackjava.utils.Util;


@Controller
@Transactional
public class MainController {
	@Autowired
    private OrderDAO orderDAO;
 
     @Autowired
     private ProductDAO productDAO;
    
    @RequestMapping("/403")
    public String accessDenied() {
        return "/403";
    }
 
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "index";
    }
 
    @RequestMapping("/productList/{pageNumber}")
    public String listProductHandler(Model model,@PathVariable("pageNumber") int currentPage, HttpServletResponse response) {
    	Page<Product> page = productDAO.listAll(currentPage);
		List<Product> listProduct = page.getContent();
		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("listProduct", listProduct);
		
		Cookie u = new Cookie("userC", "123");
	    Cookie p = new Cookie("passC", "456");
	    u.setMaxAge(60);
	    p.setMaxAge(60);
	    response.addCookie(u);
	    response.addCookie(p);
	    return "productList";
    }
    
    @RequestMapping("/buyProduct")
    public String buyProduct(HttpServletRequest request,HttpSession session, Model model, //
            @RequestParam(value = "code", defaultValue = "") String code){
    	HttpSession session1  = request.getSession();
    	System.out.println("session1 có giá trị là: "+ session1);
    	System.out.println("session có giá trị là: "+ session);
    	Nguoi p = (Nguoi) session1.getAttribute("p");
    	
		if(p == null) {
			p = new Nguoi();
			request.getSession().setAttribute("p", p);
			
		}
		p.setValue(p.getValue() + 1);
		session1.removeAttribute("p");
		System.out.println("p1 có giá trị là: "+ p.getValue());

    	Product product = null;
    	if(code != null && code.length()>0) {
    		product = productDAO.findProduct(code);
    	}
    	if(product != null) {
    		CartInfo cartInfo = Util.getCartInSession(request);
    		System.out.println("Buy product "+ cartInfo.getCartLines().size());
    		ProductInfo productInfo = new ProductInfo(product);
    		cartInfo.addProduct(productInfo, 1);
    		
    	}
    	return "redirect:/shoppingCart";
    }
    
    @RequestMapping("/shoppingCart")
    public String shoppingCartHandler(HttpServletRequest request, Model model) {
    	HttpSession session2  = request.getSession();
    	System.out.println("session2 có giá trị là: "+ session2);
    	Nguoi p = (Nguoi) session2.getAttribute("p");
    	System.out.println("p bang : "+ p);
		if(p == null) {
			p = new Nguoi();
			request.getSession().setAttribute("p", p);
			
			
		}
		
		p.setValue(p.getValue() + 1);
		session2.removeAttribute("p");
		System.out.println("p2 có giá trị là: "+ p.getValue());
		
    	CartInfo cartInf = Util.getCartInSession(request);
    	System.out.println("Shopping cart "+ cartInf.getCartLines().size());
    	model.addAttribute("cartForm", cartInf);
    	return "shoppingCart";
    }
    
    @RequestMapping(value= {"/shoppingCart"}, method = RequestMethod.POST)
    public String shoppingCartUpdateQty(HttpServletRequest request,
    		Model model, @ModelAttribute("cartForm") CartInfo cartForm) {
    	CartInfo cartInfo = Util.getCartInSession(request);
        cartInfo.updateQuantity(cartForm);
    	
    	return "redirect:/shoppingCart";
    }
    
    @RequestMapping({ "/shoppingCartRemoveProduct" })
    public String removeProductHandler(HttpServletRequest request, Model model, //
            @RequestParam(value = "code", defaultValue = "") String code) {
    	Product product = null;
        if (code != null && code.length() > 0) {
            product = productDAO.findProduct(code);
        }
        if (product != null) {
        	CartInfo cartInfo = Util.getCartInSession(request);
        	ProductInfo productInfo = new ProductInfo(product);
        	cartInfo.removeProduct(productInfo);
        }
    	return "forward:/shoppingCart";
    }
    
    @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.GET)
    public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
    	
    	CartInfo cartInfo = Util.getCartInSession(request);
    	if (cartInfo.isEmpty()) {	 
            return "redirect:/shoppingCart";
        }
    	CustomerInfo customerInfo = new CustomerInfo();
    	CustomerForm customerForm = new CustomerForm(customerInfo);
    	model.addAttribute("customerForm", customerForm);
    	 
        return "shoppingCartCustomer";
    }
    
    @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.POST)
    public String shoppingCartCustomerSave(HttpServletRequest request, //
            Model model, //
            @ModelAttribute("customerForm")  CustomerForm customerForm ) {
    	customerForm.setValid(true);
        CartInfo cartInfo = Util.getCartInSession(request);
        CustomerInfo customerInfo = new CustomerInfo(customerForm);
        cartInfo.setCustomerInfo(customerInfo);
 
        return "redirect:/shoppingCartConfirmation";
    }
    
    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
    public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
    	CartInfo cartInfo = Util.getCartInSession(request);
    	if(cartInfo == null || cartInfo.isEmpty()) {
    		return "redirect:/shoppingCart";
    	}else if (!cartInfo.isValidCustomer()) {
 
            return "redirect:/shoppingCartCustomer";
        }
        model.addAttribute("myCart", cartInfo);
 
        return "shoppingCartConfirmation";
    }
    
    
    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
    public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
    	 CartInfo cartInfo = Util.getCartInSession(request);
    	 if(cartInfo.isEmpty()) {
    		 return "redirect:/shoppingCart";
         } else if (!cartInfo.isValidCustomer()) {
  
             return "redirect:/shoppingCartCustomer";
         }
         try {
             orderDAO.saveOrder(cartInfo);
         } catch (Exception e) {
  
             return "shoppingCartConfirmation";
         }
         
         Util.removeCartInSession(request);
         Util.storeLastOrderedCartInSession(request, cartInfo);
         
         return "redirect:/shoppingCartFinalize";
    }
    
    @RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
    public String shoppingCartFinalize(HttpServletRequest request, Model model) {
    	
    	  CartInfo lastOrderedCart = Util.getLastOrderedCartInSession(request);
    	  
          if (lastOrderedCart == null) {
              return "redirect:/shoppingCart";
          }
          model.addAttribute("lastOrderedCart", lastOrderedCart);
    	return "shoppingCartFinalize";
    }
}

