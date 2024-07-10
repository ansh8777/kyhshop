package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FindDao {
    @Autowired
    JdbcTemplate jt;

    // 비밀번호 찾기위해 아이디, 이메일 있는지 확인
    public boolean findUser(String id, String email) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ? AND email = ?";
        int cnt = jt.queryForObject(sqlStmt, Integer.class, id, email);
        return cnt == 1;            // 찾으면 true, 못찾으면 false
    }

    // 랜덤 비밀번호 업데이트
    public void randomPw(String id, String randPw) {
        String sqlStmt = "UPDATE tb_user_mst SET pw = ? WHERE id = ?";
        jt.update(sqlStmt, randPw, id);
    }

    // (셀러) 비밀번호 찾기위해 아이디, 이메일 있는지 확인
    public boolean findSeller(String id, String email) {
        String sqlStmt = "SELECT count(*) FROM tb_seller_mst WHERE id = ? AND email = ?";
        int cnt = jt.queryForObject(sqlStmt, Integer.class, id, email);
        return cnt == 1;            // 찾으면 true, 못찾으면 false
    }

    // (셀러) 랜덤 비밀번호 업데이트
    public void randomPwSeller(String id, String randPw) {
        String sqlStmt = "UPDATE tb_seller_mst SET pw = ? WHERE id = ?";
        jt.update(sqlStmt, randPw, id);
    }
}
