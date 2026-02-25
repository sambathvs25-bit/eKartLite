package com.eflipkartlite.cartservice.controller;

import com.eflipkartlite.cartservice.dto.*;
import com.eflipkartlite.cartservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
public class CartController {

    private final CartService service;
    
    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication auth, @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(service.addItem(auth.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(Authentication auth,
                                                   @PathVariable Long itemId,
                                                   @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return ResponseEntity.ok(service.updateItem(auth.getName(), itemId, request.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(Authentication auth, @PathVariable Long itemId) {
        return ResponseEntity.ok(service.removeItem(auth.getName(), itemId));
    }

    @GetMapping
    public ResponseEntity<CartResponse> view(Authentication auth) {
        return ResponseEntity.ok(service.viewCart(auth.getName()));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> clear(Authentication auth) {
        service.clearCart(auth.getName());
        return ResponseEntity.ok(new MessageResponse("Cart cleared"));
    }
}
