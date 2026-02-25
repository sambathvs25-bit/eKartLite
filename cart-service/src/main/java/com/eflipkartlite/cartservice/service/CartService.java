package com.eflipkartlite.cartservice.service;

import com.eflipkartlite.cartservice.dto.*;
import com.eflipkartlite.cartservice.entity.Cart;
import com.eflipkartlite.cartservice.entity.CartItem;
import com.eflipkartlite.cartservice.exception.BusinessException;
import com.eflipkartlite.cartservice.repository.CartItemRepository;
import com.eflipkartlite.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        Cart cart = cartRepository.findByCustomerEmail(email).orElseGet(() -> {
            Cart c = new Cart();
            c.setCustomerEmail(email);
            return cartRepository.save(c);
        });

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(request.getProductId());
        item.setProductName(request.getProductName());
        item.setPrice(request.getPrice());
        item.setQuantity(request.getQuantity());
        cart.getItems().add(item);

        cartRepository.save(cart);
        return viewCart(email);
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByCustomerEmail(email).orElseThrow(() -> new BusinessException("Cart not found"));
        CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Item does not belong to customer cart");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return viewCart(email);
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = cartRepository.findByCustomerEmail(email).orElseThrow(() -> new BusinessException("Cart not found"));
        CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Item does not belong to customer cart");
        }
        cartItemRepository.delete(item);
        return viewCart(email);
    }

    @Transactional(readOnly = true)
    public CartResponse viewCart(String email) {
        Cart cart = cartRepository.findByCustomerEmail(email).orElseGet(() -> {
            Cart c = new Cart();
            c.setCustomerEmail(email);
            return cartRepository.save(c);
        });
        var items = cart.getItems().stream().map(i -> new CartItemResponse(
                i.getId(),
                i.getProductId(),
                i.getProductName(),
                i.getPrice(),
                i.getQuantity(),
                i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
        )).toList();
        BigDecimal total = items.stream().map(CartItemResponse::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), cart.getCustomerEmail(), items, total);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = cartRepository.findByCustomerEmail(email).orElseThrow(() -> new BusinessException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
