package com.project.kyhshop.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {
    @Autowired
    JdbcTemplate jt;

    // 재고 수량 가져오기
    public int getAmount(String seq) {
        String sqlStmt = "SELECT prod_amount FROM tb_product_detail WHERE seq = ?";
        return jt.queryForObject(sqlStmt, Integer.class, seq);
    }

    // 메인 배송지 가져오기
    public Map<String, Object> getMainAddress(String id) {
        String sqlStmt = "SELECT seq AS address_seq, name, phone, address, address_detail FROM tb_address_mst WHERE id = ? AND main_address = 1";
        try {
            return jt.queryForMap(sqlStmt, id);
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>(); // 결과가 없을 경우 빈 맵 반환
        }
    }

    // 주문 정보 가져오기
    public List<Map<String, Object>> orderSelect(String seq, int amount)
    {
        String sqlStmt = "SELECT    PM.seq          AS seq, " +
                         "          PM.prod_nm      AS prod_nm, " +
                         "          PM.prod_img     AS prod_img, " +
                         "          PD.prod_price   AS prod_price, " +
                         "          ?               AS amount, " +
                         "         (PD.prod_price * ?) AS total_price " +
                         "FROM      tb_product_mst PM " +
                         "          JOIN tb_product_detail PD ON PM.seq = PD.seq " +
                         "WHERE     PM.seq = ?";
        return jt.queryForList(sqlStmt, amount, amount, seq);
    }

    // 결제 완료 db 넣기
    public void orderInsert(String id, String seq, int amount, String address) {
        String sqlStmt1 = "INSERT INTO tb_order_mst (prod_id, prod_amount, user_id, address_id) VALUES (?, ?, ?, ?)";
        jt.update(sqlStmt1, seq, amount, id, address);

        String sqlStmt2 = "UPDATE tb_product_detail SET prod_amount = prod_amount - ? WHERE seq = ?";
        jt.update(sqlStmt2, amount, seq);
    }

    // 장바구니 상품 추가
    public void cartInsert(String id, String seq, int amount) {
        String sqlStmt = "INSERT INTO tb_cart_mst (prod_id, prod_amount, user_id) VALUES (?, ?, ?)";
        jt.update(sqlStmt, seq, amount, id);
    }

    // 기존 장바구니 항목 확인
    public Map<String, Object> findCartProduct(String userId, String prodId) {
        String sqlStmt = "SELECT * FROM tb_cart_mst WHERE user_id = ? AND prod_id = ?";
        try {
            return jt.queryForMap(sqlStmt, userId, prodId);
        } catch (EmptyResultDataAccessException e) {
            return null; // 결과가 없는 경우 null 반환
        }
    }

    // 장바구니 업데이트
    public void updateCartProduct(String userId, String prodId, int amount)
    {
        String sqlStmt = "UPDATE tb_cart_mst SET prod_amount = ? WHERE user_id = ? AND prod_id = ?";
        jt.update(sqlStmt, amount, userId, prodId);
    }
}
