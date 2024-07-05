package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDao {
    @Autowired
    JdbcTemplate jt;

    // 회원가입
    public void register(String id,
                         String pw,
                         String name,
                         String birthDate,
                         String email,
                         String phone,
                         String address,
                         String address_detail)
    {
        String sqlStmt = "INSERT INTO tb_user_mst(id, pw, nm, birth_date, email, phone, address, address_detail) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        jt.update(sqlStmt, id, pw, name, birthDate, email, phone, address, address_detail);
    }

    // 아이디 중복 체크
    public int dupCheck(String id) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id);
    }

    // 로그인
    public List<Map<String, Object>> login(String id, String pw) {
        String sqlStmt = "SELECT id, nm FROM tb_user_mst WHERE id = ? AND pw = ? AND del_fg = 0";
        return jt.queryForList(sqlStmt, id, pw);
    }
}
