package com.project.kyhshop.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao {
    @Autowired
    JdbcTemplate jt;

    // 카테고리 리스트 전부 가져오기
    public List<Map<String, Object>> selectAllCategories() {
        String sqlStmt = "SELECT * FROM tb_category_mst"; // 테이블 이름 수정
        return jt.queryForList(sqlStmt);
    }

    // 카테고리 추가 메소드
    public void insertCategory(String categoryName) {
        String sqlStmt = "INSERT INTO tb_category_mst (category_nm) VALUES (?)"; // 컬럼명 수정
        jt.update(sqlStmt, categoryName);
    }
}
