package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SellerDao {
    @Autowired
    JdbcTemplate jt;

    // // 상품 SELECT
    // public List<Map<String, Object>> productSelect(String id) {
    //     String sqlStmt = "SELECT seq, prod_id, "
    // }
}
