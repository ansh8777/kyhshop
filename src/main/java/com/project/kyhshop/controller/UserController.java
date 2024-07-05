package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

import java.util.*;
import com.project.kyhshop.dao.*;

@Controller
public class UserController {
    @Autowired
    UserDao ud;
    
    // 홈페이지
    @GetMapping("/")
    public String homePage() {
        return "html/homepage";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "html/login";
    }

    // 로그인 액션
    @PostMapping("/login/action")
    public String loginAction(@RequestParam String id,
                              @RequestParam String pw,
                              HttpSession session)
    {
        // 아이디, 비밀번호가 DB와 일치하면 정보(아이디, 이름)를 loginResult 변수에 가져오기
        List<Map<String, Object>> loginResult = ud.login(id, pw);

        // 로그인 정보가 있으면(아이디, 비밀번호 일치 O)
        if (loginResult.size() == 1) {
            session.setAttribute("id", loginResult.get(0).get("id"));
            session.setAttribute("nm", loginResult.get(0).get("nm"));
            return "redirect:/";
        } else {
            return "redirect:/login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 세션 무효화
        return "redirect:/";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage() {
        return "html/register";
    }

    // 회원가입 액션
    @PostMapping("/register/action")
    public String registerAction(@RequestParam String id,
                                 @RequestParam String pw,
                                 @RequestParam String nm,
                                 @RequestParam String year,
                                 @RequestParam String month,
                                 @RequestParam String day,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String address,
                                 @RequestParam String address_detail,
                                 HttpSession session,
                                 Model model)
    {
        // 월 or 일 은 예로들면 '7' 일 경우에 '07'로 만들어줌
        // 예) 2000년 2월 8일 = 20000208
        String birthDate = year + String.format("%02d", Integer.parseInt(month)) + String.format("%02d", Integer.parseInt(day));
        ud.register(id, pw, nm, birthDate, email, phone, address, address_detail);

        return "redirect:/";
    }

    // 중복 확인
    @GetMapping("/dupcheck")
    @ResponseBody
    public Map<String, Object> dupCheck(@RequestParam String id) {
        int dup = ud.dupCheck(id);
        Map<String, Object> response = new HashMap<>();
        response.put("isDuplicate", dup > 0);

        return response;
    }

    // 아이디 찾기 페이지
    @GetMapping("/findid")
    public String findId() {
        return "html/findid";
    }

    // 아이디 찾기 액션
    @GetMapping("/findid/action")
    @ResponseBody
    public Map<String, Object> findIdAction(@RequestParam String nm,
                                            @RequestParam String birthDate)
    {
        String findId = ud.findid(nm, birthDate);

        Map<String, Object> response = new HashMap<>();
        response.put("id", findId);

        return response;
    }

    // 회원페이지
    @GetMapping("/my")
    public String myPage(HttpSession session) {

        // 로그인 안되어 있으면 로그인페이지로
        if (session.getAttribute("id") == null) {
            return "redirect:/member/login";
        }
        return "html/my";
    }
}
