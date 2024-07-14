package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SellerDao {
    @Autowired
    JdbcTemplate jt;

    // 셀러 회원가입
    public void sellerRegister(String id,
                               String pw,
                               String nm,
                               String email,
                               String phone,
                               String address,
                               String address_detail,
                               String comp_nm,
                               String biz_id)
    {
        String sqlStmt = "INSERT INTO tb_seller_mst(id, pw, nm, email, phone, address, address_detail, comp_nm, biz_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jt.update(sqlStmt, id, pw, nm, email, phone, address, address_detail, comp_nm, biz_id);
    }

    // 셀러 아이디 중복 체크
    public int sellerDupCheck(String id) {
        String sqlStmt = "SELECT count(*) FROM tb_seller_mst WHERE id = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id);
    }

    // 셀러 이메일 중복 체크
    public int sellerDupEmailCheck(String email) {
        String sqlStmt = "SELECT count(*) FROM tb_seller_mst WHERE email = ?";
        return jt.queryForObject(sqlStmt, Integer.class, email);
    }

    // 셀러 로그인
    public Map<String, Object> sellerLogin(String id, String pw) {
        String sqlStmt = "SELECT id, nm, grade, del_fg FROM tb_seller_mst WHERE id = ? AND pw = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForMap(sqlStmt, id, pw);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 셀러 삭제 유무(del_fg) 값 가져오기
    public int sellerDelFg(String id, String pw) {
        String sqlStmt = "SELECT del_fg FROM tb_seller_mst WHERE id = ? AND pw = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw);
    }

    // 셀러 아이디 찾기
    public String findSellerId(String nm, String email) {
        String sqlStmt = "SELECT id FROM tb_seller_mst WHERE nm = ? AND email = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, String.class, nm, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 셀러 비밀번호 확인 (정보 변경을 위해서)
    public int sellerVerifyPass(String id, String pw) {
        String sqlStmt = "SELECT count(*) FROM tb_seller_mst WHERE id = ? AND pw = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, Integer.class, id, pw);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 셀러 정보 가져오기
    public List<Map<String, Object>> selectSeller(String id) {
        String sqlStmt = "SELECT seq, id, pw, nm, email, phone, address, address_detail, grade, comp_nm, biz_id " +
                         "FROM tb_seller_mst " +
                         "WHERE id = ?";
        return jt.queryForList(sqlStmt, id);
    }

    // 셀러 개인정보 수정
    public void sellerProfileChange(String id, String pw, String nm, String email, String phone, String address, String address_detail, String compNm, String bizId)
    {
        String sqlStmt = "UPDATE tb_seller_mst SET pw = ?, nm = ?, email = ?, phone = ?, address = ?, address_detail = ?, comp_nm = ?, biz_id = ? WHERE id = ?";
        jt.update(sqlStmt, pw, nm, email, phone, address, address_detail, compNm, bizId, id);
    }

    // 셀러 글 count
    public int sellerPageCnt(String id) {
        String sqlStmt = "SELECT count(*) FROM tb_product_mst WHERE seller_id = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 셀러 개인이 삭제 (del_fg == 1로 변경)
    public void sellerDelete(String id)
    {
        String sqlStmt = "UPDATE tb_seller_mst SET del_fg = 1 WHERE id = ?";
        jt.update(sqlStmt, id);
    }

    // 셀러아이디 유무만 가져오기
    public int sellerOX(String id) {
        String sqlStmt = "SELECT count(*) FROM tb_seller_mst WHERE id = ?";
        try {   // 결과가 없으면 0 반환
            return jt.queryForObject(sqlStmt, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 상품 SELECT
    public List<Map<String, Object>> productSelect(String id) {
        String sqlStmt = "SELECT    PM.seq AS SEQ, " +
                         "          PM.PROD_nm AS PROD_NM, " +
                         "          PM.PROD_IMG AS PROD_IMG, " +
                         "          PD.PROD_DESC AS PROD_DESC, " +
                         "          PD.PROD_AMOUNT AS PROD_AMOUNT, " +
                         "          PD.PROD_PRICE AS PROD_PRICE, " +
                         "          PD.PROD_GRADE AS PROD_GRADE, " +
                         "          PD.PROD_VARIETY AS PROD_VARIETY " +
                         "FROM      tb_product_mst PM " +
                         "LEFT JOIN tb_product_detail PD ON PM.SEQ = PD.SEQ " +
                         "WHERE		PM.SELLER_ID = ? "  +
                         "ORDER BY  PM.SEQ DESC";
        return jt.queryForList(sqlStmt, id);
    }

    // 상품 INSERT
    public void productInsert(String prod_category, String prod_title, String prod_desc, String prod_nm, String seller_id, String prod_img, String prod_amount, String prod_price, String prod_grade, String prod_variety) {
        String sqlStmt1 = "INSERT INTO tb_product_mst (seller_id, prod_category, prod_nm, prod_img) VALUES (?, ?, ?, ?)";
        String sqlStmt2 = "INSERT INTO tb_product_detail (prod_title, prod_desc, prod_amount, prod_price, prod_grade, prod_variety) VALUES (?, ?, ?, ?, ?, ?)";
        jt.update(sqlStmt1, seller_id, prod_category, prod_nm, prod_img);
        jt.update(sqlStmt2, prod_title, prod_desc, prod_amount, prod_price, prod_grade, prod_variety);
    }

    // 카테고리 가져오기
    public List<Map<String, Object>> categoryList() {
        String sqlStmt = "SELECT category_id, category_nm FROM tb_category_mst";
        return jt.queryForList(sqlStmt);
    }

    public List<Map<String,Object>> Gradeview() {
        String sqlStmt = "SELECT grade FROM tb_apple_mst group by grade";
        return jt.queryForList(sqlStmt);    
    }
  
    public List<Map<String,Object>> Varietyview() {
        String sqlStmt = "SELECT variety FROM tb_apple_mst group by variety";
        return jt.queryForList(sqlStmt);    
    }

    public List<Map<String,Object>> YearView() {
        String sqlStmt = "SELECT year FROM tb_apple_mst group by YEAR ORDER BY YEAR desc";
        return jt.queryForList(sqlStmt);    
    }
  
    public List<Map<String,Object>> testViewByGradeVariety(String grade, String variety) {
        String sqlStmt = "SELECT AVG(price) as price, year FROM tb_apple_mst where grade = ? and variety = ? group by year";
        return jt.queryForList(sqlStmt,grade,variety);
    }

    public List<Map<String,Object>> monthChart(String variety, String grade, String year) {
        String sqlStmt = "SELECT    AVG(price) AS price, " +
                         "          month " +
                         "FROM      tb_apple_mst " +
                         "WHERE     variety = ? " +
                         "AND       grade = ? " +
                         "AND       year = ? " +
                         "GROUP BY  variety, " +
                         "          grade, " +
                         "          year, " +
                         "          month " +
                         "ORDER BY  variety, " +
                         "          grade, " +
                         "          year, " +
                         "          month";
        return jt.queryForList(sqlStmt, variety, grade, year);
    }

    public List<Map<String, Object>> sellerprofileCheck(String sellerId, String pw, String nm, String email, String phone, String address, String addressDetail,String compNm,String bizId) {
        String sqlStmt = "SELECT seq FROM tb_seller_mst WHERE id = ? AND pw = ? AND nm = ? AND email = ? AND phone = ? AND address = ? AND address_detail = ? AND comp_nm = ? AND biz_id = ?";
        try {
            return jt.queryForList(sqlStmt, sellerId, pw, nm, email, phone,address,addressDetail,compNm, bizId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> saleProduct(String id) {
        String sqlStmt = "SELECT OM.seq AS seq, " +
                 "UM.nm AS nm, " +
                 "PM.prod_img AS prod_img, " +
                 "PM.prod_nm AS prod_nm, " +
                 "OM.prod_amount AS prod_amount, " +
                 "(PD.prod_price * OM.prod_amount) AS total_price, " +
                 "PD.prod_grade AS prod_grade, " +
                 "PD.prod_variety AS prod_variety, " +
                 "OM.reg_dt AS reg_dt, " +
                 "AM.NAME AS delivery_name, " +
                 "AM.address AS address, " +
                 "AM.address_detail AS address_detail, " +
                 "OM.state AS state " +
                 "FROM tb_order_mst OM " +
                 "JOIN tb_product_mst PM ON OM.prod_id = PM.seq " +
                 "JOIN tb_product_detail PD ON PM.seq = PD.seq " +
                 "JOIN tb_user_mst UM ON OM.user_id = UM.id " +
                 "JOIN tb_address_mst AM ON OM.address_id = AM.seq " +
                 "JOIN tb_seller_mst SM ON PM.seller_id = SM.id " +
                 "WHERE SM.id = ? " +
                 "ORDER BY OM.reg_dt DESC;";
        return jt.queryForList(sqlStmt, id);
    }

    // 배송 상태 값 가져오기
    public Map<String, Object> selectDelivery(String seq) {
        String sqlStmt = "SELECT seq, delivery_number, state FROM tb_order_mst WHERE seq = ?";
        return jt.queryForMap(sqlStmt, seq);
    }

    // 배송 상태 변경
    public void changeDeliveryState(String seq, String deliveryNumber) {
        String sqlStmt = "UPDATE tb_order_mst SET delivery_number = ?, state = 1 WHERE seq = ?";
        jt.update(sqlStmt, deliveryNumber, seq);
    }
}
