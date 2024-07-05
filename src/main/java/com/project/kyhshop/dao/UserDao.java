package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
                         String nm,
                         String birthDate,
                         String email,
                         String phone,
                         String address,
                         String address_detail)
    {
        String sqlStmt = "INSERT INTO tb_user_mst(id, pw, nm, birth_date, email, phone, address, address_detail) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        jt.update(sqlStmt, id, pw, nm, birthDate, email, phone, address, address_detail);
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

    // 아이디 찾기
    public String findid(String nm, String birthDate) {
        String sqlStmt = "SELECT id FROM tb_user_mst WHERE nm = ? AND birth_date = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, String.class, nm, birthDate);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 비밀번호 확인 (정보 변경을 위해서)
    public int verifyPass(String id, String pw) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ? AND pw = ?";
        try {   // 결과가 없으면 null 반환
            return jt.queryForObject(sqlStmt, Integer.class, id, pw);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 유저 정보 가져오기
    public List<Map<String, Object>> selectUser(String id) {
        String sqlStmt = "SELECT seq, id, pw, nm, birth_date, email, phone, address, address_detail, grade " +
                         "FROM TB_USER_MST " +
                         "WHERE id = ?";
        return jt.queryForList(sqlStmt, id);
    }

    // 유저 개인정보 수정
    public void userProfileChange(String id, String pw, String nm, String birthDate, String email, String phone, String address, String address_detail)
    {
        String sqlStmt = "UPDATE tb_user_mst SET pw = ?, nm = ?, birth_date = ?, email = ?, phone = ?, address = ?, address_detail = ? WHERE id = ?";
        jt.update(sqlStmt, pw, nm, birthDate, email, phone, address, address_detail, id);
    }
}
