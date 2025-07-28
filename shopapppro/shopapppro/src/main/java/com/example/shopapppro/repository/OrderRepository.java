package com.example.shopapppro.repository;

import com.example.shopapppro.models.Order;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //tìm các đơn đặt hàng của ng dùng
    List<Order> findByUserId(long userId);
}
