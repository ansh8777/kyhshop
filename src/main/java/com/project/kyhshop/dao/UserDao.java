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
        String sqlStmt = "SELECT id, nm, grade FROM tb_user_mst WHERE id = ? AND pw = ?";
        return jt.queryForList(sqlStmt, id, pw);
    }

    // 삭제 유무(del_fg) 값 가져오기
    public int delFg(String id, String pw) {
        String sqlStmt = "SELECT del_fg FROM tb_user_mst WHERE id = ? AND pw = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw);
    }

    // 탈퇴 회원 정보 체크하기
    public int delUserCheck(String id, String pw, String nm) {
        String sqlStmt = "SELECT count(*) FROM tb_user_mst WHERE id = ? AND pw = ? AND nm = ?";
        return jt.queryForObject(sqlStmt, Integer.class, id, pw, nm);
    }

    // 탈퇴 회원 재가입
    public void resign(String id) {
        String sqlStmt = "UPDATE tb_user_mst SET del_fg = 0 WHERE id = ?";
        jt.update(sqlStmt, id);
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

    // 유저 개인이 삭제 (del_fg == 1로 변경)
    public void userDelete(String id)
    {
        String sqlStmt = "UPDATE tb_user_mst SET del_fg = 1 WHERE id = ?";
        jt.update(sqlStmt, id);
    }
}
