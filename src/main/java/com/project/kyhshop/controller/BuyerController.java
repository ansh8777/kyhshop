package com.project.kyhshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.kyhshop.dao.OrderDao;

import jakarta.servlet.http.HttpSession;

@Controller
public class BuyerController {
    @Autowired
    OrderDao orderDao;

    // 구매자 마이페이지로 이동
     @GetMapping("buyer/mypage")
        public String userMypage(HttpSession session,
                                 Model model){
            String userId = (String)session.getAttribute("id");
            List<Map<String,Object>> basketList = orderDao.MypageOrder(userId);
            List<Map<String,Object>> buytList = orderDao.MypageBuy(userId);
            model.addAttribute("basketList", basketList);
            model.addAttribute("buyList", buytList);
            return "html/buyer/mypage";
        }

    // 마이페이지 장바구니->구매완료
    @GetMapping("buyer/change")
    public String changebuyer(HttpSession session) {
        String userId = (String)session.getAttribute("id");
    orderDao.orderBasketChangeAction(userId);
    return "redirect:/buyer/mypage";          
                              }
    
}
