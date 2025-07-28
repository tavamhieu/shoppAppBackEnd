package com.example.shopapppro.service.Order;

import com.example.shopapppro.dtos.OrderDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.Order;
import com.example.shopapppro.responces.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;

    Order getOrder(Long id);

    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;

    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId);

    //List<Order> findByUserId(Long userId);

   // Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}