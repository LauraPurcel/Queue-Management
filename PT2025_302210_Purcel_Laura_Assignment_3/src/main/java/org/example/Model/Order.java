package org.example.Model;

import java.time.LocalDateTime;
/**
 * Represents an order placed by a client for a specific product at a given time.
 */
public class Order {
    private int id;
    private int clientId;
    private int productId;
    private LocalDateTime orderDate;
    private int quantity;
    public Order(){

    }
    public Order(int id, int clientId, int productId, LocalDateTime orderDate, int quantity) {
        super();
        this.id = id;
        this.clientId = clientId;
        this.productId = productId;
        this.orderDate = orderDate;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", productId=" + productId +
                ", orderDate=" + orderDate +
                ", quantity=" + quantity +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
