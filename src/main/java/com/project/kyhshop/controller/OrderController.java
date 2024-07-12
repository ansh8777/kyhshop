package com.project.kyhshop.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderDao od;

    @Autowired
    UserDao ud;

    // 구매 페이지
    @GetMapping("order")
    public String orderPage(@RequestParam String seq,
                            @RequestParam int amount,
                            HttpSession session,
                            Model model)
    {
        String id = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (id == null || temp != "user") {
            return "redirect:/login";
        }

        int dbAmount = od.getAmount(seq);
        if (amount <= dbAmount) {
            Map<String, Object> address = od.getMainAddress(id);
            if (address.isEmpty()) {
                model.addAttribute("link", "/my");
                model.addAttribute("msg", "메인 배송지가 설정되지 않았습니다.");
                return "/html/alert";
            }
            model.addAttribute("address", address);

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

    // 결제 액션
    @PostMapping("order/action")
    public String orderAction(@RequestParam String seq,
                              @RequestParam int amount,
                              @RequestParam String address,
                              HttpSession session,
                              Model model)
    {
        String userId = (String)session.getAttribute("id");
        if (userId == null) {
            return "redirect:/login";
        }

        int dbAmount = od.getAmount(seq);
        if (amount > dbAmount) {
            model.addAttribute("link", "/product?seq=" + seq);
            model.addAttribute("msg", "구매하려는 수량이 재고를 초과합니다.");
            return "/html/alert";
        }

        od.orderInsert(userId, seq, amount, address);

        model.addAttribute("link", "/my");
        model.addAttribute("msg", "구매가 완료되었습니다.");
        return "/html/alert";
    }

    // 장바구니 상품 추가
    @PostMapping("cart/insert/action")
    public String cartInsertAction(@RequestParam String seq,
                                   @RequestParam int amount,
                                   HttpSession session,
                                   Model model)
    {
        String userId = (String)session.getAttribute("id");
        if (userId == null) {
            return "redirect:/login";
        }
        int dbAmount = od.getAmount(seq);
        if (amount > dbAmount) {
            model.addAttribute("link", "/product?seq=" + seq);
            model.addAttribute("msg", "구매하려는 수량이 재고를 초과합니다.");
            return "/html/alert";
        }

        Map<String, Object> findProd = od.findCartProduct(userId, seq);
        if (findProd != null) {
            // 기존 물품과 같은 물품이 있으면 수량 업데이트
            int currentAmount = (int)findProd.get("prod_amount");
            int newAmount = currentAmount + amount;
            od.updateCartProduct(userId, seq, newAmount);
        } else {
            // 기존 항목이 없으면 INSERT
            od.cartInsert(userId, seq, amount);
        }
        return "redirect:/gocart";
    }
}
