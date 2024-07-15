package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.project.kyhshop.dao.*;

@Controller
public class UserController {
    @Autowired
    UserDao ud;

    @Autowired
    OrderDao od;
    
    // 홈페이지
    @GetMapping("/")
    public String homePage(Model model,
                           HttpSession session) {
        List<Map<String, Object>> prodSel = ud.selectProduct_9();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> product : prodSel) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String priceString = product.get("prod_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            long price = Long.parseLong(priceString);
            product.put("prod_price_disp", decimalFormat.format(price));
        }
        model.addAttribute("prodSel", prodSel);
        return "html/common/homepage2";
    }

    // 로그인 페이지 (유저, 셀러 공통)
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // 로그인 되어 있으면 홈페이지로
        if (session.getAttribute("id") != null) {
            return "redirect:/";
        }
        
        return "html/common/login";
    }

    // 로그인 액션
    @PostMapping("/login/action")
    public String loginAction(@RequestParam String id,
                              @RequestParam String pw,
                              HttpSession session,
                              Model model)
    {
        // 아이디, 비밀번호가 DB와 일치하면 정보(아이디, 이름)를 loginResult 변수에 가져오기
        Map<String, Object> loginResult = ud.login(id, pw);

        // 로그인 정보가 있으면(아이디, 비밀번호 일치 O, del_fg == 0)
        if (loginResult != null && (int)loginResult.get("del_fg") == 0) {
            session.setAttribute("id", loginResult.get("id"));
            session.setAttribute("nm", loginResult.get("nm"));
            session.setAttribute("grade", loginResult.get("grade"));
            session.setAttribute("temp", "user");
            return "redirect:/";
        } else if (loginResult != null && (int)loginResult.get("del_fg") == 1) {
            model.addAttribute("link", "/login/resign");
            model.addAttribute("msg", "탈퇴한 회원입니다. 탈퇴를 해제하시겠습니까?");
            return "/html/alert";
        } else {
            model.addAttribute("link", "/login");
            model.addAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "/html/alert";
        }
    }

    // 탈퇴 회원 재가입 페이지
    @GetMapping("/login/resign")
    public String resignPage() {
        return "html/user/resign";
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

    // 회원가입 고르기 (판매자, 구매자)
    @GetMapping("/regiselect")
    public String registerSelectPage() {
        return "html/common/regiselect";
    }

    // 유저 회원가입 페이지
    @GetMapping("/register/user")
    public String userRegisterPage() {
        return "html/user/register";
    }

    // 유저 회원가입 액션
    @PostMapping("/register/user/action")
    public String userRegisterAction(@RequestParam String id,
                                     @RequestParam String pw,
                                     @RequestParam String nm,
                                     @RequestParam String year,
                                     @RequestParam String month,
                                     @RequestParam String day,
                                     @RequestParam String email,
                                     @RequestParam String phone_1,
                                     @RequestParam String phone_2,
                                     @RequestParam String phone_3,
                                     @RequestParam String address,
                                     @RequestParam String address_detail,
                                     HttpSession session,
                                     Model model)
    {
        int dup = ud.dupCheck(id);
        if (dup == 1) {
            model.addAttribute("link", "/login");
            model.addAttribute("msg", "아이디 중복 오류!!");
            return "/html/alert";
        }
        // 월 or 일 은 예로들면 '7' 일 경우에 '07'로 만들어줌
        // 예) 2000년 2월 8일 = 20000208
        String birthDate = year + String.format("%02d", Integer.parseInt(month)) + String.format("%02d", Integer.parseInt(day));

        // 폰 번호 합치기 010-0000-0000
        String phone = String.format("%s-%s-%s", phone_1, phone_2, phone_3);
        ud.userRegister(id, pw, nm, birthDate, email, phone, address, address_detail);

        model.addAttribute("link", "/login");
        model.addAttribute("msg", "회원가입이 완료되었습니다.");
        return "/html/alert";
    }

    // 유저 중복 확인
    @GetMapping("/dupcheck")
    @ResponseBody
    public Map<String, Object> dupCheck(@RequestParam String id) {
        int dup = ud.dupCheck(id);
        Map<String, Object> response = new HashMap<>();
        response.put("isDuplicate", dup > 0);

        return response;
    }

    // 이메일 중복 확인
    @GetMapping("/dupcheck/email")
    @ResponseBody
    public Map<String, Object> dupEmailCheck(@RequestParam String email) {
        int dup = ud.dupEmailCheck(email);
        Map<String, Object> response = new HashMap<>();
        response.put("isDupEmail", dup > 0);

        return response;
    }

    // 아이디 찾기 페이지
    @GetMapping("/find/id")
    public String findId() {
        return "html/user/findid";
    }

    // 아이디 찾기 액션
    @GetMapping("/find/id/action")
    @ResponseBody
    public Map<String, Object> findIdAction(@RequestParam String nm,
                                            @RequestParam String birthDate)
    {
        String findId = ud.findid(nm, birthDate);

        Map<String, Object> response = new HashMap<>();
        response.put("id", findId);

        return response;
    }

    // 회원페이지 (유저, 셀러 공통)
    @GetMapping("/my")
    public String myPage(HttpSession session,
                         Model model)
    {

        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");

        // 로그인 안되어 있으면 로그인페이지로
        if (userId == null) {
            return "redirect:/login";
        }
        if (temp == "user") {
            List<Map<String, Object>> orderList = ud.orderSelect(userId);
            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            for (Map<String, Object> order : orderList) {
                // prod_price 값을 가져와서 문자열로 변환합니다.
                String priceString = order.get("price").toString();

                // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
                long price = Long.parseLong(priceString);
                order.put("price_disp", decimalFormat.format(price));

                String prodId = order.get("prod_id").toString();
                int cnt = ud.getReviewCnt(userId, prodId);
                order.put("cnt", cnt);
            }
            model.addAttribute("orderList", orderList);
        }
        return "html/common/my";
    }

    // 비밀번호 확인 페이지 (개인정보 수정을 위해)
    @GetMapping("my/passcheck")
    public String passCheck(HttpSession session) {

        // 로그인 안되어 있으면 로그인페이지로
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }

        return "html/user/passwordcheck";
    }

    // 회원수정 페이지
    @PostMapping("my/profile/verify")
    public String userProfile(@RequestParam String id,
                              @RequestParam String pw,
                              HttpSession session,
                              Model model)
    {
        // 로그인 되어 있으면
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId != null || temp == "user") {
            int verifyCnt = ud.verifyPass(id, pw);  // 비밀번호 확인

            if (verifyCnt == 1) {                   // 비밀번호 확인 되면
                Map<String, Object> userSelect = ud.selectUser(userId);         // 유저 정보 가져오기

                model.addAttribute("userSelect", userSelect);     // 유저 정보 addAttribute

                return "html/user/userchange";
            } else {                                // 비밀번호 확인 실패 시
                model.addAttribute("link", "/my/passcheck");
                model.addAttribute("msg", "틀린 비밀번호 입니다.");
                return "/html/alert";
            }
        } else {
            return "/html/common/login";
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
                                   @RequestParam String phone1,
                                   @RequestParam String phone2,
                                   @RequestParam String phone3,
                                   @RequestParam String address,
                                   @RequestParam String addressDetail,
                                   HttpSession session,
                                   Model model)
    {
        // 로그인 되어 있으면
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId != null || temp == "user") {

            String birthDate = year + String.format("%02d", Integer.parseInt(month)) + String.format("%02d", Integer.parseInt(day));

            // 폰 번호 합치기 010-0000-0000
            String phone = String.format("%s-%s-%s", phone1, phone2, phone3);

            // 셀러 전 정보 중복확인
            int userSelect = ud.userSelect(userId, pw, birthDate, email, phone, address, addressDetail);

            if (userSelect == 1) {
                return "<script>window.history.back(); alert('변경된 정보가 없습니다.');</script>";
            }
            ud.userProfileChange(userId, pw, nm, birthDate, email, phone, address, addressDetail);
        } else {
            return "<script>window.close();</script>";
        }
        return "<script>window.close(); alert('회원정보가 수정되었습니다.');</script>";
    }
    
    // 회원삭제하기 액션
    @PostMapping("my/profile/deleteaction")
    @ResponseBody
    public String userDeleteAction(HttpSession session,
                                   Model model)
    {
        // 로그인 되어 있으면 유저 삭제 및 세션 종료
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId != null || temp == "user") {
            ud.userDelete(userId);
            session.invalidate();
        }
        return "<script>window.opener.location.reload(); window.close(); alert('회원정보가 삭제되었습니다.');</script>";
    }

    // 주소록 관리 페이지
    @GetMapping("my/address")
    public String addressPage(HttpSession session,
                              Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        List<Map<String, Object>> addressList = ud.selectAddress(userId);
        model.addAttribute("addressList", addressList);

        return "html/user/address";
    }

    // 주소록 입력 액션
    @PostMapping("my/address/insert/action")
    public String addressInsertAction(@RequestParam String name,
                                      @RequestParam String phone_1,
                                      @RequestParam String phone_2,
                                      @RequestParam String phone_3,
                                      @RequestParam String address,
                                      @RequestParam String address_detail,
                                      HttpSession session,
                                      Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        // 폰 번호 합치기 010-0000-0000
        String phone = String.format("%s-%s-%s", phone_1, phone_2, phone_3);
        
        ud.insertAddress(userId, name, phone, address, address_detail);

        return "redirect:/my/address";
    }

    // 주소록 삭제 액션
    @PostMapping("my/address/delete/action")
    public String addressDeleteAction(@RequestParam String seq,
                                      HttpSession session)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        ud.deleteAddress(seq);

        return "redirect:/my/address";
    }

    // 대표 주소 액션
    @PostMapping("my/address/main/action")
    public String addressMainAction(@RequestParam String seq,
                                    HttpSession session,
                                    Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        ud.mainAddress(seq, userId);

        model.addAttribute("link", "/my/address");
        model.addAttribute("msg", "수정이 완료되었습니다.");
        return "/html/alert";
    }

    // 장바구니 넣을 때 실행되는 팝업
    @GetMapping("gocart")
    public String goCartBefore() {
        return "/html/user/gocart";
    }

    // 장바구니
    @GetMapping("cart")
    public String cart(HttpSession session,
                       Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        List<Map<String, Object>> cartList = ud.cartSelect(userId);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

            for (Map<String, Object> cart : cartList) {
                // prod_price 값을 가져와서 문자열로 변환합니다.
                String priceString = cart.get("prod_price").toString();

                // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
                BigDecimal price = new BigDecimal(priceString);
                cart.put("prod_price_disp", decimalFormat.format(price));
            }
        model.addAttribute("cartList", cartList);

        return "html/user/cart";
    }

    // 장바구니 삭제
    @PostMapping("cart/delete")
    public String cartDelete(@RequestParam String prodId,
                             HttpSession session,
                             Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }
        ud.cartDelete(userId, prodId);
        return "redirect:/cart";
    }

    // 장바구니 여러개 삭제
    @PostMapping("cart/deleteselect")
    public String cartDeleteSelect(@RequestParam("prodIds") String ids,
                                   HttpSession session,
                                   Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || temp != "user") {
            return "redirect:/login";
        }

        List<Long> prodIds = Arrays.stream(ids.split(","))
                               .map(Long::valueOf)
                               .collect(Collectors.toList());
        ud.cartDeleteAll(userId, prodIds);
        return "redirect:/cart";
    }

    // 장바구니에서 전체상품 구매 페이지
    @GetMapping("cart/order")
    public String cartOrderPage(
                                HttpSession session,
                                Model model) {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        Map<String, Object> address = od.getMainAddress(userId);
        if (address.isEmpty()) {
            model.addAttribute("link", "/my");
            model.addAttribute("msg", "메인 배송지가 설정되지 않았습니다.");
            return "/html/alert";
        }
        model.addAttribute("address", address);

        List<Map<String, Object>> orderList = ud.cartSelect2(userId);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> cart : orderList) {
            // prod_price 값을 가져와서 문자열로 변환합니다.
            String prod_price = cart.get("prod_price").toString();
            String total_price = cart.get("total_price").toString();

            // 문자열 형태의 가격을 정수로 변환하여 포맷팅합니다.
            BigDecimal price1 = new BigDecimal(prod_price);
            BigDecimal price2 = new BigDecimal(total_price);
            cart.put("prod_price_disp", decimalFormat.format(price1));
            cart.put("total_price_disp", decimalFormat.format(price2));
        }
        // 총 결제금액 계산
        BigDecimal totalPaymentAmount = orderList.stream()
        .map(order -> new BigDecimal(order.get("total_price").toString()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);


        model.addAttribute("orderList", orderList);
        model.addAttribute("totalPaymentAmount", decimalFormat.format(totalPaymentAmount));

        return "html/order/cart_order";
    }

    // 장바구니 결제 액션
    @PostMapping("/cart/order/action")
    public String cartOrderAction(@RequestParam String addressId,
                                  HttpSession session,
                                  Model model) {

        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        List<Map<String, Object>> cartItems = ud.getCartItems(userId);

        for (Map<String, Object> item : cartItems) {
            String prodId = item.get("prod_id").toString();
            int cartAmount = Integer.parseInt(item.get("prod_amount").toString());
            int availableAmount = ud.getProductAmount(prodId);

            if (cartAmount > availableAmount) {
                model.addAttribute("link", "/my");
                model.addAttribute("msg", "상품의 재고가 부족합니다.");
                return "/html/alert";
            }
            item.put("address_id", addressId);
            ud.cartToOrder(item);
        }
        
        model.addAttribute("link", "/my");
        model.addAttribute("msg", "구매가 완료되었습니다.");
        return "/html/alert";
    }

    // 장바구니에서 수량 변경
    @PostMapping("cart/change/amount")
    public String changeCartAmount(@RequestParam String seq,
                                   @RequestParam int amount,
                                   HttpSession session,
                                   Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        ud.changeCartAmount(seq, amount, userId);

        return "redirect:/cart";
    }

    // 운송장 팝업
    @GetMapping("my/delivery")
    public String myDeliveryPage(@RequestParam String seq,
                                 HttpSession session,
                                 Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        Map<String, Object> orderList = ud.deliSelect(seq);

        model.addAttribute("orderList", orderList);

        return "/html/user/mydelivery";
    }

    // 수취확인
    @PostMapping("my/delivery/complete")
    @ResponseBody
    public String deliveryComplete(@RequestParam String seq,
                                   HttpSession session,
                                   Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }
        ud.deliComplete(seq);
        
        return "<script>window.opener.location.reload(); window.close();</script>";
    }

    // 리뷰작성하기
    @GetMapping("review")
    public String reviewPage(@RequestParam String seq,
                             HttpSession session,
                             Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        int cnt = ud.getReviewCnt(userId, seq);
        model.addAttribute("cnt", cnt);
        model.addAttribute("seq", seq);
        if (cnt >= 1) {
            model.addAttribute("link", "/my");
            model.addAttribute("msg", "리뷰를 이미 작성 하셨습니다.");
            return "/html/alert";
        }

        return "/html/user/review";
    }

    // 리뷰 작성 액션
    @PostMapping("review/action")
    public String reviewAction(@RequestParam("seq") String seq,
                               @RequestParam String review,
                               @RequestParam double score,
                               @RequestParam MultipartFile file,
                               HttpSession session,
                               Model model) throws IOException
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        int cnt = ud.getReviewCnt(userId, seq);
        model.addAttribute("cnt", cnt);
        if (cnt >= 1) {
            model.addAttribute("link", "/my");
            model.addAttribute("msg", "리뷰를 이미 작성 하셨습니다.");
            return "/html/alert";
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

        ud.reviewInsert(userId, seq, imgName, review, score);

        model.addAttribute("link", "/my");
        model.addAttribute("msg", "리뷰를 등록하였습니다.");
        return "/html/alert";
    }

    // 리뷰 수정 페이지
    @GetMapping("review/edit")
    public String reviewEditPage(@RequestParam String seq,
                                 HttpSession session,
                                 Model model)
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "redirect:/login";
        }

        Map<String, Object> reviews = ud.getReview(seq);
        model.addAttribute("reviews", reviews);

        return "/html/user/reviewedit";
    }

    // 리뷰 수정 액션
    @PostMapping("review/edit/action")
    @ResponseBody
    public String reviewEditAction(@RequestParam String seq,
                                   @RequestParam MultipartFile file,
                                   @RequestParam String review,
                                   @RequestParam double score,
                                   HttpSession session,
                                   Model model) throws IOException
    {
        String userId = (String)session.getAttribute("id");
        String temp = (String)session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "<script>alert('로그인이 필요합니다.'); window.location.href='/login';</script>";
        }

        // 기존 이미지 가져오기
        String originImg = ud.getOriginImg(seq);

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

        ud.reviewEdit(userId, seq, imgName, review, score);

        return "<script>alert('리뷰가 수정되었습니다.'); window.opener.location.reload(); window.close();</script>";
    }

    // 리뷰 삭제 액션
    @PostMapping("review/delete/action")
    @ResponseBody
    public String reviewDeleteAction(@RequestParam String seq, HttpSession session) {
        String userId = (String) session.getAttribute("id");
        String temp = (String) session.getAttribute("temp");
        if (userId == null || !temp.equals("user")) {
            return "<script>alert('로그인이 필요합니다.'); window.location.href='/login';</script>";
        }
        String page = ud.getPage(seq);

        ud.deleteReview(seq);
        return "<script>alert('리뷰가 삭제되었습니다.'); window.location.href='/product?seq=" + page + "';</script>";
    }
}
