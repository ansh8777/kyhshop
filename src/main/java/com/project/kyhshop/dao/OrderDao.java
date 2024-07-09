package com.project.kyhshop.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {
    @Autowired
    JdbcTemplate jt;

    // 상품 아이디 구매전인 상태로 생성
    public void orderBasketInsertAction(String productId, String userId){
        String sqlStmt = "insert into order_mst (product_id, user_id,order_state) values(?,?,0)";
        jt.update(sqlStmt,productId,userId);
    }
   
    // 상품 자세히 보기
    public List<Map<String,Object>> orderBasketdetail(String productId, String userId){
        String sqlStmt = "select * from order_mst where order_state=0 and product_id =? and user_id=?";
        return jt.queryForList(sqlStmt,productId,userId);
    }

    
    // 상세보기 -> 장바구니
    public void orderBuyChange(String amount, String userId, String product_id) {
        String sqlStmt = "INSERT INTO order_mst(order_state,amount,user_id,product_id) VALUES(1, ?, ?, ?)";
        jt.update(sqlStmt, amount, userId , product_id);
    }  

    // 상품 상세보기 -> 구매
    public void orderBasketChange(String amount, String userId, String product_id) {
        String sqlStmt = "INSERT INTO order_mst(order_state,amount,user_id,product_id) VALUES(0, ?, ?, ?)";
        jt.update(sqlStmt, amount, userId , product_id);
    }  

    // 구매 목록보기 (구매자가 같은)
    public List<Map<String,Object>> orderBuyInsertAction2(String userId){
        String sqlStmt = "select * from order_mst where user_id=? and order_state=1";
        return jt.queryForList(sqlStmt,userId);
    }

 // ----------------------------------------------------------------------------------------------

    public List<Map<String,Object>> MypageOrder(String userId) {
        String sqlStmt = "select * from order_mst where order_state=0 and user_id='"+userId+"'";
        return jt.queryForList(sqlStmt);
    }  

    public List<Map<String,Object>> MypageBuy(String userId) {
        String sqlStmt = "select * from order_mst where order_state=1 and user_id='"+userId+"'";
        return jt.queryForList(sqlStmt);
    }  


    // 장바구니 -> 구매완료
    public void orderBasketChangeAction(String userId) {
        String sqlStmt = "UPDATE order_mst SET order_state = 1 WHERE user_id = ?";
        jt.update(sqlStmt, userId);
    }
}
