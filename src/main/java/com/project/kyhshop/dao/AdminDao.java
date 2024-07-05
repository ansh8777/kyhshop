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
}
