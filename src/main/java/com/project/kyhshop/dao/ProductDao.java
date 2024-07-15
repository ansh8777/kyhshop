package com.project.kyhshop.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
                         "FROM      tb_product_mst PM "                                 +
                         "LEFT JOIN tb_product_detail PD ON PM.SEQ = PD.SEQ "   +
                         "WHERE     PM.SEQ = ?";
        return jt.queryForList(sqlStmt, seq);
    }

    // 상품 이미지 가져오기
    public String getOriginImg(String seq) {
        String sqlStmt = "SELECT prod_img FROM tb_product_mst WHERE seq = ?";
        return jt.queryForObject(sqlStmt, String.class, seq);
    }

    // 상품 수정
    public void productEdit(String seq, String seller_id, String prod_category, String prod_nm, String prod_img, String prod_title, String prod_desc, String prod_amount, String prod_price, String prod_grade, String prod_variety)
    {
        String sqlStmt1 = "UPDATE tb_product_mst SET prod_category = ?, prod_nm = ?, prod_img = ? WHERE seq = ?";
        String sqlStmt2 = "UPDATE tb_product_detail SET prod_title = ?, prod_desc = ?, prod_amount = ?, prod_price = ?, prod_grade = ?, prod_variety = ? WHERE seq = ?";

        jt.update(sqlStmt1, prod_category, prod_nm, prod_img, seq);
        jt.update(sqlStmt2, prod_title, prod_desc, prod_amount, prod_price, prod_grade, prod_variety, seq);
    }

    // 상품 삭제
    public void productDelete(String seq) {
        String sqlStmt1 = "DELETE FROM tb_product_mst WHERE seq = ?";
        String sqlStmt2 = "DELETE FROM tb_product_detail WHERE seq = ?";

        jt.update(sqlStmt1, seq);
        jt.update(sqlStmt2, seq);
    }

    // 상품 모두 가져오기
    public List<Map<String, Object>> productSelect(String search) {
        String sqlStmt = "SELECT    PM.seq AS SEQ, " +
                         "          PM.PROD_nm AS PROD_NM, " +
                         "          PM.PROD_IMG AS PROD_IMG, " +
                         "          PD.PROD_title AS PROD_title, " +
                         "          PD.PROD_DESC AS PROD_DESC, " +
                         "          PD.PROD_AMOUNT AS PROD_AMOUNT, " +
                         "          PD.PROD_PRICE AS PROD_PRICE, " +
                         "          PD.PROD_GRADE AS PROD_GRADE, " +
                         "          PD.PROD_VARIETY AS PROD_VARIETY, " +
                         "          SM.nm AS SELLER_NM " +
                         "FROM      tb_product_mst PM   LEFT JOIN tb_product_detail PD ON PM.seq = PD.seq " +
                         "                              LEFT JOIN tb_seller_mst SM ON PM.seller_id = SM.id " +
                         "WHERE     PD.PROD_AMOUNT <> 0 ";

        if (search != null && !search.isEmpty()) {
            sqlStmt += "AND PD.PROD_title LIKE ? ";
        }
        sqlStmt += "ORDER BY PM.SEQ DESC";

        if (search != null && !search.isEmpty()) {
            return jt.queryForList(sqlStmt, "%" + search + "%");
        } else {
            return jt.queryForList(sqlStmt);
        }
    }

    // 사과 등급, 품종 가져오기
    public List<Map<String, Object>> gradevariety(String seq) {
        String sqlstmt = "SELECT    prod_grade AS grade, " +
                         "          prod_variety AS variety " +
                         "FROM      tb_product_detail " +
                         "WHERE     SEQ = ?";
        return jt.queryForList(sqlstmt,seq);
    }

    // 최저가 가져오기
    public List<Map<String, Object>> lowerPrice(String garde, String variety) {
        String sqlstmt = "SELECT    min(prod_price) as minprice " +
        "FROM      tb_product_detail " +
        "WHERE     prod_grade = ? and prod_variety = ?";
        return jt.queryForList(sqlstmt,garde,variety);
    }   

    // 리뷰 가져오기
    public List<Map<String, Object>> reviewList(String prodId) {
        String sqlStmt = "SELECT UM.nm AS nm, " +
                         "RM.seq AS seq, " +
                         "RM.user_id AS id, " +
                         "RM.score AS score, " +
                         "RM.img AS img, " +
                         "RM.review AS review " +
                         "FROM tb_review_mst RM " +
                         "JOIN tb_user_mst UM ON RM.USER_ID = UM.ID " +
                         "WHERE RM.PROD_ID = ?";
        return jt.queryForList(sqlStmt, prodId);
    }

    // 평균점수
    public double avgScore(String prodId) {
        String sqlStmt = "SELECT avg(score) " +
                 "FROM tb_review_mst " +
                 "WHERE PROD_ID = ?";
        Double avgs = jt.queryForObject(sqlStmt, double.class, prodId);
        return avgs != null ? avgs : 0.0;
    }

    // 리뷰 개수
    public int reviewCnt(String prodId) {
        String sqlStmt = "SELECT count(*) FROM tb_review_mst WHERE prod_id = ?";
        try {
            return jt.queryForObject(sqlStmt, Integer.class, prodId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
