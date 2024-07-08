package com.project.kyhshop.controller;
import java.text.DecimalFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.*;

@Controller
public class ProductController {
    @Autowired
    ProductDao pd;

    @GetMapping("/product")
    public String productPage(@RequestParam String seq,
                              Model model)
    {
        List<Map<String, Object>> productSet = pd.productSelect_1(seq);
        // 가격 포맷
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : productSet) {
            double price = Double.parseDouble(product.get("prod_price").toString());

            // 소수점이 0인지 확인합니다.
            if (price % 1 == 0) {
                // 소수점이 0이면 정수로 변환하고 포맷팅합니다.
                product.put("prod_price_disp", decimalFormat.format((int) price));
            } else {
                // 소수점이 0이 아니면 그대로 유지합니다.
                product.put("prod_price_disp", String.format("%,.2f", price));
            }
        }
        model.addAttribute("productSet", productSet);

        return "/html/product/product";
    }
}
