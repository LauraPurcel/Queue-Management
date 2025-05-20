package org.example.Model;

import java.time.LocalDateTime;
/**
 * Represents a bill (invoice) generated for an order.
 * <p>
 * This is a Java {@code record}. Records are immutable and are used to model
 * plain data aggregates.
 * <p>
 * The {@code Bill} record contains the following components:
 * <ul>
 *   <li>{@code orderId} – ID of the related order</li>
 *   <li>{@code clientId} – ID of the client who placed the order</li>
 *   <li>{@code productId} – ID of the product ordered</li>
 *   <li>{@code quantity} – Quantity of the product ordered</li>
 *   <li>{@code totalPrice} – Total price calculated for the order</li>
 *   <li>{@code timestamp} – The date and time when the bill was issued</li>
 * </ul>
 */
public record Bill(int orderId, int clientId, int productId,
                   int quantity, double totalPrice, LocalDateTime timestamp) {
}
