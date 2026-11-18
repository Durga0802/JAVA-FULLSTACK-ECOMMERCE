package com.piyush.ecommerce.controller;

import com.piyush.ecommerce.dto.OrderItemDTO;
import com.piyush.ecommerce.dto.OrderRequestDTO;
import com.piyush.ecommerce.dto.OrderResponseDTO;
import com.piyush.ecommerce.entity.Order;
import com.piyush.ecommerce.repository.OrderRepository;
import com.piyush.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService,OrderRepository orderRepository){
        this.orderService = orderService;
        this.orderRepository=orderRepository;
    }

    @PostMapping("/place/{productId}")
    public ResponseEntity<?> placeOrder(@PathVariable Long productId) {
        try {
            OrderRequestDTO dto = new OrderRequestDTO();
            // Maan lo abhi ke liye userId 1 hai (kyunki index se login detail nahi mil rahi)
            dto.setUserId(5L);

            // Ek naya Item DTO banao aur list mein daal do
            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(productId);
            item.setQuantity(1); // Default 1 quantity

            dto.setItems(java.util.List.of(item)); // List bana ke set kar di

            Order order = orderService.placeOrder(dto);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Asli Error: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/count")
    public long getOrderCount() {
        return orderRepository.count();
    }
}