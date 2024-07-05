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
                              HttpSession session,
                              Model model)
    {
        // 아이디, 비밀번호가 DB와 일치하면 정보(아이디, 이름)를 loginResult 변수에 가져오기
        List<Map<String, Object>> loginResult = ud.login(id, pw);
        int del_fg = ud.delFg(id, pw);

        // 로그인 정보가 있으면(아이디, 비밀번호 일치 O, del_fg == 0)
        if (loginResult.size() == 1 && del_fg == 0) {
            session.setAttribute("id", loginResult.get(0).get("id"));
            session.setAttribute("nm", loginResult.get(0).get("nm"));
            session.setAttribute("grade", loginResult.get(0).get("grade"));
            return "redirect:/";
        } else if (loginResult.size() == 1 && del_fg == 1) {
            model.addAttribute("link", "/login/resign");
            model.addAttribute("msg", "탈퇴한 회원입니다. 탈퇴를 해제하시겠습니까?");
            return "/html/alert";
        } else {
            return "redirect:/login";
        }
    }

    // 탈퇴 회원 재가입 페이지
    @GetMapping("/login/resign")
    public String resignPage() {
        return "html/resign";
    }

    // 탈퇴 회원 재가입 액션
    @PostMapping("/login/resign/action")
    public String resignAction(@RequestParam String id,
                               @RequestParam String pw,
                               @RequestParam String nm,
                               Model model)
    {
        int delCheck = ud.delUserCheck(id, pw, nm);
        if (delCheck == 1) {
            ud.resign(id);
            model.addAttribute("link", "/login");
            model.addAttribute("msg", "회원 재가입 완료 되었습니다.");
            return "/html/alert";
        } else {
            model.addAttribute("link", "/login/resign");
            model.addAttribute("msg", "회원정보가 일치하지 않습니다.");
            return "/html/alert";
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
        String formatPhone = phone.replaceAll("-", "");
        ud.register(id, pw, nm, birthDate, email, formatPhone, address, address_detail);

        return "redirect:/login";
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
            return "redirect:/login";
        }
        return "html/my";
    }

    // 비밀번호 확인 페이지 (개인정보 수정을 위해)
    @GetMapping("my/passcheck")
    public String passCheck(HttpSession session) {

        // 로그인 안되어 있으면 로그인페이지로
        if (session.getAttribute("id") == null) {
            return "redirect:/login";
        }

        return "html/passwordcheck";
    }

    // 회원수정 페이지
    @PostMapping("my/profile/verify")
    public String userProfile(@RequestParam String id,
                              @RequestParam String pw,
                              HttpSession session,
                              Model model)
    {
        // 로그인 되어 있으면
        if (session.getAttribute("id") != null) {
            int verifyCnt = ud.verifyPass(id, pw);  // 비밀번호 확인

            if (verifyCnt == 1) {                   // 비밀번호 확인 되면
                String userId = (String)session.getAttribute("id");
                List<Map<String, Object>> userSelect = ud.selectUser(userId);   // 유저 정보 가져오기
                model.addAttribute("userSelect", userSelect);     // 유저 정보 addAttribute

                return "html/userchange";
            } else {                                // 비밀번호 확인 실패 시
                model.addAttribute("link", "/my/passcheck");
                model.addAttribute("msg", "틀린 비밀번호 입니다.");
                return "/html/alert";
            }
        } else {
            return "/html/login";
        }
    }

    // 회원수정하기 액션
    @PostMapping("my/profile/changeaction")
    @ResponseBody
    public String userChangeAction(@RequestParam String pw,
                                   @RequestParam String nm,
                                   @RequestParam String year,
                                   @RequestParam String month,
                                   @RequestParam String day,
                                   @RequestParam String email,
                                   @RequestParam String phone,
                                   @RequestParam String address,
                                   @RequestParam String addressDetail,
                                   HttpSession session,
                                   Model model)
    {
        // 로그인 되어 있으면
        if (session.getAttribute("id") != null) {
            String userId = (String)session.getAttribute("id");

            String birthDate = year + String.format("%02d", Integer.parseInt(month)) + String.format("%02d", Integer.parseInt(day));
            String formatPhone = phone.replaceAll("-", "");     // 전화번호 010-0000-0000 입력받았을 때 01000000000 으로 변환
            ud.userProfileChange(userId, pw, nm, birthDate, email, formatPhone, address, addressDetail);
        } else {
            return "<script>window.close();</script>";
        }
        return "<script>window.close(); alert('회원정보가 수정되었습니다.');</script>";
    }
    
    // 회원삭제하기 액션
    @GetMapping("my/profile/deleteaction")
    @ResponseBody
    public String userDeleteAction(HttpSession session,
                                   Model model)
    {
        // 로그인 되어 있으면
        if (session.getAttribute("id") != null) {
            String userId = (String)session.getAttribute("id");
            ud.userDelete(userId);
            session.invalidate();
        }
        return "<script>window.close(); alert('회원정보가 삭제되었습니다.');</script>";
    }
}
