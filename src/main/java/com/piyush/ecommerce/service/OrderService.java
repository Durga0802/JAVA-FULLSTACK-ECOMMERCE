package com.piyush.ecommerce.service;

import com.piyush.ecommerce.dto.*;
import com.piyush.ecommerce.entity.*;
import com.piyush.ecommerce.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Order placeOrder(OrderRequestDTO dto) {

        // 1. User check (Message ke saath)
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Bhai, ye User ID (" + dto.getUserId() + ") database mein nahi mili!"));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (OrderItemDTO itemDTO : dto.getItems()) {

            // 2. Product check (Yahan sabse zyada chance hai fail hone ka)
            Product product = productRepository
                    .findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product nahi mila! ID check kar: " + itemDTO.getProductId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);

            total += product.getPrice() * itemDTO.getQuantity();
            items.add(item);
        }

        order.setItems(items);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }
    // OrderService.java mein
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setTotalPrice(order.getTotalPrice());

            // Agar user null nahi hai toh uska email nikal lo
            if (order.getUser() != null) {
                dto.setUserEmail(order.getUser().getEmail());
            } else {
                dto.setUserEmail("Guest User");
            }

            // Status abhi ke liye manually set kar dete hain agar entity mein nahi hai
            dto.setStatus("COMPLETED");

            return dto;
        }).collect(Collectors.toList());
    }
}