package com.example.order_service.service;


import com.example.order_service.Client.ProductClient;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.dto.ProductDto;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ProductClient productClient;

    private OrderRepository orderRepository;

    public OrderService(ProductClient productClient,OrderRepository orderRepository) {
        this.productClient = productClient;
        this.orderRepository=orderRepository;
    }


    public List<OrderDto> getAllOrders(){
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Create a new product from DTO
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = convertToEntity(orderDto);
        Order savedProduct = orderRepository.save(order);
        return convertToDto(savedProduct);
    }

    // Update existing product using DTO
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        existing.setProduct(orderDto.getProductName());
        existing.setQuantity(orderDto.getQuantity());

        Order updated = orderRepository.save(existing);
        return convertToDto(updated);
    }

    // Delete product by id
    public void deleteOrder(Long id) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        orderRepository.delete(existing);
    }

    // Get order by id
    public OrderDto getOrderById(Long id) {

        ProductDto productDto = productClient.getProductById(id);
        return new OrderDto(id, productDto.getName(), 2);
    }

    // Fallback method for circuit breaker
    public OrderDto fallbackGetOrderById(Long id, Throwable throwable) {
        return new OrderDto(id, "Fallback Product", 0);
    }

    private OrderDto convertToDto(Order order) {
        return new OrderDto(order.getId(),order.getProduct(),order.getQuantity());
    }
    private Order convertToEntity(OrderDto dto) {
        return new Order(dto.getId(), dto.getProductName(), dto.getQuantity());
    }
}
