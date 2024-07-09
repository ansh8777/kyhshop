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
