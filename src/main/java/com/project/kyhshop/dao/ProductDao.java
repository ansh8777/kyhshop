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
        String sqlStmt = "SELECT    TM.SEQ             AS seq, "                        +
                         "          PD.PROD_TITLE      AS prod_title, "                 +
                         "          PD.PROD_DESC      AS prod_desc, "                   +
                         "          TM.prod_img        AS prod_img, "                   +
                         "          TM.prod_nm         AS prod_nm, "                    +
                         "          PD.prod_price      AS prod_price "                  +
                         "FROM      TB_PRODUCT_MST TM "                                 +
                         "JOIN      TB_PRODUCT_DETAIL PD ON TM.PROD_ID = PD.PROD_ID "   +
                         "WHERE     TM.SEQ = ?";
        return jt.queryForList(sqlStmt, seq);
    }
}
