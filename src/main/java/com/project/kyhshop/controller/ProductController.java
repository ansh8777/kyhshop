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

    // 상품 페이지
    @GetMapping("/product")
    public String productPage(@RequestParam String seq,
                              HttpSession session,
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

        List<Map<String, Object>> gradevariety = pd.gradevariety(seq);

        //  등급과 품종 정보가 있는 경우에만 lowerPrice를 호출하여 가격이 가장 낮은 상품을 조회합니다.
        List<Map<String, Object>> lowerPrice = new ArrayList<>();
        if (!gradevariety.isEmpty()) {
            String grade = (String) gradevariety.get(0).get("grade");
            String variety = (String) gradevariety.get(0).get("variety");
            lowerPrice = pd.lowerPrice(grade, variety);
        }

        model.addAttribute("productSet", productSet);
        model.addAttribute("lowerPrice", lowerPrice);

        return "/html/product/product";
    }

    // 상품 수정 페이지
    @GetMapping("product/edit")
    public String productEditPage(@RequestParam String seq,
                                  HttpSession session,
                                  Model model)
    {
        String sellerId = (String)session.getAttribute("id");
        int ox = sd.sellerOX(sellerId);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 

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
        int ox = sd.sellerOX(sellerId);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 

        // 기존 이미지 가져오기
        String originImg = pd.getOriginImg(seq);

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
            imgName = originImg;
        }

        pd.productEdit(seq, sellerId, category, productName, imgName, productTitle, productDescription, productAmount, productPrice, productGrade, productVariety);

        model.addAttribute("link", "/product?seq=" + seq);
        model.addAttribute("msg", "수정이 완료되었습니다.");
        return "/html/alert";
    }

    // 상품 삭제하기 액션
    @PostMapping("product/delete/action")
    public String productDeleteAction(@RequestParam String seq,
                                      HttpSession session,
                                      Model model)
    {
        String sellerId = (String)session.getAttribute("id");
        int ox = sd.sellerOX(sellerId);

        // 셀러가 아니면 로그인 페이지로
        if (ox == 0) {
            return "redirect:/login";
        } 

        pd.productDelete(seq);
        return "redirect:/";
    }

    // 모든 상품 페이지
    @GetMapping("/allproduct")
    public String allProductPage(@RequestParam(defaultValue = "1") int page,
                                 Model model)
    {
        int pageSize = 10;
        int pageIndex = page - 1;

        
        List<Map<String, Object>> productSelectList = pd.productSelect();

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) productSelectList.size() / pageSize);

        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, productSelectList.size());
        List<Map<String, Object>> paginatedList = productSelectList.subList(start, end);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : productSelectList) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String priceString = product.get("prod_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            long price = Long.parseLong(priceString);
            product.put("prod_price_disp", decimalFormat.format(price));
        }
        model.addAttribute("productSelectList", paginatedList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "html/product/allproduct";
    }
}
