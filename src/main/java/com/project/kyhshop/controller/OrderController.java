package com.project.kyhshop.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderDao od;

    // 구매 페이지
    @GetMapping("order")
    public String orderPage(@RequestParam String seq,
                            @RequestParam int amount,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("id") == null) {
            return "redirect:/product?seq=" + seq;
        }
        int dbAmount = od.getAmount(seq);
        String id = (String) session.getAttribute("id");

        if (amount <= dbAmount) {
            Map<String, Object> address = od.getMainAddress(id);
            if (address.isEmpty()) {
                model.addAttribute("msg", "메인 배송지가 설정되지 않았습니다.");
                return "/html/alert";
            }
            List<Map<String, Object>> orderList = od.orderSelect(seq, amount);
            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            for (Map<String, Object> order : orderList) {
                // prod_price 값을 가져와서 문자열로 변환합니다.
                String priceString1 = order.get("prod_price").toString();
                String priceString2 = order.get("total_price").toString();

                // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
                BigDecimal price1 = new BigDecimal(priceString1);
                BigDecimal price2 = new BigDecimal(priceString2);
                order.put("prod_price_disp", decimalFormat.format(price1));
                order.put("total_price_disp", decimalFormat.format(price2));
            }

            model.addAttribute("address", address);

            // 총 결제금액 계산
            BigDecimal totalPaymentAmount = orderList.stream()
                    .map(order -> new BigDecimal(order.get("total_price").toString()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("orderList", orderList);
            model.addAttribute("totalPaymentAmount", decimalFormat.format(totalPaymentAmount));

            return "html/order/order";
        } else {
            model.addAttribute("link", "/product?seq=" + seq);
            model.addAttribute("msg", "구매하려는 수량이 재고를 초과합니다.");
            return "/html/alert";
        }
    }

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
