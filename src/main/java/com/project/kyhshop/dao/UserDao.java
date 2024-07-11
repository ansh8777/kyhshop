package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDao {
    @Autowired
    JdbcTemplate jt;

    // 유저 회원가입
    public void userRegister(String id,
                             String pw,
                             String nm,
                             String birthDate,
                             String email,
                             String phone,
                             String address,
                             String address_detail)
    {
        String sqlStmt = "INSERT INTO tb_user_mst(id, pw, nm, birth_date, email, phone, address, address_detail) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        jt.update(sqlStmt, id, pw, nm, birthDate, email, phone, address, address_detail);
    }

    // 유저 아이디 중복 체크
    public int dupCheck(String id) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id);
    }

    // 유저 이메일 중복 체크
    public int dupEmailCheck(String email) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE email = ?";
        return jt.queryForObject(sqlStmt, Integer.class, email);
    }

    // 로그인
    public Map<String, Object> login(String id, String pw) {
        String sqlStmt = "SELECT id, nm, grade, del_fg FROM tb_user_mst WHERE id = ? AND pw = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForMap(sqlStmt, id, pw);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 삭제 유무(del_fg) 값 가져오기
    public int delFg(String id, String pw) {
        String sqlStmt = "SELECT del_fg FROM tb_user_mst WHERE id = ? AND pw = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw);
    }

    // 탈퇴 회원 정보 체크하기
    public int delUserCheck(String id, String pw, String nm) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ? AND pw = ? AND nm = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw, nm);
    }

    // 탈퇴 회원 재가입
    public void resign(String id) {
        String sqlStmt = "UPDATE tb_user_mst SET del_fg = 0 WHERE id = ?";
        jt.update(sqlStmt, id);
    }

    // 아이디 찾기
    public String findid(String nm, String birthDate) {
        String sqlStmt = "SELECT id FROM tb_user_mst WHERE nm = ? AND birth_date = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, String.class, nm, birthDate);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 비밀번호 확인 (정보 변경을 위해서)
    public int verifyPass(String id, String pw) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ? AND pw = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, Integer.class, id, pw);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 유저 정보 가져오기
    public Map<String, Object> selectUser(String id) {
        String sqlStmt = "SELECT seq, id, pw, nm, birth_date, email, phone, address, address_detail, grade " +
                         "FROM tb_user_mst " +
                         "WHERE id = ?";
        return jt.queryForMap(sqlStmt, id);
    }

    // 유저 개인정보 수정
    public void userProfileChange(String id, String pw, String nm, String birthDate, String email, String phone, String address, String address_detail)
    {
        String sqlStmt = "UPDATE tb_user_mst SET pw = ?, nm = ?, birth_date = ?, email = ?, phone = ?, address = ?, address_detail = ? WHERE id = ?";
        jt.update(sqlStmt, pw, nm, birthDate, email, phone, address, address_detail, id);
    }

    // 유저 개인이 삭제 (del_fg == 1로 변경)
    public void userDelete(String id)
    {
        String sqlStmt = "UPDATE tb_user_mst SET del_fg = 1 WHERE id = ?";
        jt.update(sqlStmt, id);
    }

    // 상품 9개 가져오기
    public List<Map<String, Object>> selectProduct_9() {
        String sqlStmt = "SELECT    TM.SEQ             AS seq, "                        +
                         "          TM.seller_id       AS seller_id, "                  +
                         "          TM.prod_img        AS prod_img, "                   +
                         "          TM.prod_nm         AS prod_nm, "                    +
                         "          PD.prod_title      AS prod_title, "                 +
                         "          PD.prod_price      AS prod_price, "                 +
                         "          PD.prod_grade      AS prod_grade, "                 +
                         "          PD.prod_variety    AS prod_variety "                +
                         "FROM      tb_product_mst TM "                                 +
                         "LEFT JOIN tb_product_detail PD ON TM.SEQ = PD.SEQ "   +
                         "WHERE PD.prod_amount <> 0 " +
                         "ORDER BY  TM.SEQ DESC "                                       +
                         "LIMIT 9"; 
        return jt.queryForList(sqlStmt);
    }

    // 저장된 주소지 가져오기
    public List<Map<String, Object>> selectAddress(String id) {
        String sqlStmt = "SELECT seq, name, phone, address, address_detail, main_address FROM tb_address_mst WHERE id = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForList(sqlStmt, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 주소 Insert
    public void insertAddress(String id, String name, String phone, String address, String address_detail)
    {
        String sqlStmt = "INSERT INTO tb_address_mst (id, name, phone, address, address_detail) VALUES (?, ?, ?, ?, ?)";
        jt.update(sqlStmt, id, name, phone, address, address_detail);
    }

    // 주소 Delete
    public void deleteAddress(String seq) {
        String sqlStmt = "DELETE FROM tb_address_mst WHERE seq = ?";
        jt.update(sqlStmt, seq);
    }

    // 메인 주소 Update
    public void mainAddress(String seq, String id) {
        String sqlStmt1 = "UPDATE tb_address_mst SET main_address = 0 WHERE id = ?";
        String sqlStmt2 = "UPDATE tb_address_mst SET main_address = 1 WHERE seq = ?";
        jt.update(sqlStmt1, id);
        jt.update(sqlStmt2, seq);
    }

    // 장바구니 SELECT
    public List<Map<String, Object>> cartSelect(String userId)
    {
        String sqlStmt = "SELECT    PM.prod_img                     AS prod_img, " +
                         "          PM.seq                          AS prod_id, " +
                         "          PD.prod_title                   AS prod_title, " +
                         "          PD.prod_price                   AS prod_price, " +
                         "          CM.prod_amount                  AS prod_amount, " +
                         "         (PD.prod_price * CM.prod_amount) AS total_price, " +
                         "          PM.seller_id                    AS seller_id, " +
                         "          SM.nm                           AS seller_name " +
                         "FROM      tb_cart_mst CM " +
                         "          JOIN tb_product_mst PM ON CM.prod_id = PM.seq " +
                         "          JOIN tb_product_detail PD ON PM.seq = PD.seq " +
                         "          JOIN tb_seller_mst SM ON PM.seller_id = SM.id " +
                         "WHERE     CM.user_id = ?";
        return jt.queryForList(sqlStmt, userId);
    }

    // 장바구니 SELECT2
    public List<Map<String, Object>> cartSelect2(String userId)
    {
        String sqlStmt = "SELECT PM.prod_nm AS prod_nm, " +
                        "PD.prod_price AS prod_price, " +
                        "CM.prod_amount AS amount, " +
                        "(PD.prod_price * CM.prod_amount) AS total_price " +
                        "FROM tb_cart_mst CM " +
                        "JOIN tb_product_mst PM ON CM.prod_id = PM.seq " +
                        "JOIN tb_product_detail PD ON PM.seq = PD.seq " +
                        "WHERE CM.user_id = ?";
        return jt.queryForList(sqlStmt, userId);
    }

    // 장바구니 DELETE
    public void cartDelete(String userId, String prodId) {
        String sqlStmt = "DELETE FROM tb_cart_mst WHERE user_id = ? AND prod_id = ?";
        jt.update(sqlStmt, userId, prodId);
    }

    // 장바구니 여러개 DELETE
    public void cartDeleteAll(String userId, List<Long> prodId) {
        String sqlStmt = "DELETE FROM tb_cart_mst WHERE user_id = ? AND prod_id IN (" + 
                      prodId.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        jt.update(sqlStmt, userId);
    }

    // 주문내역 SELECT
    public List<Map<String, Object>> orderSelect(String userId) {
        String sqlStmt = "SELECT OM.seq AS seq, PM.prod_nm AS prod_nm, OM.prod_amount AS prod_amount, " +
                     "(PD.prod_price * OM.prod_amount) AS price, OM.reg_dt AS reg_dt, PM.prod_img AS prod_img " +
                     "FROM tb_order_mst OM " +
                     "JOIN tb_product_mst PM ON OM.prod_id = PM.seq " +
                     "JOIN tb_product_detail PD ON OM.prod_id = PD.seq " +
                     "WHERE OM.user_id = ? " +
                     "ORDER BY OM.reg_dt DESC " +
                     "LIMIT 5";
        return jt.queryForList(sqlStmt, userId);
    }

    // 장바구니에서 가져오기
    public List<Map<String, Object>> getCartItems(String userId) {
        String sqlStmt = "SELECT prod_id, prod_amount, user_id FROM tb_cart_mst WHERE user_id = ?";
        return jt.queryForList(sqlStmt, userId);
    }

    // 오더마스터 INSERT 장바구니, product_detail 있는건 DELETE
    public void cartToOrder(Map<String, Object> item) {
        String sqlStmt1 = "INSERT INTO tb_order_mst (prod_id, prod_amount, user_id, state) VALUES (?, ?, ?, 1)";
        jt.update(sqlStmt1, item.get("prod_id"), item.get("prod_amount"), item.get("user_id"));

        String sqlStmt2 = "DELETE FROM tb_cart_mst WHERE user_id = ? AND prod_id = ?";
        jt.update(sqlStmt2, item.get("user_id"), item.get("prod_id"));

        String sqlStmt3 = "UPDATE tb_product_detail SET prod_amount = prod_amount - ? WHERE seq = ?";
        jt.update(sqlStmt3, item.get("prod_amount"), item.get("prod_id"));
    }
}
