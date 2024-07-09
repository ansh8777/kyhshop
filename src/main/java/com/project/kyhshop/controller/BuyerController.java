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
            String id = session.getAttribute("userId").toString();
            List<Map<String,Object>> basketList = orderDao.MypageOrder(id);
            List<Map<String,Object>> buytList = orderDao.MypageBuy(id);
            model.addAttribute("basketList", basketList);
            model.addAttribute("buyList", buytList);
            return "html/buyer/mypage";
        }

    // 마이페이지 장바구니->구매완료
    @GetMapping("buyer/change")
    public String changebuyer(HttpSession session) {
    String id = session.getAttribute("userId").toString();
    orderDao.orderBasketChangeAction(id);
    return "redirect:/buyer/mypage";          
                              }
    
}
