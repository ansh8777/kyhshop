package com.project.kyhshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AdminDao {
    @Autowired
    JdbcTemplate jt;

    // 유저 리스트 전부 가져오기
    public List<Map<String, Object>> selectUser() {
        String sqlStmt = "SELECT seq, "             +
                         "       id, "              +
                         "       pw, "              +
                         "       nm, "              +
                         "       birth_date, "      +
                         "       email, "           +
                         "       phone, "           +
                         "       address, "         +
                         "       address_detail, "  +
                         "       grade, "           +
                         "       reg_dt, "          +
                         "       del_fg "           +
                         "FROM   tb_user_mst";
        return jt.queryForList(sqlStmt);
    }

    // 판매자 리스트 전부 가져오기
    public List<Map<String, Object>> selectSeller() {
        String sqlStmt = "SELECT seq, "             +
                         "       id, "              +
                         "       pw, "              +
                         "       nm, "              +
                         "       email, "           +
                         "       phone, "           +
                         "       address, "         +
                         "       address_detail, "  +
                         "       grade, "           +
                         "       comp_nm, "         +
                         "       biz_id, "          +
                         "       reg_dt, "          +
                         "       del_fg "           +
                         "FROM   tb_seller_mst";
        return jt.queryForList(sqlStmt);
    }

    // 상품 목록 조회 메소드 구현
    public List<Map<String, Object>> selectProductList(String orderString) {
        String sqlStmt = "SELECT p.seq as seq, " +
                         "p.prod_id as prodId, " +
                         "p.seller_id as sellerId, " +
                         "p.prod_category as productCategory, " +
                         "p.prod_nm as productName, " +
                         "(SELECT code_name FROM tb_code_detail WHERE seq = p.prod_category) as category, " +
                         "p.product_state as productState, " +
                         "(SELECT code_name FROM tb_code_detail WHERE seq = p.product_state) as state, " +
                         "p.reg_dt as regDt, " +
                         "d.prod_amount as prodAmount " +
                         "FROM tb_product_mst p " +
                         "JOIN tb_product_detail d ON p.prod_id = d.prod_id " +
                         "ORDER BY " + orderString;

        return jt.queryForList(sqlStmt);
    }

    // 유저 삭제
    public void deleteUser(String seq) {
        String sqlStmt = "DELETE FROM tb_user_mst WHERE seq = ? ";
        jt.update(sqlStmt,seq);
    }

    // 셀러 삭제
    public void deleteSeller(String seq) {
        String sqlStmt = "DELETE FROM tb_seller_mst WHERE seq = ? ";
        jt.update(sqlStmt,seq);
    }

    // 유저 수정
    public void userUpdate(String seq,
                              String id,
                              String pw,
                              String nm,
                              String birthDate,
                              String email,
                              String phone,
                              String address,
                              String addressDetail,
                              String grade,
                              String delFg) {
          String sqlStmt = "UPDATE tb_user_mst SET id = ?, pw = ?, nm = ?, birth_date = ?, email = ?, phone = ?, address = ?, address_detail = ?, grade = ?, del_fg = ? WHERE seq = ?";
          jt.update(sqlStmt, id, pw, nm, birthDate, email, phone, address, addressDetail, grade, delFg, seq);
   }

   // 셀러 수정
    public void sellerUpdate(String seq,
                              String id,
                              String pw,
                              String nm,
                              String email,
                              String phone,
                              String compNm,
                              String bizId,
                              String address,
                              String addressDetail,
                              String grade,
                              String delFg) {
          String sqlStmt = "UPDATE tb_seller_mst SET id = ?, pw = ?, nm = ?, email = ?, phone = ?,comp_nm = ?,biz_id = ?, address = ?, address_detail = ?, grade = ?, del_fg = ? WHERE seq = ?";
          jt.update(sqlStmt, id, pw, nm, email, phone, compNm, bizId, address, addressDetail, grade, delFg, seq);
   }
}
