package com.example.shopapppro.service.OrderDetail;

import com.example.shopapppro.dtos.OrderDetailDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;

    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;

    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailData)throws DataNotFoundException;

    void deleteById(Long id);

    List<OrderDetail> findByOrderId (Long orderId);
}