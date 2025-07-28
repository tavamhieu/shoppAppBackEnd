package com.example.shopapppro.service.Order;

import com.example.shopapppro.dtos.OrderDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.Order;
import com.example.shopapppro.models.OrderStatus;
import com.example.shopapppro.models.User;
import com.example.shopapppro.repository.OrderRepository;
import com.example.shopapppro.repository.UserRepository;
import com.example.shopapppro.responces.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private  final UserRepository userRepository;
    private  final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
//       kiểm tra xem user id có tồn tại không
       User user=userRepository.findById(orderDTO.getUserId())
               .orElseThrow(()->new DataNotFoundException("can find user with id"+orderDTO.getUserId()));

//chuyển đổi oderDTO sang Oder để chèn vào DB
        //dùng thư viện model mapper
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId));
        Order order= new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
//        để dk shipping date cho ợp lý bắt bược phải > ngày hôm nay
//        Date shippingDate = orderDTO.getShippingDate() == null
//                ? LocalDate.now() : orderDTO.getShippingDate();
//        if(shippingDate == null || shippingDate.before(new Date())){
//            throw  new DataNotFoundException(" data must be least today");
//        }
        //Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);

//        ánh sạ từ oder qua orderResponse
       return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order= orderRepository
                .findById(id).orElseThrow(()->new DataNotFoundException("can not find by order with id"+id));
        User existingUser= userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("can not find by user with id"+id));
//        map  tk dto sang oder
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId));
//        cập nhặp các trường của đơn hàng từ orderdto
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
