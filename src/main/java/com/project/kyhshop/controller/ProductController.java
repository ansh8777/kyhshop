package com.project.kyhshop.controller;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {
    @Autowired
    ProductDao pd;

    @Autowired
    SellerDao sd;

    @GetMapping("/product")
    public String productPage(@RequestParam String seq,
                              Model model)
    {
        List<Map<String, Object>> productSet = pd.productSelect_1(seq);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : productSet) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String priceString = product.get("prod_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            long price = Long.parseLong(priceString);
            product.put("prod_price_disp", decimalFormat.format(price));
        }
        model.addAttribute("productSet", productSet);

        return "/html/product/product";
    }

    // 상품 수정 페이지
    @GetMapping("product/edit")
    public String productEditPage(@RequestParam String seq,
                                  HttpSession session,
                                  Model model)
    {
        List<Map<String, Object>> productSet = pd.productSelect_1(seq);
        model.addAttribute("productSet", productSet);

        List<Map<String, Object>> categoryList = sd.categoryList();
        model.addAttribute("categoryList", categoryList);

        return "/html/seller/productedit";
    }

    // 상품 수정 액션
    @PostMapping("product/edit/action")
    public String productrEditAction(@RequestParam String seq,
                                     @RequestParam String category,
                                     @RequestParam String productName,
                                     @RequestParam MultipartFile file,
                                     @RequestParam String productTitle,
                                     @RequestParam String productDescription,
                                     @RequestParam String productAmount,
                                     @RequestParam String productPrice,
                                     @RequestParam String productGrade,
                                     @RequestParam String productVariety,
                                     HttpSession session,
                                     Model model) throws IOException
    {
        String sellerId = (String)session.getAttribute("id");

        String imgName = null;
        // 파일이 업로드되었을 경우에만 처리
        if (file != null && !file.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String imgPath = "C:/kyhshop/src/main/resources/static/images/";
            String ogName = file.getOriginalFilename();
            imgName = uuid + "_" + ogName;
            String uploadImg = imgPath + imgName;

            File img = new File(uploadImg);
            file.transferTo(img);
        } else {
            imgName = "default_image.jpg";
        }

        pd.productEdit(seq, sellerId, category, productName, imgName, productTitle, productDescription, productAmount, productPrice, productGrade, productVariety);

        model.addAttribute("link", "/product?seq=" + seq);
        model.addAttribute("msg", "수정이 완료되었습니다.");
        return "/html/alert";
    }

    // 상품 삭제하기 액션
    @PostMapping("product/delete/action")
    public String productDeleteAction(@RequestParam String seq,
                                      Model model)
    {
        pd.productDelete(seq);

        return "redirect:/";
    }

    // 모든 상품 페이지
    @GetMapping("/allproduct")
    public String allProductPage(Model model) {
        List<Map<String, Object>> productSelectList = pd.productSelect();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : productSelectList) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String priceString = product.get("prod_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            long price = Long.parseLong(priceString);
            product.put("prod_price_disp", decimalFormat.format(price));
        }
        model.addAttribute("productSelectList", productSelectList);

        return "html/product/allproduct";
    }
}
