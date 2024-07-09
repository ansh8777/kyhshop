package com.project.kyhshop.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    @Autowired
    OrderDao orderDao;

    // 장바구니에 상품 추가 액션
    @GetMapping("order/basket/insert/action")
    public String orderBasketInsertAction(  @RequestParam String amount,
                                            @RequestParam String product_id,
                                            HttpSession session) {
        String userId = (String)session.getAttribute("id");
        orderDao.orderBasketChange(amount,userId,product_id);
        return "redirect:/buyer/mypage";
    }

    // 상품 구매 액션
    @GetMapping("order/buy/insert/action")
    public String orderBuyInsertAction(@RequestParam String amount,
                                       @RequestParam String product_id,
                                        HttpSession session) {
        String userId = (String)session.getAttribute("id");
        orderDao.orderBuyChange(amount,userId,product_id);
        return "redirect:/buyer/mypage";
    }
    
}
