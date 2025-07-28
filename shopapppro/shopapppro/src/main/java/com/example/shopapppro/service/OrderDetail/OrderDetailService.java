package com.example.shopapppro.service.OrderDetail;

import com.example.shopapppro.dtos.OrderDetailDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.Order;
import com.example.shopapppro.models.OrderDetail;
import com.example.shopapppro.models.Product;
import com.example.shopapppro.repository.OrderDetailRepository;
import com.example.shopapppro.repository.OrderRepository;
import com.example.shopapppro.repository.ProductRepository;
import com.example.shopapppro.responces.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService{
    private  final OrderRepository orderRepository;
    private  final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
//        // tìm xem id của tk product và order này có toonff tại hay không
        Order order= orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                ()-> new DataNotFoundException("can not find oder with id"+orderDetailDTO.getOrderId()));

        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                ()-> new DataNotFoundException("can not find product with id"+orderDetailDTO.getProductId()));
        OrderDetail orderDetail= OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .price(orderDetailDTO.getPrice())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);

    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("can not find oderdetail with id"+id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        // tìm xem orderdetail có tồn tại hay không
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("can not find order detail with id:"+id));
        // xem orderId thược cái cái order nào không
        Order existingOrder=  orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFoundException("can not find order with id"+id));
        //xem product có tồn tại hay không
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                ()-> new DataNotFoundException("can not find product with id"+orderDetailDTO.getProductId()));
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);

    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
