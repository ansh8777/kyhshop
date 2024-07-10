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

    // 판매자 회원가입
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
}
