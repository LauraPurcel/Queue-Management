package org.example.Model;

import java.time.LocalDateTime;

public record Bill(int billId, int orderId, int clientId, int productId,
                   int quantity, double totalPrice, LocalDateTime timestamp) {
}
