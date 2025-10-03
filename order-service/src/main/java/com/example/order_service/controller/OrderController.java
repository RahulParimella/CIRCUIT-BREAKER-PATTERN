package com.example.order_service.controller;

import com.example.order_service.Client.ProductClient;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.dto.ProductDto;
import com.example.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductClient productClient;

    public OrderController(OrderService orderService, ProductClient productClient) {
        this.orderService = orderService;
        this.productClient = productClient;
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackGetProductById")
    public OrderDto getOrderById(@PathVariable Long id) {
//        if (true) throw new RuntimeException("Simulated failure");
        ProductDto product = productClient.getProductById(id);
        return new OrderDto(id, product.getName(), 3);
    }
    @PostMapping
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    @PutMapping("/{id}")
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(id, orderDto);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }

    public OrderDto fallbackGetProductById(Long id, Throwable throwable) {
        System.out.println("Fallback triggered for order id: " + id + " -> reason: " + throwable.getMessage());
        return new OrderDto(id, "Fallback Product", 1);
    }
}
