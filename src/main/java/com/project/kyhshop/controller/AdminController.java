package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
