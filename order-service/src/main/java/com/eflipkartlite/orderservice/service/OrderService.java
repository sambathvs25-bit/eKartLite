package com.eflipkartlite.orderservice.service;

import com.eflipkartlite.orderservice.client.PaymentRequest;
import com.eflipkartlite.orderservice.dto.*;
import com.eflipkartlite.orderservice.entity.Order;
import com.eflipkartlite.orderservice.entity.OrderItem;
import com.eflipkartlite.orderservice.entity.OrderStatus;
import com.eflipkartlite.orderservice.exception.BusinessException;
import com.eflipkartlite.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    
    public OrderService(OrderRepository orderRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerEmail(email);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getProductId());
            item.setProductName(itemReq.getProductName());
            item.setPrice(itemReq.getPrice());
            item.setQuantity(itemReq.getQuantity());
            order.getItems().add(item);
            total = total.add(itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }
        order.setTotalAmount(total);

        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public PaymentResultResponse payOrder(String email, Long orderId, PayOrderRequest request, String authHeader) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Order not found"));
        if (!order.getCustomerEmail().equals(email)) {
            throw new BusinessException("Order does not belong to customer");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Only CREATED orders can be paid");
        }

        var paymentResponse = paymentClient.processPayment(new PaymentRequest(order.getId(), order.getTotalAmount(), request.getMethod()), authHeader);
        if (!paymentResponse.isSuccess()) {
            return new PaymentResultResponse(order.getId(), false, paymentResponse.getTransactionReference(), order.getStatus(),
                    "Payment service unavailable. Please retry.");
        }

        order.setStatus(OrderStatus.PAID);
        order.setStatus(OrderStatus.READY_FOR_SHIPPING);
        orderRepository.save(order);

        return new PaymentResultResponse(order.getId(), true, paymentResponse.getTransactionReference(), order.getStatus(),
                "Payment successful. Order ready for shipping");
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> customerOrders(String email) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> readyForShipping() {
        return orderRepository.findByStatus(OrderStatus.READY_FOR_SHIPPING).stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream().map(i -> new OrderResponse.OrderItemResponse(
                        i.getProductId(), i.getProductName(), i.getPrice(), i.getQuantity())).toList());
    }
}
