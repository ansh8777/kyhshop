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
        String sqlStmt = "SELECT id, nm, grade, del_fg FROM tb_user_mst WHERE id = ? AND BINARY(pw) = ?";
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
                         "          CM.seq                          AS seq, " +
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
                        "PM.seq AS seq, " +
                        "PM.prod_img AS prod_img, " +
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
        String sqlStmt = "SELECT OM.seq AS seq, OM.prod_id AS prod_id, PM.prod_nm AS prod_nm, OM.prod_amount AS prod_amount, OM.state AS state, " +
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
        String sqlStmt1 = "INSERT INTO tb_order_mst (prod_id, prod_amount, user_id, address_id) VALUES (?, ?, ?, ?)";
        jt.update(sqlStmt1, item.get("prod_id"), item.get("prod_amount"), item.get("user_id"), item.get("address_id"));

        String sqlStmt2 = "DELETE FROM tb_cart_mst WHERE user_id = ? AND prod_id = ?";
        jt.update(sqlStmt2, item.get("user_id"), item.get("prod_id"));

        String sqlStmt3 = "UPDATE tb_product_detail SET prod_amount = prod_amount - ? WHERE seq = ?";
        jt.update(sqlStmt3, item.get("prod_amount"), item.get("prod_id"));
    }

    // product_detail 수량 가져오기
    public int getProductAmount(String prodId) {
        String sqlStmt = "SELECT prod_amount FROM tb_product_detail WHERE seq = ?";
        return jt.queryForObject(sqlStmt, Integer.class, prodId);
    }

    // product_detail
    public String getProductNm(String prodId) {
        String sqlStmt = "SELECT prod_nm FROM tb_product_mst WHERE seq = ?";
        return jt.queryForObject(sqlStmt, String.class, prodId);
    }

    // 장바구니 수량 변경
    public void changeCartAmount(String seq, int amount, String userId) {
        String sqlStmt = "UPDATE tb_cart_mst SET prod_amount = ? WHERE seq = ? AND user_id = ?";
        jt.update(sqlStmt, amount, seq, userId);
    }

    // 유저 회원정보 수정할 때 중복확인
    public int userSelect(String id, String pw, String birthDate, String email, String phone, String address, String address_detail) {
        String sqlStmt = "SELECT count(*) " +
                         "FROM tb_user_mst " +
                         "WHERE id = ? " +
                         "and pw = ? " +
                         "and birth_date = ? " +
                         "and email = ? " +
                         "and phone = ? " +
                         "and address = ? " +
                         "and address_detail = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw, birthDate, email, phone, address, address_detail);
    }

    // 배송 팝업 내용 SELECT (유저전용)
    public Map<String, Object> deliSelect(String seq)
    {
        String sqlStmt = "SELECT seq, delivery_number, state FROM tb_order_mst WHERE seq = ?";
        return jt.queryForMap(sqlStmt, seq);
    }

    // 수취확인
    public void deliComplete(String seq)
    {
        String sqlStmt = "UPDATE tb_order_mst SET state = 2 WHERE seq = ?";
        jt.update(sqlStmt, seq);
    }

    // 포인트 가져오기
    public Map<String, Object> getPoint(String userId)
    {
        String sqlStmt = "SELECT    UM.nm    AS nm, "                        +
                         "          PM.point AS point "                      +
                         "FROM      tb_user_mst UM "                         +
                         "JOIN      tb_point_mst PM ON UM.id = PM.user_id "  +
                         "WHERE     UM.id = ?";
        return jt.queryForMap(sqlStmt, userId);
    }

    // 오더 배송완료상태 가져오기
    public Map<String, Object> getOrder(String userId)
    {
        String sqlStmt = "SELECT prod_id FROM tb_order_mst WHERE user_id = ? AND state = 2";
        return jt.queryForMap(sqlStmt, userId);
    }

    // 오더 상품에 대한 자신 리뷰개수 가져오기
    public int getReviewCnt(String userId, String prodId)
    {
        String sqlStmt = "SELECT COUNT(*) AS review_count " +
                         "FROM tb_review_mst RM " +
                         "LEFT JOIN tb_order_mst OM ON RM.prod_id = OM.prod_id AND RM.USER_ID = OM.USER_ID " +
                         "WHERE OM.STATE = 2 " +
                         "AND RM.PROD_ID = ? " +
                         "AND RM.USER_ID = ?";
        return jt.queryForObject(sqlStmt, Integer.class, prodId, userId);
    }

    // 리뷰 작성하기
    public void reviewInsert(String userId, String prodId, String img, String review, double score) {
        String sqlStmt = "INSERT INTO tb_review_mst (prod_id, user_id, img, review, score) VALUES (?, ?, ?, ?, ?)";
        jt.update(sqlStmt, prodId, userId, img, review, score);
    }

    // 리뷰 수정하기
    public void reviewEdit(String userId, String seq, String img, String review, double score) {
        String sqlStmt = "UPDATE tb_review_mst SET review = ?, score = ?, img = ? WHERE seq = ? AND user_id = ?";
        jt.update(sqlStmt, review, score, img, seq, userId);
    }

    // 기존 이미지 가져오기
    public String getOriginImg(String seq) {
        String sqlStmt = "SELECT img FROM tb_review_mst WHERE seq = ?";
        return jt.queryForObject(sqlStmt, String.class, seq);
    }

    // 리뷰 가져오기
    public Map<String, Object> getReview(String seq) {
        String sqlStmt = "SELECT seq, img, review, score FROM tb_review_mst WHERE seq = ?";
        return jt.queryForMap(sqlStmt, seq);
    }

    // 리뷰가 등록된 페이지 가져오기
    public String getPage(String seq) {
        String sqlStmt = "SELECT prod_id FROM tb_review_mst WHERE seq = ?";
        return jt.queryForObject(sqlStmt, String.class, seq);
    }

    // 리뷰 삭제하기
    public void deleteReview(String seq) {
        String sqlStmt = "DELETE FROM tb_review_mst WHERE seq = ?";
        jt.update(sqlStmt, seq);
    }
}
