package com.example.shopapppro.controller;

import com.example.shopapppro.dtos.OrderDetailDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.OrderDetail;
import com.example.shopapppro.responces.OrderDetailResponse;
import com.example.shopapppro.service.OrderDetail.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    //Thêm mới 1 order detail
    @PostMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result) {
        try {
//            if (result.hasErrors()) {
//                List<String> errorMessages = result.getFieldErrors()
//                        .stream()
//                        .map(FieldError::getDefaultMessage)
//                        .toList();
//                return ResponseEntity.badRequest().body(errorMessages);
//            }

            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") long id) throws DataNotFoundException {
        OrderDetail orderDetail=orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));

//        return ResponseEntity.ok().body(orderDetail);
    }
//lấy danh sách
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") long orderId){
        List<OrderDetail>orderDetails=orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse>orderDetailResponses= orderDetails
                .stream().map(orderDetail ->OrderDetailResponse.fromOrderDetail(orderDetail)).toList();

        return ResponseEntity.ok().body(orderDetailResponses);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);

        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") long id
          ){
        orderDetailService.deleteById(id);
        return ResponseEntity.ok().body("delete order detail complete with id:"+id);
//
    }
}
