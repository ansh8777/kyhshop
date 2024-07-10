package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.service.*;
import com.project.kyhshop.util.*;
import com.project.kyhshop.dao.*;

@Controller
public class PasswordController {
    @Autowired
    EmailService es;

    @Autowired
    RandomString rs;

    @Autowired
    FindDao fd;

    @GetMapping("/find/pw")
    public String findPwPage() {
        return "html/user/findpw";
    }

    @PostMapping("/find/pw/action")
    public String findPwAction(@RequestParam String id,
                               @RequestParam String email,
                               Model model)
    {
        boolean boolUser = fd.findUser(id, email);
        if (!boolUser) {
            model.addAttribute("link", "/find/pw");
            model.addAttribute("msg", "등록된 유저 정보가 없습니다.");
            return "/html/alert";
        }
        String newPw = rs.generateRandomString(10);

        fd.randomPw(id, newPw);
        String to = email;
        String subject = "비밀번호가 변경되었습니다.";
        String text = "새로운 비밀번호: " + newPw;

        es.sendEmail(to, subject, text);
        model.addAttribute("vari", "1");    
        model.addAttribute("link", "/login");
        model.addAttribute("msg", "임시 비밀번호가 발급되었습니다.");

        return "/html/alert";
    }

    @GetMapping("/find/seller/pw")
    public String findSellerPwPage() {
        return "html/seller/findpw";
    }

    @PostMapping("/find/seller/pw/action")
    public String findSellerPwAction(@RequestParam String id,
                                     @RequestParam String email,
                                     Model model)
    {
        boolean boolUser = fd.findSeller(id, email);
        if (!boolUser) {
            model.addAttribute("link", "/find/seller/pw");
            model.addAttribute("msg", "등록된 유저 정보가 없습니다.");
            return "/html/alert";
        }
        String newPw = rs.generateRandomString(10);

        fd.randomPwSeller(id, newPw);
        String to = email;
        String subject = "비밀번호가 변경되었습니다.";
        String text = "새로운 비밀번호: " + newPw;

        es.sendEmail(to, subject, text);
        model.addAttribute("vari", "1");    
        model.addAttribute("link", "/login");
        model.addAttribute("msg", "임시 비밀번호가 발급되었습니다.");

        return "/html/alert";
    }
}
