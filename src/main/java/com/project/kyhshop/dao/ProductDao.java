package com.project.kyhshop.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDao {
    @Autowired
    JdbcTemplate jt;

    // 1개 상품 페이지 가져오기
    public List<Map<String, Object>> productSelect_1(String seq)
    {
        String sqlStmt = "SELECT    PM.SEQ             AS seq, "                        +
                         "          PM.prod_id         AS prod_id, "                    +
                         "          PM.SELLER_ID       AS seller_id, "                  +
                         "          PM.prod_category   AS prod_category, "              +
                         "          PD.PROD_TITLE      AS prod_title, "                 +
                         "          PD.PROD_DESC       AS prod_desc, "                  +
                         "          PM.prod_img        AS prod_img, "                   +
                         "          PM.prod_nm         AS prod_nm, "                    +
                         "          PD.prod_price      AS prod_price, "                 +
                         "          PD.prod_amount     AS prod_amount, "                +
                         "          PD.prod_grade      AS prod_grade, "                 +
                         "          PD.prod_variety    AS prod_variety "                +
                         "FROM      TB_PRODUCT_MST PM "                                 +
                         "LEFT JOIN TB_PRODUCT_DETAIL PD ON PM.SEQ = PD.SEQ "   +
                         "WHERE     PM.SEQ = ?";
        return jt.queryForList(sqlStmt, seq);
    }

    // 상품 수정
    public void productEdit(String seq, String prod_id, String seller_id, String prod_category, String prod_nm, String prod_img, String prod_title, String prod_desc, String prod_amount, String prod_price, String prod_grade, String prod_variety)
    {
        String sqlStmt1 = "UPDATE tb_product_mst SET prod_id = ?, prod_category = ?, prod_nm = ?, prod_img = ? WHERE seq = ?";
        String sqlStmt2 = "UPDATE tb_product_detail SET prod_id = ?, prod_title = ?, prod_desc = ?, prod_amount = ?, prod_price = ?, prod_grade = ?, prod_variety = ? WHERE seq = ?";

        jt.update(sqlStmt1, prod_id, prod_category, prod_nm, prod_img, seq);
        jt.update(sqlStmt2, prod_id, prod_title, prod_desc, prod_amount, prod_price, prod_grade, prod_variety, seq);
    }

    // 상품 삭제
    public void productDelete(String seq) {
        String sqlStmt1 = "DELETE FROM tb_product_mst WHERE seq = ?";
        String sqlStmt2 = "DELETE FROM tb_product_detail WHERE seq = ?";

        jt.update(sqlStmt1, seq);
        jt.update(sqlStmt2, seq);
    }
}