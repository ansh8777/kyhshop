package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class SellerController {
    @Autowired
    SellerDao sd;
    
    // 상품 관리 페이지
    @GetMapping("/seller/product")
    public String productList(HttpSession session,
                              Model model)
    {
        String id = (String)session.getAttribute("id");
        int ox = sd.sellerOX(id);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 
        List<Map<String, Object>> productSelect = sd.productSelect(id);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : productSelect) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String priceString = product.get("prod_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            long price = Long.parseLong(priceString);
            product.put("prod_price_disp", decimalFormat.format(price));
        }
        model.addAttribute("productSelect", productSelect);

        return "/html/seller/productlist";
    }

    // 상품 등록 페이지
    @GetMapping("/seller/product/insert")
    public String productInsert(HttpSession session,
                                Model model)
    {
        String id = (String)session.getAttribute("id");
        int ox = sd.sellerOX(id);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 
        List<Map<String, Object>> categoryList = sd.categoryList();
        model.addAttribute("categoryList", categoryList);


        return "/html/seller/productinsert";
    }

    // 상품 등록 액션
    @PostMapping("/seller/product/insert/action")
    public String productInsertAction(@RequestParam String productId,
                                      @RequestParam String category,
                                      @RequestParam String productTitle,
                                      @RequestParam String productName,
                                      @RequestParam MultipartFile file,
                                      @RequestParam String productDescription,
                                      @RequestParam String productAmount,
                                      @RequestParam String productPrice,
                                      @RequestParam String productGrade,
                                      @RequestParam String productVariety,
                                      HttpSession session,
                                      Model model) throws IOException
    {
        String id = (String)session.getAttribute("id");
        int ox = sd.sellerOX(id);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 

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

        sd.productInsert(productId, category, productTitle, productDescription, productName, id, imgName, productAmount, productPrice, productGrade, productVariety);
        return "redirect:/seller/product";
    }
}
