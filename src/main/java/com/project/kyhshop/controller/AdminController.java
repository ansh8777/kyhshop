package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import com.project.kyhshop.dao.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @Autowired
    AdminDao ad;

    @GetMapping("/admin")
    public String adminPage(HttpSession session) {
        // 로그인 안되어 있으면
        if (session.getAttribute("id") == null) {
            return "redirect:/login";
        } else if ((int)session.getAttribute("grade") != 99) {  // 등급이 99가 아니면
            return "redirect:/";
        }
        return "html/admin/adminpage";
    }

    @GetMapping("admin/userlist")
    public String userList(HttpSession session,
                           Model model)
    {
        // 로그인 안되어 있으면
        if (session.getAttribute("id") == null) {
            return "redirect:/login";
        } else if ((int)session.getAttribute("grade") != 99) {  // 등급이 99가 아니면
            return "redirect:/";
        }
        List<Map<String, Object>> userList = ad.selectUser();
        model.addAttribute("userList", userList);
        
        return "html/admin/userlist";
    }

    @GetMapping("admin/sellerlist")
    public String sellerList(HttpSession session,
                           Model model)
    {
        // 로그인 안되어 있으면
        if (session.getAttribute("id") == null) {
            return "redirect:/login";
        } else if ((int)session.getAttribute("grade") != 99) {  // 등급이 99가 아니면
            return "redirect:/";
        }
        List<Map<String, Object>> sellerList = ad.selectSeller();
        model.addAttribute("sellerList", sellerList);
        
        return "html/admin/sellerlist";
    }

    @GetMapping("/admin/product")
    public String mangeProduct(@RequestParam(defaultValue = "1") String order, Model model) {
        // order 파라미터에 따라 정렬 기준 설정
        String orderString = "";
        if (order.equals("1")) {
            orderString = "productCategory";
        } else if (order.equals("2")) {
            orderString = "regDt";
        } else {
            orderString = "sellerId";
        }
        // 설정된 정렬 기준에 따라 상품 리스트 조회
        List<Map<String, Object>> productList = ad.selectProductList(orderString);
        // 조회된 상품 리스트를 모델에 추가
        model.addAttribute("productList", productList);
        // admin/product.html 페이지로 이동
        return "html/admin/product";
    }

    // @PostMapping("/admin/product/delete")
    // public String deleteProduct(@RequestParam String prodId, HttpSession session) {
    //     // 로그인 안되어 있으면
    //     if (session.getAttribute("id") == null) {
    //         return "redirect:/login";
    //     } 
    //     else if ((int)session.getAttribute("grade") != 99) {  // 등급이 99가 아니면
    //         return "redirect:/";
    //     }

    //     ad.deleteProduct(prodId);
    //     return "redirect:/admin/product";
    // }

    @PostMapping("admin/user/delete")
    public String deleteUser(@RequestParam String seq) {
        
        ad.deleteUser(seq);
        return "redirect:/admin/userlist";
    }

    @PostMapping("admin/seller/delete")
    public String deleteSeller(@RequestParam String seq) {
        
        ad.deleteSeller(seq);
        return "redirect:/admin/sellerlist";
    }

    @PostMapping("admin/userlist/update")
    public String updateUser(@RequestParam String seq,
                             @RequestParam String id,
                             @RequestParam String pw,
                             @RequestParam String nm,
                             @RequestParam String birthDate,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String address,
                             @RequestParam String addressDetail,
                             @RequestParam String grade,
                             @RequestParam String delFg
                             ) {
        
        ad.userUpdate(seq,id,pw,nm,birthDate,email,phone,address,addressDetail,grade,delFg);
        return "redirect:/admin/userlist";
    }

    @PostMapping("admin/sellerlist/update")
    public String updateUser(@RequestParam String seq,
                             @RequestParam String id,
                             @RequestParam String pw,
                             @RequestParam String nm,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String compNm,
                             @RequestParam String bizId,
                             @RequestParam String address,
                             @RequestParam String addressDetail,
                             @RequestParam String grade,
                             @RequestParam String delFg
                             ) {
        
        ad.sellerUpdate(seq,id,pw,nm,email,phone,compNm,bizId,address,addressDetail,grade,delFg);
        return "redirect:/admin/sellerlist";
    }
}
