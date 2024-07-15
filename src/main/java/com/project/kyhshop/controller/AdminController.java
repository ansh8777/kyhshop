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

    // 어드민 페이지
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

    // 어드민 유저 관리 페이지
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

    // 어드민 셀러 관리 페이지
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

    // 어드민 유저 삭제 액션
    @PostMapping("admin/user/delete")
    public String deleteUser(@RequestParam String seq) {
        
        ad.deleteUser(seq);
        return "redirect:/admin/userlist";
    }

    // 어드민 셀러 삭제 액션
    @PostMapping("admin/seller/delete")
    public String deleteSeller(@RequestParam String seq) {
        
        ad.deleteSeller(seq);
        return "redirect:/admin/sellerlist";
    }

    // 어드민 유저 수정 액션
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

    // 어드민 셀러 수정 액션
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
