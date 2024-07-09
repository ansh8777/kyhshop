package com.project.kyhshop.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.OrderDao;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    @Autowired
    OrderDao orderDao;

     // 장바구니 페이지로 이동
     @GetMapping("order/basket")
     public String orderBasket() {  
         return "html/order/basket";
     }
     
     // 상품 상세 정보 페이지로 이동
    @GetMapping("order/detail")
    public String orderdetail(@RequestParam String productId,
                              @RequestParam String UserId,
                              HttpSession session,
                              Model model) {
     orderDao.orderBasketInsertAction(productId,UserId);
     session.setAttribute("userId", UserId);
     List<Map<String,Object>> basketList = orderDao.orderBasketdetail(productId,UserId);
     model.addAttribute("basketList", basketList);                     
     return "html/buyer/productdetail";
    }


    // 장바구니에 상품 추가 액션
    @GetMapping("order/basket/insert/action")
    public String orderBasketInsertAction(@RequestParam String productId,
                                          @RequestParam String amount,
                                          HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        orderDao.orderBasketChange(amount,productId,userId);
        return "redirect:/buyer/mypage";
    }

    // 상품 구매 액션
    @GetMapping("order/buy/insert/action")
    public String orderBuyInsertAction(@RequestParam String productId,
                                       @RequestParam String amount,
                                        HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        orderDao.orderBuyChange(amount,productId,userId);
        return "redirect:/buyer/mypage";
    }

    // 구매 페이지
    @GetMapping("order/buy")
    public String orderBuyPage(@RequestParam String seq,
                               @RequestParam String amount,
                               HttpSession session)
    {
        String userId = session.getAttribute("userId").toString();
        orderDao.buy(seq, amount);

        return "html/order/buy";
    }
}
