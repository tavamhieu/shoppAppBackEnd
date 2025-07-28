package com.example.shopapppro.controller;

import com.example.shopapppro.dtos.OrderDTO;
import com.example.shopapppro.dtos.UserDTO;
import com.example.shopapppro.models.Order;
import com.example.shopapppro.responces.OrderResponse;
import com.example.shopapppro.service.Order.IOrderService;
import com.example.shopapppro.service.Order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result){

        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Order orderResponse = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/{user_id}") // Thêm biến đường dẫn "user_id"
    //GET http://localhost:8089/api/v1/orders/user/4
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId) {
        try {
            List<Order>orders = orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}") // Thêm biến đường dẫn "user_id"
    //GET http://localhost:8089/api/v1/orders/4
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long orderId) {
        try {
            Order existingOrder =orderService.getOrder(orderId);
            return ResponseEntity.ok(existingOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //PUT http://localhost:8089/api/v1/orders/2
    //công việc của admin
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable long id,
            @Valid @RequestBody OrderDTO orderDTO) {

        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrder(
            @Valid @PathVariable Long id) {
//        xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
//        String result = localizationUtils.getLocalizedMessage(
//                MessageKeys.DELETE_ORDER_SUCCESSFULLY, id);
        return ResponseEntity.ok().body("OK DELETE OK");
    }
}
