package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SellerDao {
    @Autowired
    JdbcTemplate jt;

    // 상품 SELECT
    public List<Map<String, Object>> productSelect(String id) {
        String sqlStmt = "SELECT    PM.seq AS SEQ, " +
                         "          PM.PROD_ID AS PROD_ID, " +
                         "          PM.PROD_nm AS PROD_NM, " +
                         "          PM.PROD_IMG AS PROD_IMG, " +
                         "          PD.PROD_DESC AS PROD_DESC, " +
                         "          PD.PROD_AMOUNT AS PROD_AMOUNT, " +
                         "          PD.PROD_PRICE AS PROD_PRICE " +
                         "FROM      TB_PRODUCT_MST PM " +
                         "          JOIN TB_PRODUCT_DETAIL PD ON PM.PROD_ID = PD.PROD_ID " +
                         "WHERE		PM.SELLER_ID = ? "  +
                         "ORDER BY  PM.SEQ DESC";
        return jt.queryForList(sqlStmt, id);
    }

    // 상품 INSERT
    public void productInsert(String prod_id, String prod_category, String prod_title, String prod_desc, String prod_nm, String seller_id, String prod_img, String prod_amount, String prod_price) {
        String sqlStmt1 = "INSERT INTO tb_product_mst (prod_id, seller_id, prod_category, prod_nm, prod_img) VALUES (?, ?, ?, ?, ?)";
        String sqlStmt2 = "INSERT INTO tb_product_detail (prod_id, prod_title, prod_desc, prod_amount, prod_price) VALUES (?, ?, ?, ?, ?)";
        jt.update(sqlStmt1, prod_id, seller_id, prod_category, prod_nm, prod_img);
        jt.update(sqlStmt2, prod_id, prod_title, prod_desc, prod_amount, prod_price);
    }

    // 카테고리 가져오기
    public List<Map<String, Object>> categoryList() {
        String sqlStmt = "SELECT category_id, category_nm FROM tb_category_mst";
        return jt.queryForList(sqlStmt);
    }
}
