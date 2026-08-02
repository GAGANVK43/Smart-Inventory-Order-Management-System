package com.inventory.service.impl;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.request.StockAdjustmentRequestDto;
import com.inventory.dto.response.InventoryMovementResponseDto;
import com.inventory.entity.InventoryMovement;
import com.inventory.entity.Product;
import com.inventory.enums.MovementType;
import com.inventory.exception.OutOfStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.InventoryMovementRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    @Autowired
    public InventoryServiceImpl(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    @Transactional
    public InventoryMovementResponseDto adjustStock(StockAdjustmentRequestDto requestDto, String performedBy) {
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + requestDto.getProductId()));

        int currentQuantity = product.getQuantity();
        int changeQty = requestDto.getQuantity();
        int newQuantity = currentQuantity;

        MovementType type = requestDto.getMovementType();

        if (type == MovementType.STOCK_IN || type == MovementType.RETURN) {
            newQuantity = currentQuantity + changeQty;
        } else if (type == MovementType.STOCK_OUT || type == MovementType.DISCARD) {
            if (currentQuantity < changeQty) {
                throw new OutOfStockException("Cannot adjust stock out by " + changeQty + " units. Only " + currentQuantity + " units available in inventory.");
            }
            newQuantity = currentQuantity - changeQty;
        } else if (type == MovementType.ADJUSTMENT) {
            newQuantity = changeQty; // Direct overwrite
        }

        product.setQuantity(newQuantity);
        productRepository.save(product);

        InventoryMovement movement = new InventoryMovement(
                product,
                type,
                changeQty,
                newQuantity,
                requestDto.getReason(),
                performedBy != null ? performedBy : "SYSTEM"
        );

        InventoryMovement savedMovement = movementRepository.save(movement);
        return mapToResponseDto(savedMovement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.isActive() && p.getQuantity() <= p.getReorderLevel())
                .map(this::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementResponseDto> getMovementHistoryByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementResponseDto> getAllMovementsHistory() {
        return movementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private InventoryMovementResponseDto mapToResponseDto(InventoryMovement movement) {
        return new InventoryMovementResponseDto(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getProduct().getSku(),
                movement.getMovementType(),
                movement.getQuantityChanged(),
                movement.getStockAfter(),
                movement.getReason(),
                movement.getPerformedBy(),
                movement.getCreatedAt()
        );
    }

    private ProductResponseDto mapToProductResponseDto(Product product) {
        boolean isLowStock = product.getQuantity() <= product.getReorderLevel();
        return new ProductResponseDto(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getReorderLevel(),
                product.isActive(),
                isLowStock,
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getSupplier().getId(),
                product.getSupplier().getName()
        );
    }
}
