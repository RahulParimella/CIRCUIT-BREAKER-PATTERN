package com.example.product_service.service;

import com.example.product_service.dto.ProductDto;
import com.example.product_service.model.Product;
import org.springframework.stereotype.Service;
import com.example.product_service.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products and return as DTOs
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get product by id and return as DTO
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        return convertToDto(product);
    }

    // Create a new product from DTO
    public ProductDto createProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    // Update existing product using DTO
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        existing.setName(productDto.getName());
        existing.setPrice(productDto.getPrice());

        Product updated = productRepository.save(existing);
        return convertToDto(updated);
    }

    // Delete product by id
    public void deleteProduct(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        productRepository.delete(existing);
    }

    // Helper method: convert entity → DTO
    private ProductDto convertToDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    // Helper method: convert DTO → entity
    private Product convertToEntity(ProductDto dto) {
        return new Product(dto.getId(), dto.getName(), dto.getPrice());
    }
}
